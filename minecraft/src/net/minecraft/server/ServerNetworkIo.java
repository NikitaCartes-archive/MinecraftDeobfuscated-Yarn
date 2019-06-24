package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.LegacyQueryHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.server.network.IntegratedServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Lazy;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetworkIo {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build())
	);
	private final MinecraftServer server;
	public volatile boolean active;
	private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
	private final List<ClientConnection> connections = Collections.synchronizedList(Lists.newArrayList());

	public ServerNetworkIo(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
		this.active = true;
	}

	public void bind(@Nullable InetAddress inetAddress, int i) throws IOException {
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
								protected void initChannel(Channel channel) throws Exception {
									try {
										channel.config().setOption(ChannelOption.TCP_NODELAY, true);
									} catch (ChannelException var3) {
									}

									channel.pipeline()
										.addLast("timeout", new ReadTimeoutHandler(30))
										.addLast("legacy_query", new LegacyQueryHandler(ServerNetworkIo.this))
										.addLast("splitter", new SplitterHandler())
										.addLast("decoder", new DecoderHandler(NetworkSide.SERVERBOUND))
										.addLast("prepender", new SizePrepender())
										.addLast("encoder", new PacketEncoder(NetworkSide.CLIENTBOUND));
									ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
									ServerNetworkIo.this.connections.add(clientConnection);
									channel.pipeline().addLast("packet_handler", clientConnection);
									clientConnection.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
								}
							}
						)
						.group(lazy.get())
						.localAddress(inetAddress, i)
						.bind()
						.syncUninterruptibly()
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public SocketAddress bindLocal() {
		ChannelFuture channelFuture;
		synchronized (this.channels) {
			channelFuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
					clientConnection.setPacketListener(new IntegratedServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
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
						} catch (Exception var8) {
							if (clientConnection.isLocal()) {
								CrashReport crashReport = CrashReport.create(var8, "Ticking memory connection");
								CrashReportSection crashReportSection = crashReport.addElement("Ticking connection");
								crashReportSection.add("Connection", clientConnection::toString);
								throw new CrashException(crashReport);
							}

							LOGGER.warn("Failed to handle packet for {}", clientConnection.getAddress(), var8);
							Text text = new LiteralText("Internal server error");
							clientConnection.send(new DisconnectS2CPacket(text), future -> clientConnection.disconnect(text));
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
}
