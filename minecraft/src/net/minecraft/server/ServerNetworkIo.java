package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.LegacyQueryHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.RateLimitedConnection;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.LocalServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Lazy;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.slf4j.Logger;

public class ServerNetworkIo {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build())
	);
	final MinecraftServer server;
	public volatile boolean active;
	private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
	final List<ClientConnection> connections = Collections.synchronizedList(Lists.newArrayList());

	public ServerNetworkIo(MinecraftServer server) {
		this.server = server;
		this.active = true;
	}

	public void bind(@Nullable InetAddress address, int port) throws IOException {
		synchronized (this.channels) {
			Class<? extends ServerSocketChannel> class_;
			Lazy<? extends EventLoopGroup> lazy;
			if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
				class_ = EpollServerSocketChannel.class;
				lazy = EPOLL_CHANNEL;
				LOGGER.info("Using epoll channel type");
			} else {
				class_ = NioServerSocketChannel.class;
				lazy = DEFAULT_CHANNEL;
				LOGGER.info("Using default channel type");
			}

			this.channels
				.add(
					new ServerBootstrap()
						.channel(class_)
						.childHandler(
							new ChannelInitializer<Channel>() {
								@Override
								protected void initChannel(Channel channel) {
									try {
										channel.config().setOption(ChannelOption.TCP_NODELAY, true);
									} catch (ChannelException var4) {
									}

									channel.pipeline()
										.addLast("timeout", new ReadTimeoutHandler(30))
										.addLast("legacy_query", new LegacyQueryHandler(ServerNetworkIo.this))
										.addLast("splitter", new SplitterHandler())
										.addLast("decoder", new DecoderHandler(NetworkSide.SERVERBOUND))
										.addLast("prepender", new SizePrepender())
										.addLast("encoder", new PacketEncoder(NetworkSide.CLIENTBOUND));
									int i = ServerNetworkIo.this.server.getRateLimit();
									ClientConnection clientConnection = (ClientConnection)(i > 0 ? new RateLimitedConnection(i) : new ClientConnection(NetworkSide.SERVERBOUND));
									ServerNetworkIo.this.connections.add(clientConnection);
									channel.pipeline().addLast("packet_handler", clientConnection);
									clientConnection.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
								}
							}
						)
						.group(lazy.get())
						.localAddress(address, port)
						.bind()
						.syncUninterruptibly()
				);
		}
	}

	public SocketAddress bindLocal() {
		ChannelFuture channelFuture;
		synchronized (this.channels) {
			channelFuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) {
					ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
					clientConnection.setPacketListener(new LocalServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
					ServerNetworkIo.this.connections.add(clientConnection);
					channel.pipeline().addLast("packet_handler", clientConnection);
				}
			}).group(DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
			this.channels.add(channelFuture);
		}

		return channelFuture.channel().localAddress();
	}

	public void stop() {
		this.active = false;

		for (ChannelFuture channelFuture : this.channels) {
			try {
				channelFuture.channel().close().sync();
			} catch (InterruptedException var4) {
				LOGGER.error("Interrupted whilst closing channel");
			}
		}
	}

	public void tick() {
		synchronized (this.connections) {
			Iterator<ClientConnection> iterator = this.connections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (!clientConnection.hasChannel()) {
					if (clientConnection.isOpen()) {
						try {
							clientConnection.tick();
						} catch (Exception var7) {
							if (clientConnection.isLocal()) {
								throw new CrashException(CrashReport.create(var7, "Ticking memory connection"));
							}

							LOGGER.warn("Failed to handle packet for {}", clientConnection.getAddress(), var7);
							Text text = Text.literal("Internal server error");
							clientConnection.send(new DisconnectS2CPacket(text), PacketCallbacks.always(() -> clientConnection.disconnect(text)));
							clientConnection.disableAutoRead();
						}
					} else {
						iterator.remove();
						clientConnection.handleDisconnection();
					}
				}
			}
		}
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public List<ClientConnection> getConnections() {
		return this.connections;
	}

	/**
	 * A channel in-bound handler that only forwards received messages to the next
	 * channel in-bound handler in the channel pipeline after a random delay between
	 * {@link #baseDelay} and {@code baseDelay + }{@link #extraDelay} milliseconds.
	 * 
	 * @apiNote This may be used to simulate a laggy network environment.
	 */
	static class DelayingChannelInboundHandler extends ChannelInboundHandlerAdapter {
		private static final Timer TIMER = new HashedWheelTimer();
		private final int baseDelay;
		private final int extraDelay;
		private final List<ServerNetworkIo.DelayingChannelInboundHandler.Packet> packets = Lists.<ServerNetworkIo.DelayingChannelInboundHandler.Packet>newArrayList();

		public DelayingChannelInboundHandler(int baseDelay, int extraDelay) {
			this.baseDelay = baseDelay;
			this.extraDelay = extraDelay;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			this.delay(ctx, msg);
		}

		private void delay(ChannelHandlerContext ctx, Object msg) {
			int i = this.baseDelay + (int)(Math.random() * (double)this.extraDelay);
			this.packets.add(new ServerNetworkIo.DelayingChannelInboundHandler.Packet(ctx, msg));
			TIMER.newTimeout(this::forward, (long)i, TimeUnit.MILLISECONDS);
		}

		private void forward(Timeout timeout) {
			ServerNetworkIo.DelayingChannelInboundHandler.Packet packet = (ServerNetworkIo.DelayingChannelInboundHandler.Packet)this.packets.remove(0);
			packet.context.fireChannelRead(packet.message);
		}

		static class Packet {
			public final ChannelHandlerContext context;
			public final Object message;

			public Packet(ChannelHandlerContext context, Object message) {
				this.context = context;
				this.message = message;
			}
		}
	}
}
