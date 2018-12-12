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
import net.minecraft.class_3238;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.server.network.IntegratedServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Lazy;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerNetworkIO {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Lazy<NioEventLoopGroup> field_14111 = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> field_14105 = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build())
	);
	private final MinecraftServer server;
	public volatile boolean field_14108;
	private final List<ChannelFuture> field_14106 = Collections.synchronizedList(Lists.newArrayList());
	private final List<ClientConnection> field_14107 = Collections.synchronizedList(Lists.newArrayList());

	public ServerNetworkIO(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
		this.field_14108 = true;
	}

	public void method_14354(@Nullable InetAddress inetAddress, int i) throws IOException {
		synchronized (this.field_14106) {
			Class<? extends ServerSocketChannel> class_;
			Lazy<? extends EventLoopGroup> lazy;
			if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
				class_ = EpollServerSocketChannel.class;
				lazy = field_14105;
				LOGGER.info("Using epoll channel type");
			} else {
				class_ = NioServerSocketChannel.class;
				lazy = field_14111;
				LOGGER.info("Using default channel type");
			}

			this.field_14106
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
										.addLast("legacy_query", new class_3238(ServerNetworkIO.this))
										.addLast("splitter", new SplitterHandler())
										.addLast("decoder", new DecoderHandler(NetworkSide.SERVER))
										.addLast("prepender", new SizePrepender())
										.addLast("encoder", new PacketEncoder(NetworkSide.CLIENT));
									ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVER);
									ServerNetworkIO.this.field_14107.add(clientConnection);
									channel.pipeline().addLast("packet_handler", clientConnection);
									clientConnection.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIO.this.server, clientConnection));
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
	public SocketAddress method_14353() {
		ChannelFuture channelFuture;
		synchronized (this.field_14106) {
			channelFuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVER);
					clientConnection.setPacketListener(new IntegratedServerHandshakeNetworkHandler(ServerNetworkIO.this.server, clientConnection));
					ServerNetworkIO.this.field_14107.add(clientConnection);
					channel.pipeline().addLast("packet_handler", clientConnection);
				}
			}).group(field_14111.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
			this.field_14106.add(channelFuture);
		}

		return channelFuture.channel().localAddress();
	}

	public void stop() {
		this.field_14108 = false;

		for (ChannelFuture channelFuture : this.field_14106) {
			try {
				channelFuture.channel().close().sync();
			} catch (InterruptedException var4) {
				LOGGER.error("Interrupted whilst closing channel");
			}
		}
	}

	public void tick() {
		synchronized (this.field_14107) {
			Iterator<ClientConnection> iterator = this.field_14107.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (!clientConnection.hasChannel()) {
					if (clientConnection.isOpen()) {
						try {
							clientConnection.tick();
						} catch (Exception var8) {
							if (clientConnection.isLocal()) {
								CrashReport crashReport = CrashReport.create(var8, "Ticking memory connection");
								CrashReportSection crashReportSection = crashReport.method_562("Ticking connection");
								crashReportSection.add("Connection", clientConnection::toString);
								throw new CrashException(crashReport);
							}

							LOGGER.warn("Failed to handle packet for {}", clientConnection.getAddress(), var8);
							TextComponent textComponent = new StringTextComponent("Internal server error");
							clientConnection.sendPacket(new DisconnectClientPacket(textComponent), future -> clientConnection.disconnect(textComponent));
							clientConnection.method_10757();
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
