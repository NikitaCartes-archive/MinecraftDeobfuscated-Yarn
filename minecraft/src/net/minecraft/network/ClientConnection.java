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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
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
	public static final Lazy<NioEventLoopGroup> field_11650 = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> field_11657 = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<DefaultEventLoopGroup> field_11649 = new Lazy<>(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	private final NetworkSide field_11643;
	private final Queue<ClientConnection.class_2536> field_11644 = Queues.<ClientConnection.class_2536>newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Channel channel;
	private SocketAddress address;
	private PacketListener field_11652;
	private TextComponent field_11660;
	private boolean encrypted;
	private boolean disconnected;
	private int packetsReceivedCounter;
	private int packetsSentCounter;
	private float avgPacketsReceived;
	private float avgPacketsSent;
	private int ticks;
	private boolean field_11640;

	public ClientConnection(NetworkSide networkSide) {
		this.field_11643 = networkSide;
	}

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		super.channelActive(channelHandlerContext);
		this.channel = channelHandlerContext.channel();
		this.address = this.channel.remoteAddress();

		try {
			this.method_10750(NetworkState.HANDSHAKE);
		} catch (Throwable var3) {
			LOGGER.fatal(var3);
		}
	}

	public void method_10750(NetworkState networkState) {
		this.channel.attr(ATTR_KEY_PROTOCOL).set(networkState);
		this.channel.config().setAutoRead(true);
		LOGGER.debug("Enabled auto read");
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
		this.method_10747(new TranslatableTextComponent("disconnect.endOfStream"));
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
					this.method_10747(new TranslatableTextComponent("disconnect.timeout"));
				} else {
					TextComponent textComponent = new TranslatableTextComponent("disconnect.genericReason", "Internal Exception: " + throwable);
					if (bl) {
						LOGGER.debug("Failed to sent packet", throwable);
						this.method_10752(new DisconnectS2CPacket(textComponent), future -> this.method_10747(textComponent));
						this.disableAutoRead();
					} else {
						LOGGER.debug("Double fault", throwable);
						this.method_10747(textComponent);
					}
				}
			}
		}
	}

	protected void method_10770(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				method_10759(packet, this.field_11652);
			} catch (OffThreadException var4) {
			}

			this.packetsReceivedCounter++;
		}
	}

	private static <T extends PacketListener> void method_10759(Packet<T> packet, PacketListener packetListener) {
		packet.apply((T)packetListener);
	}

	public void method_10763(PacketListener packetListener) {
		Validate.notNull(packetListener, "packetListener");
		LOGGER.debug("Set listener of {} to {}", this, packetListener);
		this.field_11652 = packetListener;
	}

	public void method_10743(Packet<?> packet) {
		this.method_10752(packet, null);
	}

	public void method_10752(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
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
		NetworkState networkState = NetworkState.method_10786(packet);
		NetworkState networkState2 = this.channel.attr(ATTR_KEY_PROTOCOL).get();
		this.packetsSentCounter++;
		if (networkState2 != networkState) {
			LOGGER.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			if (networkState != networkState2) {
				this.method_10750(networkState);
			}

			ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
			if (genericFutureListener != null) {
				channelFuture.addListener(genericFutureListener);
			}

			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(() -> {
				if (networkState != networkState2) {
					this.method_10750(networkState);
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
		if (this.field_11652 instanceof ServerLoginNetworkHandler) {
			((ServerLoginNetworkHandler)this.field_11652).method_18785();
		}

		if (this.field_11652 instanceof ServerPlayNetworkHandler) {
			((ServerPlayNetworkHandler)this.field_11652).method_18784();
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

	public void method_10747(TextComponent textComponent) {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
			this.field_11660 = textComponent;
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
			lazy = field_11657;
		} else {
			class_ = NioSocketChannel.class;
			lazy = field_11650;
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
		new Bootstrap().group(field_11649.get()).handler(new ChannelInitializer<Channel>() {
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

	public PacketListener method_10744() {
		return this.field_11652;
	}

	@Nullable
	public TextComponent method_10748() {
		return this.field_11660;
	}

	public void disableAutoRead() {
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
				if (this.method_10748() != null) {
					this.method_10744().method_10839(this.method_10748());
				} else if (this.method_10744() != null) {
					this.method_10744().method_10839(new TranslatableTextComponent("multiplayer.disconnect.generic"));
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
