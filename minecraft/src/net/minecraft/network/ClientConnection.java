package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A connection backed by a netty channel. It can be one to a client on the
 * server or one to a server on a client.
 */
public class ClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	/**
	 * Represents when the average packet counter is updated, what percent of the
	 * value of the average counter is set from the current counter.
	 * 
	 * <p>The formula is {@link #averagePacketsSent averagePacketsSent} = {@value}
	 * &times; {@link #packetsSentCounter packetsSentCounter} + (1 - {@value}) &times;
	 * {@code averagePacketsSent}.
	 */
	private static final float CURRENT_PACKET_COUNTER_WEIGHT = 0.75F;
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Marker NETWORK_MARKER = MarkerFactory.getMarker("NETWORK");
	public static final Marker NETWORK_PACKETS_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), marker -> marker.add(NETWORK_MARKER));
	public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), marker -> marker.add(NETWORK_PACKETS_MARKER));
	public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), marker -> marker.add(NETWORK_PACKETS_MARKER));
	/**
	 * The attribute key for the current network state of the backing netty
	 * channel.
	 */
	public static final AttributeKey<NetworkState> PROTOCOL_ATTRIBUTE_KEY = AttributeKey.valueOf("protocol");
	public static final Lazy<NioEventLoopGroup> CLIENT_IO_GROUP = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> EPOLL_CLIENT_IO_GROUP = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<DefaultEventLoopGroup> LOCAL_CLIENT_IO_GROUP = new Lazy<>(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	/**
	 * The side this connection is to.
	 */
	private final NetworkSide side;
	private final Queue<ClientConnection.QueuedPacket> packetQueue = Queues.<ClientConnection.QueuedPacket>newConcurrentLinkedQueue();
	private Channel channel;
	private SocketAddress address;
	private PacketListener packetListener;
	private Text disconnectReason;
	private boolean encrypted;
	private boolean disconnected;
	private int packetsReceivedCounter;
	private int packetsSentCounter;
	private float averagePacketsReceived;
	private float averagePacketsSent;
	private int ticks;
	private boolean errored;

	public ClientConnection(NetworkSide side) {
		this.side = side;
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		super.channelActive(context);
		this.channel = context.channel();
		this.address = this.channel.remoteAddress();

		try {
			this.setState(NetworkState.HANDSHAKING);
		} catch (Throwable var3) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Failed to change protocol to handshake", var3);
		}
	}

	public void setState(NetworkState state) {
		this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).set(state);
		this.channel.config().setAutoRead(true);
		LOGGER.debug("Enabled auto read");
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) {
		this.disconnect(new TranslatableText("disconnect.endOfStream"));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) {
		if (ex instanceof PacketEncoderException) {
			LOGGER.debug("Skipping packet due to errors", ex.getCause());
		} else {
			boolean bl = !this.errored;
			this.errored = true;
			if (this.channel.isOpen()) {
				if (ex instanceof TimeoutException) {
					LOGGER.debug("Timeout", ex);
					this.disconnect(new TranslatableText("disconnect.timeout"));
				} else {
					Text text = new TranslatableText("disconnect.genericReason", "Internal Exception: " + ex);
					if (bl) {
						LOGGER.debug("Failed to sent packet", ex);
						NetworkState networkState = this.getState();
						Packet<?> packet = (Packet<?>)(networkState == NetworkState.LOGIN ? new LoginDisconnectS2CPacket(text) : new DisconnectS2CPacket(text));
						this.send(packet, future -> this.disconnect(text));
						this.disableAutoRead();
					} else {
						LOGGER.debug("Double fault", ex);
						this.disconnect(text);
					}
				}
			}
		}
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
		if (this.channel.isOpen()) {
			try {
				handlePacket(packet, this.packetListener);
			} catch (OffThreadException var4) {
			} catch (RejectedExecutionException var5) {
				this.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown"));
			} catch (ClassCastException var6) {
				LOGGER.error("Received {} that couldn't be processed", packet.getClass(), var6);
				this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_packet"));
			}

			this.packetsReceivedCounter++;
		}
	}

	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
		packet.apply((T)listener);
	}

	/**
	 * Sets the packet listener that will handle oncoming packets, including
	 * ones that are not yet handled by the current packet listener.
	 * 
	 * @apiNote This may be called from the {@linkplain #packetListener} stored
	 * in this connection.
	 */
	public void setPacketListener(PacketListener listener) {
		Validate.notNull(listener, "packetListener");
		this.packetListener = listener;
	}

	public void send(Packet<?> packet) {
		this.send(packet, null);
	}

	public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
		if (this.isOpen()) {
			this.sendQueuedPackets();
			this.sendImmediately(packet, callback);
		} else {
			this.packetQueue.add(new ClientConnection.QueuedPacket(packet, callback));
		}
	}

	private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
		NetworkState networkState = NetworkState.getPacketHandlerState(packet);
		NetworkState networkState2 = this.getState();
		this.packetsSentCounter++;
		if (networkState2 != networkState) {
			LOGGER.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			this.sendInternal(packet, callback, networkState, networkState2);
		} else {
			this.channel.eventLoop().execute(() -> this.sendInternal(packet, callback, networkState, networkState2));
		}
	}

	private void sendInternal(
		Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback, NetworkState packetState, NetworkState currentState
	) {
		if (packetState != currentState) {
			this.setState(packetState);
		}

		ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
		if (callback != null) {
			channelFuture.addListener(callback);
		}

		channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	/**
	 * Returns the current network state of this connection.
	 */
	private NetworkState getState() {
		return this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).get();
	}

	private void sendQueuedPackets() {
		if (this.channel != null && this.channel.isOpen()) {
			synchronized (this.packetQueue) {
				ClientConnection.QueuedPacket queuedPacket;
				while ((queuedPacket = (ClientConnection.QueuedPacket)this.packetQueue.poll()) != null) {
					this.sendImmediately(queuedPacket.packet, queuedPacket.callback);
				}
			}
		}
	}

	public void tick() {
		this.sendQueuedPackets();
		if (this.packetListener instanceof ServerLoginNetworkHandler) {
			((ServerLoginNetworkHandler)this.packetListener).tick();
		}

		if (this.packetListener instanceof ServerPlayNetworkHandler) {
			((ServerPlayNetworkHandler)this.packetListener).tick();
		}

		if (!this.isOpen() && !this.disconnected) {
			this.handleDisconnection();
		}

		if (this.channel != null) {
			this.channel.flush();
		}

		if (this.ticks++ % 20 == 0) {
			this.updateStats();
		}
	}

	protected void updateStats() {
		this.averagePacketsSent = MathHelper.lerp(0.75F, (float)this.packetsSentCounter, this.averagePacketsSent);
		this.averagePacketsReceived = MathHelper.lerp(0.75F, (float)this.packetsReceivedCounter, this.averagePacketsReceived);
		this.packetsSentCounter = 0;
		this.packetsReceivedCounter = 0;
	}

	public SocketAddress getAddress() {
		return this.address;
	}

	public void disconnect(Text disconnectReason) {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
			this.disconnectReason = disconnectReason;
		}
	}

	public boolean isLocal() {
		return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
	}

	/**
	 * Returns the side of this connection, or the direction of the packets received
	 * by this connection.
	 */
	public NetworkSide getSide() {
		return this.side;
	}

	/**
	 * Returns the opposite side of this connection, or the direction of the packets
	 * sent by this connection.
	 */
	public NetworkSide getOppositeSide() {
		return this.side.getOpposite();
	}

	public static ClientConnection connect(InetSocketAddress address, boolean useEpoll) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		Class<? extends SocketChannel> class_;
		Lazy<? extends EventLoopGroup> lazy;
		if (Epoll.isAvailable() && useEpoll) {
			class_ = EpollSocketChannel.class;
			lazy = EPOLL_CLIENT_IO_GROUP;
		} else {
			class_ = NioSocketChannel.class;
			lazy = CLIENT_IO_GROUP;
		}

		new Bootstrap()
			.group(lazy.get())
			.handler(
				new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel channel) {
						try {
							channel.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException var3) {
						}

						channel.pipeline()
							.addLast("timeout", new ReadTimeoutHandler(30))
							.addLast("splitter", new SplitterHandler())
							.addLast("decoder", new DecoderHandler(NetworkSide.CLIENTBOUND))
							.addLast("prepender", new SizePrepender())
							.addLast("encoder", new PacketEncoder(NetworkSide.SERVERBOUND))
							.addLast("packet_handler", clientConnection);
					}
				}
			)
			.channel(class_)
			.connect(address.getAddress(), address.getPort())
			.syncUninterruptibly();
		return clientConnection;
	}

	public static ClientConnection connectLocal(SocketAddress address) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		new Bootstrap().group(LOCAL_CLIENT_IO_GROUP.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				channel.pipeline().addLast("packet_handler", clientConnection);
			}
		}).channel(LocalChannel.class).connect(address).syncUninterruptibly();
		return clientConnection;
	}

	public void setupEncryption(Cipher decryptionCipher, Cipher encryptionCipher) {
		this.encrypted = true;
		this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(decryptionCipher));
		this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(encryptionCipher));
	}

	public boolean isEncrypted() {
		return this.encrypted;
	}

	public boolean isOpen() {
		return this.channel != null && this.channel.isOpen();
	}

	public boolean hasChannel() {
		return this.channel == null;
	}

	public PacketListener getPacketListener() {
		return this.packetListener;
	}

	@Nullable
	public Text getDisconnectReason() {
		return this.disconnectReason;
	}

	public void disableAutoRead() {
		this.channel.config().setAutoRead(false);
	}

	/**
	 * Sets the compression threshold of this connection.
	 * 
	 * <p>Packets over the threshold in size will be written as a {@code 0}
	 * byte followed by contents, while compressed ones will be written as
	 * a var int for the decompressed size followed by the compressed contents.
	 * 
	 * <p>The connections on the two sides must have the same compression
	 * threshold, or compression errors may result.
	 * 
	 * @param compressionThreshold the compression threshold, in number of bytes
	 * @param rejectsBadPackets whether this connection may abort if a compressed packet with a bad size is received
	 */
	public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
		if (compressionThreshold >= 0) {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(compressionThreshold, rejectsBadPackets);
			} else {
				this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(compressionThreshold, rejectsBadPackets));
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(compressionThreshold);
			} else {
				this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(compressionThreshold));
			}
		} else {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				this.channel.pipeline().remove("decompress");
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				this.channel.pipeline().remove("compress");
			}
		}
	}

	public void handleDisconnection() {
		if (this.channel != null && !this.channel.isOpen()) {
			if (this.disconnected) {
				LOGGER.warn("handleDisconnection() called twice");
			} else {
				this.disconnected = true;
				if (this.getDisconnectReason() != null) {
					this.getPacketListener().onDisconnected(this.getDisconnectReason());
				} else if (this.getPacketListener() != null) {
					this.getPacketListener().onDisconnected(new TranslatableText("multiplayer.disconnect.generic"));
				}
			}
		}
	}

	public float getAveragePacketsReceived() {
		return this.averagePacketsReceived;
	}

	public float getAveragePacketsSent() {
		return this.averagePacketsSent;
	}

	static class QueuedPacket {
		final Packet<?> packet;
		@Nullable
		final GenericFutureListener<? extends Future<? super Void>> callback;

		public QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
			this.packet = packet;
			this.callback = callback;
		}
	}
}
