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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Lazy;
import net.minecraft.util.Tickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Marker field_11641 = MarkerManager.getMarker("NETWORK");
	public static final Marker field_11639 = MarkerManager.getMarker("NETWORK_PACKETS", field_11641);
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
	private final Queue<ClientConnection.class_2536> field_11644 = Queues.<ClientConnection.class_2536>newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Channel channel;
	private SocketAddress address;
	private PacketListener packetListener;
	private TextComponent disconnectReason;
	private boolean encrypted;
	private boolean disconnected;
	private int field_11658;
	private int field_11656;
	private float field_11654;
	private float field_11653;
	private int field_11655;
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
			this.setState(NetworkState.HANDSHAKE);
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
		this.disconnect(new TranslatableTextComponent("disconnect.endOfStream"));
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
					this.disconnect(new TranslatableTextComponent("disconnect.timeout"));
				} else {
					TextComponent textComponent = new TranslatableTextComponent("disconnect.genericReason", "Internal Exception: " + throwable);
					if (bl) {
						LOGGER.debug("Failed to sent packet", throwable);
						this.sendPacket(new DisconnectS2CPacket(textComponent), future -> this.disconnect(textComponent));
						this.method_10757();
					} else {
						LOGGER.debug("Double fault", throwable);
						this.disconnect(textComponent);
					}
				}
			}
		}
	}

	protected void method_10770(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				method_10759(packet, this.packetListener);
			} catch (OffThreadException var4) {
			}

			this.field_11658++;
		}
	}

	private static <T extends PacketListener> void method_10759(Packet<T> packet, PacketListener packetListener) {
		packet.apply((T)packetListener);
	}

	public void setPacketListener(PacketListener packetListener) {
		Validate.notNull(packetListener, "packetListener");
		LOGGER.debug("Set listener of {} to {}", this, packetListener);
		this.packetListener = packetListener;
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (this.isOpen()) {
			this.method_10751();
			this.method_10764(packet, genericFutureListener);
		} else {
			this.lock.writeLock().lock();

			try {
				this.field_11644.add(new ClientConnection.class_2536(packet, genericFutureListener));
			} finally {
				this.lock.writeLock().unlock();
			}
		}
	}

	private void method_10764(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		NetworkState networkState = NetworkState.getPacketHandlerState(packet);
		NetworkState networkState2 = this.channel.attr(ATTR_KEY_PROTOCOL).get();
		this.field_11656++;
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

	private void method_10751() {
		if (this.channel != null && this.channel.isOpen()) {
			this.lock.readLock().lock();

			try {
				while (!this.field_11644.isEmpty()) {
					ClientConnection.class_2536 lv = (ClientConnection.class_2536)this.field_11644.poll();
					this.method_10764(lv.field_11661, lv.field_11662);
				}
			} finally {
				this.lock.readLock().unlock();
			}
		}
	}

	public void tick() {
		this.method_10751();
		if (this.packetListener instanceof Tickable) {
			((Tickable)this.packetListener).tick();
		}

		if (this.channel != null) {
			this.channel.flush();
		}

		if (this.field_11655++ % 20 == 0) {
			this.field_11653 = this.field_11653 * 0.75F + (float)this.field_11656 * 0.25F;
			this.field_11654 = this.field_11654 * 0.75F + (float)this.field_11658 * 0.25F;
			this.field_11656 = 0;
			this.field_11658 = 0;
		}
	}

	public SocketAddress getAddress() {
		return this.address;
	}

	public void disconnect(TextComponent textComponent) {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
			this.disconnectReason = textComponent;
		}
	}

	public boolean isLocal() {
		return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
	}

	@Environment(EnvType.CLIENT)
	public static ClientConnection connect(InetAddress inetAddress, int i, boolean bl) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENT);
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
							.addLast("decoder", new DecoderHandler(NetworkSide.CLIENT))
							.addLast("prepender", new SizePrepender())
							.addLast("encoder", new PacketEncoder(NetworkSide.SERVER))
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
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENT);
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
	public TextComponent getDisconnectReason() {
		return this.disconnectReason;
	}

	public void method_10757() {
		this.channel.config().setAutoRead(false);
	}

	public void setMinCompressedSize(int i) {
		if (i >= 0) {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				((PacketInflater)this.channel.pipeline().get("decompress")).setMinCompressedSize(i);
			} else {
				this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(i));
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				((PacketDeflater)this.channel.pipeline().get("compress")).setMinCompressedSize(i);
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
				if (this.getDisconnectReason() != null) {
					this.getPacketListener().onConnectionLost(this.getDisconnectReason());
				} else if (this.getPacketListener() != null) {
					this.getPacketListener().onConnectionLost(new TranslatableTextComponent("multiplayer.disconnect.generic"));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float getPacketsReceived() {
		return this.field_11654;
	}

	@Environment(EnvType.CLIENT)
	public float getPacketsSent() {
		return this.field_11653;
	}

	static class class_2536 {
		private final Packet<?> field_11661;
		@Nullable
		private final GenericFutureListener<? extends Future<? super Void>> field_11662;

		public class_2536(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
			this.field_11661 = packet;
			this.field_11662 = genericFutureListener;
		}
	}
}
