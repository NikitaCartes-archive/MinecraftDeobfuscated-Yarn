/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
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
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.LegacyQueryHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.RateLimitedConnection;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.LocalServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Lazy;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerNetworkIo {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));
    final MinecraftServer server;
    public volatile boolean active;
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
    final List<ClientConnection> connections = Collections.synchronizedList(Lists.newArrayList());

    public ServerNetworkIo(MinecraftServer server) {
        this.server = server;
        this.active = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bind(@Nullable InetAddress address, int port) throws IOException {
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            Lazy<MultithreadEventLoopGroup> lazy;
            Class class_;
            if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
                class_ = EpollServerSocketChannel.class;
                lazy = EPOLL_CHANNEL;
                LOGGER.info("Using epoll channel type");
            } else {
                class_ = NioServerSocketChannel.class;
                lazy = DEFAULT_CHANNEL;
                LOGGER.info("Using default channel type");
            }
            this.channels.add(((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(class_)).childHandler(new ChannelInitializer<Channel>(){

                @Override
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException channelException) {
                        // empty catch block
                    }
                    channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler)new LegacyQueryHandler(ServerNetworkIo.this)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.SERVERBOUND)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.CLIENTBOUND));
                    int i = ServerNetworkIo.this.server.getRateLimit();
                    ClientConnection clientConnection = i > 0 ? new RateLimitedConnection(i) : new ClientConnection(NetworkSide.SERVERBOUND);
                    ServerNetworkIo.this.connections.add(clientConnection);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection);
                    clientConnection.setPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
                }
            }).group(lazy.get()).localAddress(address, port)).bind().syncUninterruptibly());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SocketAddress bindLocal() {
        ChannelFuture channelFuture;
        List<ChannelFuture> list = this.channels;
        synchronized (list) {
            channelFuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>(){

                @Override
                protected void initChannel(Channel channel) {
                    ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
                    clientConnection.setPacketListener(new LocalServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
                    ServerNetworkIo.this.connections.add(clientConnection);
                    channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection);
                }
            }).group(DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.channels.add(channelFuture);
        }
        return channelFuture.channel().localAddress();
    }

    public void stop() {
        this.active = false;
        for (ChannelFuture channelFuture : this.channels) {
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException interruptedException) {
                LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<ClientConnection> list = this.connections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.connections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (clientConnection.hasChannel()) continue;
                if (clientConnection.isOpen()) {
                    try {
                        clientConnection.tick();
                    } catch (Exception exception) {
                        if (clientConnection.isLocal()) {
                            throw new CrashException(CrashReport.create(exception, "Ticking memory connection"));
                        }
                        LOGGER.warn("Failed to handle packet for {}", (Object)clientConnection.getAddress(), (Object)exception);
                        LiteralText text = new LiteralText("Internal server error");
                        clientConnection.send(new DisconnectS2CPacket(text), future -> clientConnection.disconnect(text));
                        clientConnection.disableAutoRead();
                    }
                    continue;
                }
                iterator.remove();
                clientConnection.handleDisconnection();
            }
        }
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public List<ClientConnection> getConnections() {
        return this.connections;
    }

    static class DelayingChannelInboundHandler
    extends ChannelInboundHandlerAdapter {
        private static final Timer TIMER = new HashedWheelTimer();
        private final int baseDelay;
        private final int extraDelay;
        private final List<Packet> packets = Lists.newArrayList();

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
            this.packets.add(new Packet(ctx, msg));
            TIMER.newTimeout(this::forward, i, TimeUnit.MILLISECONDS);
        }

        private void forward(Timeout timeout) {
            Packet packet = this.packets.remove(0);
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

