package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Marker MARKER_NETWORK = MarkerManager.getMarker("NETWORK");
	public static final Marker MARKER_NETWORK_PACKETS = MarkerManager.getMarker("NETWORK_PACKETS", MARKER_NETWORK);
	public static final AttributeKey<NetworkState> ATTR_KEY_PROTOCOL = AttributeKey.valueOf("protocol");
	public static final Lazy<NioEventLoopGroup> CLIENT_IO_GROUP = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> CLIENT_IO_GROUP_EPOLL = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<DefaultEventLoopGroup> CLIENT_IO_GROUP_LOCAL = new Lazy<>(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	private final NetworkSide side;
	private final Queue<ClientConnection.PacketWrapper> packetQueue = Queues.<ClientConnection.PacketWrapper>newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Channel channel;
	private SocketAddress address;
	private PacketListener packetListener;
	private Text field_11660;
	private boolean encrypted;
	private boolean disconnected;
	private int packetsReceivedCounter;
	private int packetsSentCounter;
	private float avgPacketsReceived;
	private float avgPacketsSent;
	private int ticks;
	private boolean field_11640;

	public ClientConnection(NetworkSide networkSide) {
		this.side = networkSide;
	}

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		super.channelActive(channelHandlerContext);
		this.channel = channelHandlerContext.channel();
		this.address = this.channel.remoteAddress();

		try {
			this.setState(NetworkState.HANDSHAKING);
		} catch (Throwable var3) {
			LOGGER.fatal(var3);
		}
	}

	public void setState(NetworkState networkState) {
		this.channel.attr(ATTR_KEY_PROTOCOL).set(networkState);
		this.channel.config().setAutoRead(true);
		LOGGER.debug("Enabled auto read");
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
		this.method_10747(new TranslatableText("disconnect.endOfStream"));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
		if (throwable instanceof PacketEncoderException) {
			LOGGER.debug("Skipping packet due to errors", throwable.getCause());
		} else {
			boolean bl = !this.field_11640;
			this.field_11640 = true;
			if (this.channel.isOpen()) {
				if (throwable instanceof TimeoutException) {
					LOGGER.debug("Timeout", throwable);
					this.method_10747(new TranslatableText("disconnect.timeout"));
				} else {
					Text text = new TranslatableText("disconnect.genericReason", "Internal Exception: " + throwable);
					if (bl) {
						LOGGER.debug("Failed to sent packet", throwable);
						this.send(new DisconnectS2CPacket(text), future -> this.method_10747(text));
						this.disableAutoRead();
					} else {
						LOGGER.debug("Double fault", throwable);
						this.method_10747(text);
					}
				}
			}
		}
	}

	protected void method_10770(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				handlePacket(packet, this.packetListener);
			} catch (OffThreadException var4) {
			}

			this.packetsReceivedCounter++;
		}
	}

	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener packetListener) {
		packet.apply((T)packetListener);
	}

	public void setPacketListener(PacketListener packetListener) {
		Validate.notNull(packetListener, "packetListener");
		LOGGER.debug("Set listener of {} to {}", this, packetListener);
		this.packetListener = packetListener;
	}

	public void send(Packet<?> packet) {
		this.send(packet, null);
	}

	public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (this.isOpen()) {
			this.sendQueuedPackets();
			this.sendImmediately(packet, genericFutureListener);
		} else {
			this.lock.writeLock().lock();

			try {
				this.packetQueue.add(new ClientConnection.PacketWrapper(packet, genericFutureListener));
			} finally {
				this.lock.writeLock().unlock();
			}
		}
	}

	private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		NetworkState networkState = NetworkState.getPacketHandlerState(packet);
		NetworkState networkState2 = this.channel.attr(ATTR_KEY_PROTOCOL).get();
		this.packetsSentCounter++;
		if (networkState2 != networkState) {
			LOGGER.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			if (networkState != networkState2) {
				this.setState(networkState);
			}

			ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
			if (genericFutureListener != null) {
				channelFuture.addListener(genericFutureListener);
			}

			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(() -> {
				if (networkState != networkState2) {
					this.setState(networkState);
				}

				ChannelFuture channelFuturex = this.channel.writeAndFlush(packet);
				if (genericFutureListener != null) {
					channelFuturex.addListener(genericFutureListener);
				}

				channelFuturex.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			});
		}
	}

	private void sendQueuedPackets() {
		if (this.channel != null && this.channel.isOpen()) {
			this.lock.readLock().lock();

			try {
				while (!this.packetQueue.isEmpty()) {
					ClientConnection.PacketWrapper packetWrapper = (ClientConnection.PacketWrapper)this.packetQueue.poll();
					this.sendImmediately(packetWrapper.packet, packetWrapper.listener);
				}
			} finally {
				this.lock.readLock().unlock();
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

		if (this.channel != null) {
			this.channel.flush();
		}

		if (this.ticks++ % 20 == 0) {
			this.avgPacketsSent = this.avgPacketsSent * 0.75F + (float)this.packetsSentCounter * 0.25F;
			this.avgPacketsReceived = this.avgPacketsReceived * 0.75F + (float)this.packetsReceivedCounter * 0.25F;
			this.packetsSentCounter = 0;
			this.packetsReceivedCounter = 0;
		}
	}

	public SocketAddress getAddress() {
		return this.address;
	}

	public void method_10747(Text text) {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
			this.field_11660 = text;
		}
	}

	public boolean isLocal() {
		return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
	}

	@Environment(EnvType.CLIENT)
	public static ClientConnection connect(InetAddress inetAddress, int i, boolean bl) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.field_11942);
		Class<? extends SocketChannel> class_;
		Lazy<? extends EventLoopGroup> lazy;
		if (Epoll.isAvailable() && bl) {
			class_ = EpollSocketChannel.class;
			lazy = CLIENT_IO_GROUP_EPOLL;
		} else {
			class_ = NioSocketChannel.class;
			lazy = CLIENT_IO_GROUP;
		}

		new Bootstrap()
			.group(lazy.get())
			.handler(
				new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel channel) throws Exception {
						try {
							channel.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException var3) {
						}

						channel.pipeline()
							.addLast("timeout", new ReadTimeoutHandler(30))
							.addLast("splitter", new SplitterHandler())
							.addLast("decoder", new DecoderHandler(NetworkSide.field_11942))
							.addLast("prepender", new SizePrepender())
							.addLast("encoder", new PacketEncoder(NetworkSide.field_11941))
							.addLast("packet_handler", clientConnection);
					}
				}
			)
			.channel(class_)
			.connect(inetAddress, i)
			.syncUninterruptibly();
		return clientConnection;
	}

	@Environment(EnvType.CLIENT)
	public static ClientConnection connect(SocketAddress socketAddress) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.field_11942);
		new Bootstrap().group(CLIENT_IO_GROUP_LOCAL.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast("packet_handler", clientConnection);
			}
		}).channel(LocalChannel.class).connect(socketAddress).syncUninterruptibly();
		return clientConnection;
	}

	public void setupEncryption(SecretKey secretKey) {
		this.encrypted = true;
		this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(NetworkEncryptionUtils.cipherFromKey(2, secretKey)));
		this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(NetworkEncryptionUtils.cipherFromKey(1, secretKey)));
	}

	@Environment(EnvType.CLIENT)
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
	public Text method_10748() {
		return this.field_11660;
	}

	public void disableAutoRead() {
		this.channel.config().setAutoRead(false);
	}

	public void setMinCompressedSize(int i) {
		if (i >= 0) {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(i);
			} else {
				this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(i));
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(i);
			} else {
				this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(i));
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
				if (this.method_10748() != null) {
					this.getPacketListener().method_10839(this.method_10748());
				} else if (this.getPacketListener() != null) {
					this.getPacketListener().method_10839(new TranslatableText("multiplayer.disconnect.generic"));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float getAveragePacketsReceived() {
		return this.avgPacketsReceived;
	}

	@Environment(EnvType.CLIENT)
	public float getAveragePacketsSent() {
		return this.avgPacketsSent;
	}

	static class PacketWrapper {
		private final Packet<?> packet;
		@Nullable
		private final GenericFutureListener<? extends Future<? super Void>> listener;

		public PacketWrapper(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
			this.packet = packet;
			this.listener = genericFutureListener;
		}
	}
}
