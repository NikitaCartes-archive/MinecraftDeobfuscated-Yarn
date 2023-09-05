package net.minecraft.network;

import com.google.common.base.Suppliers;
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
import io.netty.channel.ChannelPipeline;
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
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.handler.PacketBundler;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketEncoder;
import net.minecraft.network.handler.PacketEncoderException;
import net.minecraft.network.handler.PacketInflater;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.handler.PacketUnbundler;
import net.minecraft.network.handler.PacketValidator;
import net.minecraft.network.handler.SizePrepender;
import net.minecraft.network.handler.SplitterHandler;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.PerformanceLog;
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
	public static final AttributeKey<NetworkState.PacketHandler<?>> SERVERBOUND_PROTOCOL_KEY = AttributeKey.valueOf("serverbound_protocol");
	public static final AttributeKey<NetworkState.PacketHandler<?>> CLIENTBOUND_PROTOCOL_KEY = AttributeKey.valueOf("clientbound_protocol");
	public static final Supplier<NioEventLoopGroup> CLIENT_IO_GROUP = Suppliers.memoize(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final Supplier<EpollEventLoopGroup> EPOLL_CLIENT_IO_GROUP = Suppliers.memoize(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final Supplier<DefaultEventLoopGroup> LOCAL_CLIENT_IO_GROUP = Suppliers.memoize(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	/**
	 * The side this connection is to.
	 */
	private final NetworkSide side;
	private final Queue<Consumer<ClientConnection>> queuedTasks = Queues.<Consumer<ClientConnection>>newConcurrentLinkedQueue();
	private Channel channel;
	private SocketAddress address;
	@Nullable
	private volatile PacketListener prePlayStateListener;
	@Nullable
	private volatile PacketListener packetListener;
	@Nullable
	private Text disconnectReason;
	private boolean encrypted;
	private boolean disconnected;
	private int packetsReceivedCounter;
	private int packetsSentCounter;
	private float averagePacketsReceived;
	private float averagePacketsSent;
	private int ticks;
	private boolean errored;
	@Nullable
	private volatile Text pendingDisconnectionReason;
	@Nullable
	PacketSizeLogger packetSizeLogger;

	public ClientConnection(NetworkSide side) {
		this.side = side;
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		super.channelActive(context);
		this.channel = context.channel();
		this.address = this.channel.remoteAddress();
		if (this.pendingDisconnectionReason != null) {
			this.disconnect(this.pendingDisconnectionReason);
		}
	}

	public static void setHandlers(Channel channel) {
		channel.attr(SERVERBOUND_PROTOCOL_KEY).set(NetworkState.HANDSHAKING.getHandler(NetworkSide.SERVERBOUND));
		channel.attr(CLIENTBOUND_PROTOCOL_KEY).set(NetworkState.HANDSHAKING.getHandler(NetworkSide.CLIENTBOUND));
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) {
		this.disconnect(Text.translatable("disconnect.endOfStream"));
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
					this.disconnect(Text.translatable("disconnect.timeout"));
				} else {
					Text text = Text.translatable("disconnect.genericReason", "Internal Exception: " + ex);
					if (bl) {
						LOGGER.debug("Failed to sent packet", ex);
						if (this.getOppositeSide() == NetworkSide.CLIENTBOUND) {
							NetworkState networkState = this.channel.attr(CLIENTBOUND_PROTOCOL_KEY).get().getState();
							Packet<?> packet = (Packet<?>)(networkState == NetworkState.LOGIN ? new LoginDisconnectS2CPacket(text) : new DisconnectS2CPacket(text));
							this.send(packet, PacketCallbacks.always(() -> this.disconnect(text)));
						} else {
							this.disconnect(text);
						}

						this.tryDisableAutoRead();
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
			PacketListener packetListener = this.packetListener;
			if (packetListener == null) {
				throw new IllegalStateException("Received a packet before the packet listener was initialized");
			} else {
				if (packetListener.accepts(packet)) {
					try {
						handlePacket(packet, packetListener);
					} catch (OffThreadException var5) {
					} catch (RejectedExecutionException var6) {
						this.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
					} catch (ClassCastException var7) {
						LOGGER.error("Received {} that couldn't be processed", packet.getClass(), var7);
						this.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
					}

					this.packetsReceivedCounter++;
				}
			}
		}
	}

	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
		packet.apply((T)listener);
	}

	public void disableAutoRead() {
		this.channel.config().setAutoRead(false);
	}

	public void enableAutoRead() {
		this.channel.config().setAutoRead(true);
	}

	/**
	 * Sets the packet listener that will handle oncoming packets, including
	 * ones that are not yet handled by the current packet listener.
	 * 
	 * @apiNote This may be called from the {@linkplain #packetListener} stored
	 * in this connection.
	 */
	public void setPacketListener(PacketListener packetListener) {
		Validate.notNull(packetListener, "packetListener");
		NetworkSide networkSide = packetListener.getSide();
		if (networkSide != this.side) {
			throw new IllegalStateException("Trying to set listener for wrong side: connection is " + this.side + ", but listener is " + networkSide);
		} else {
			NetworkState networkState = packetListener.getState();
			NetworkState networkState2 = this.channel.attr(getProtocolAttributeKey(networkSide)).get().getState();
			if (networkState2 != networkState) {
				throw new IllegalStateException(
					"Trying to set listener for protocol " + networkState.getId() + ", but current " + networkSide + " protocol is " + networkState2.getId()
				);
			} else {
				this.packetListener = packetListener;
				this.prePlayStateListener = null;
			}
		}
	}

	/**
	 * Sets the initial packet listener.
	 * 
	 * @throws IllegalStateException if the listener was already set
	 * @see #setPacketListener
	 */
	public void setInitialPacketListener(PacketListener packetListener) {
		if (this.packetListener != null) {
			throw new IllegalStateException("Listener already set");
		} else if (this.side == NetworkSide.SERVERBOUND
			&& packetListener.getSide() == NetworkSide.SERVERBOUND
			&& packetListener.getState() == NetworkState.HANDSHAKING) {
			this.packetListener = packetListener;
		} else {
			throw new IllegalStateException("Invalid initial listener");
		}
	}

	public void connect(String address, int port, ClientQueryPacketListener listener) {
		this.connect(address, port, listener, ConnectionIntent.STATUS);
	}

	public void connect(String address, int port, ClientLoginPacketListener listener) {
		this.connect(address, port, listener, ConnectionIntent.LOGIN);
	}

	private void connect(String address, int port, PacketListener listener, ConnectionIntent intent) {
		this.prePlayStateListener = listener;
		this.submit(connection -> {
			connection.setS2CPacketHandler(intent);
			this.setPacketListener(listener);
			connection.sendImmediately(new HandshakeC2SPacket(SharedConstants.getGameVersion().getProtocolVersion(), address, port, intent), null, true);
		});
	}

	public void setS2CPacketHandler(ConnectionIntent intent) {
		this.channel.attr(CLIENTBOUND_PROTOCOL_KEY).set(intent.getState().getHandler(NetworkSide.CLIENTBOUND));
	}

	public void send(Packet<?> packet) {
		this.send(packet, null);
	}

	public void send(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
		this.send(packet, callbacks, true);
	}

	public void send(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush) {
		if (this.isOpen()) {
			this.handleQueuedTasks();
			this.sendImmediately(packet, callbacks, flush);
		} else {
			this.queuedTasks.add((Consumer)connection -> connection.sendImmediately(packet, callbacks, flush));
		}
	}

	public void submit(Consumer<ClientConnection> task) {
		if (this.isOpen()) {
			this.handleQueuedTasks();
			task.accept(this);
		} else {
			this.queuedTasks.add(task);
		}
	}

	private void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush) {
		this.packetsSentCounter++;
		if (this.channel.eventLoop().inEventLoop()) {
			this.sendInternal(packet, callbacks, flush);
		} else {
			this.channel.eventLoop().execute(() -> this.sendInternal(packet, callbacks, flush));
		}
	}

	private void sendInternal(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush) {
		ChannelFuture channelFuture = flush ? this.channel.writeAndFlush(packet) : this.channel.write(packet);
		if (callbacks != null) {
			channelFuture.addListener(future -> {
				if (future.isSuccess()) {
					callbacks.onSuccess();
				} else {
					Packet<?> packetx = callbacks.getFailurePacket();
					if (packetx != null) {
						ChannelFuture channelFuturex = this.channel.writeAndFlush(packetx);
						channelFuturex.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
					}
				}
			});
		}

		channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void flush() {
		if (this.isOpen()) {
			this.flushInternal();
		} else {
			this.queuedTasks.add(ClientConnection::flushInternal);
		}
	}

	private void flushInternal() {
		if (this.channel.eventLoop().inEventLoop()) {
			this.channel.flush();
		} else {
			this.channel.eventLoop().execute(() -> this.channel.flush());
		}
	}

	private static AttributeKey<NetworkState.PacketHandler<?>> getProtocolAttributeKey(NetworkSide side) {
		return switch (side) {
			case CLIENTBOUND -> CLIENTBOUND_PROTOCOL_KEY;
			case SERVERBOUND -> SERVERBOUND_PROTOCOL_KEY;
		};
	}

	private void handleQueuedTasks() {
		if (this.channel != null && this.channel.isOpen()) {
			synchronized (this.queuedTasks) {
				Consumer<ClientConnection> consumer;
				while ((consumer = (Consumer<ClientConnection>)this.queuedTasks.poll()) != null) {
					consumer.accept(this);
				}
			}
		}
	}

	public void tick() {
		this.handleQueuedTasks();
		if (this.packetListener instanceof TickablePacketListener tickablePacketListener) {
			tickablePacketListener.tick();
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

		if (this.packetSizeLogger != null) {
			this.packetSizeLogger.push();
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

	public String getAddressAsString(boolean logIps) {
		if (this.address == null) {
			return "local";
		} else {
			return logIps ? this.address.toString() : "IP hidden";
		}
	}

	public void disconnect(Text disconnectReason) {
		if (this.channel == null) {
			this.pendingDisconnectionReason = disconnectReason;
		}

		if (this.isOpen()) {
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

	public static ClientConnection connect(InetSocketAddress address, boolean useEpoll, @Nullable PerformanceLog packetSizeLog) {
		ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		if (packetSizeLog != null) {
			clientConnection.resetPacketSizeLog(packetSizeLog);
		}

		ChannelFuture channelFuture = connect(address, useEpoll, clientConnection);
		channelFuture.syncUninterruptibly();
		return clientConnection;
	}

	public static ChannelFuture connect(InetSocketAddress address, boolean useEpoll, ClientConnection connection) {
		Class<? extends SocketChannel> class_;
		EventLoopGroup eventLoopGroup;
		if (Epoll.isAvailable() && useEpoll) {
			class_ = EpollSocketChannel.class;
			eventLoopGroup = (EventLoopGroup)EPOLL_CLIENT_IO_GROUP.get();
		} else {
			class_ = NioSocketChannel.class;
			eventLoopGroup = (EventLoopGroup)CLIENT_IO_GROUP.get();
		}

		return new Bootstrap().group(eventLoopGroup).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				ClientConnection.setHandlers(channel);

				try {
					channel.config().setOption(ChannelOption.TCP_NODELAY, true);
				} catch (ChannelException var3) {
				}

				ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
				ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND, connection.packetSizeLogger);
				connection.addFlowControlHandler(channelPipeline);
			}
		}).channel(class_).connect(address.getAddress(), address.getPort());
	}

	public static void addHandlers(ChannelPipeline pipeline, NetworkSide side, @Nullable PacketSizeLogger packetSizeLogger) {
		NetworkSide networkSide = side.getOpposite();
		AttributeKey<NetworkState.PacketHandler<?>> attributeKey = getProtocolAttributeKey(side);
		AttributeKey<NetworkState.PacketHandler<?>> attributeKey2 = getProtocolAttributeKey(networkSide);
		pipeline.addLast("splitter", new SplitterHandler(packetSizeLogger))
			.addLast("decoder", new DecoderHandler(attributeKey))
			.addLast("prepender", new SizePrepender())
			.addLast("encoder", new PacketEncoder(attributeKey2))
			.addLast("unbundler", new PacketUnbundler(attributeKey2))
			.addLast("bundler", new PacketBundler(attributeKey));
	}

	public void addFlowControlHandler(ChannelPipeline pipeline) {
		pipeline.addLast(new FlowControlHandler()).addLast("packet_handler", this);
	}

	private static void addValidatorInternal(ChannelPipeline pipeline, NetworkSide side) {
		NetworkSide networkSide = side.getOpposite();
		AttributeKey<NetworkState.PacketHandler<?>> attributeKey = getProtocolAttributeKey(side);
		AttributeKey<NetworkState.PacketHandler<?>> attributeKey2 = getProtocolAttributeKey(networkSide);
		pipeline.addLast("validator", new PacketValidator(attributeKey, attributeKey2));
	}

	public static void addValidator(ChannelPipeline pipeline, NetworkSide side) {
		addValidatorInternal(pipeline, side);
	}

	public static ClientConnection connectLocal(SocketAddress address) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		new Bootstrap().group((EventLoopGroup)LOCAL_CLIENT_IO_GROUP.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				ClientConnection.setHandlers(channel);
				ChannelPipeline channelPipeline = channel.pipeline();
				ClientConnection.addValidator(channelPipeline, NetworkSide.CLIENTBOUND);
				clientConnection.addFlowControlHandler(channelPipeline);
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

	public boolean isChannelAbsent() {
		return this.channel == null;
	}

	@Nullable
	public PacketListener getPacketListener() {
		return this.packetListener;
	}

	@Nullable
	public Text getDisconnectReason() {
		return this.disconnectReason;
	}

	public void tryDisableAutoRead() {
		if (this.channel != null) {
			this.channel.config().setAutoRead(false);
		}
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
				PacketListener packetListener = this.getPacketListener();
				PacketListener packetListener2 = packetListener != null ? packetListener : this.prePlayStateListener;
				if (packetListener2 != null) {
					Text text = (Text)Objects.requireNonNullElseGet(this.getDisconnectReason(), () -> Text.translatable("multiplayer.disconnect.generic"));
					packetListener2.onDisconnected(text);
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

	public void resetPacketSizeLog(PerformanceLog log) {
		this.packetSizeLogger = new PacketSizeLogger(log);
	}
}
