/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDeflater;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.network.PacketInflater;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
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
import org.jetbrains.annotations.Nullable;

public class ClientConnection
extends SimpleChannelInboundHandler<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker MARKER_NETWORK = MarkerManager.getMarker("NETWORK");
    public static final Marker MARKER_NETWORK_PACKETS = MarkerManager.getMarker("NETWORK_PACKETS", MARKER_NETWORK);
    public static final AttributeKey<NetworkState> ATTR_KEY_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final Lazy<NioEventLoopGroup> CLIENT_IO_GROUP = new Lazy<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final Lazy<EpollEventLoopGroup> CLIENT_IO_GROUP_EPOLL = new Lazy<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    public static final Lazy<DefaultEventLoopGroup> CLIENT_IO_GROUP_LOCAL = new Lazy<DefaultEventLoopGroup>(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
    private final NetworkSide side;
    private final Queue<PacketWrapper> packetQueue = Queues.newConcurrentLinkedQueue();
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private Text disconnectReason;
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
        } catch (Throwable throwable) {
            LOGGER.fatal(throwable);
        }
    }

    public void setState(NetworkState networkState) {
        this.channel.attr(ATTR_KEY_PROTOCOL).set(networkState);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.disconnect(new TranslatableText("disconnect.endOfStream", new Object[0]));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        if (throwable instanceof PacketEncoderException) {
            LOGGER.debug("Skipping packet due to errors", throwable.getCause());
            return;
        }
        boolean bl = !this.field_11640;
        this.field_11640 = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if (throwable instanceof TimeoutException) {
            LOGGER.debug("Timeout", throwable);
            this.disconnect(new TranslatableText("disconnect.timeout", new Object[0]));
        } else {
            TranslatableText text = new TranslatableText("disconnect.genericReason", "Internal Exception: " + throwable);
            if (bl) {
                LOGGER.debug("Failed to sent packet", throwable);
                this.send(new DisconnectS2CPacket(text), future -> this.disconnect(text));
                this.disableAutoRead();
            } else {
                LOGGER.debug("Double fault", throwable);
                this.disconnect(text);
            }
        }
    }

    protected void method_10770(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                ClientConnection.handlePacket(packet, this.packetListener);
            } catch (OffThreadException offThreadException) {
                // empty catch block
            }
            ++this.packetsReceivedCounter;
        }
    }

    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener packetListener) {
        packet.apply(packetListener);
    }

    public void setPacketListener(PacketListener packetListener) {
        Validate.notNull(packetListener, "packetListener", new Object[0]);
        LOGGER.debug("Set listener of {} to {}", (Object)this, (Object)packetListener);
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
            this.packetQueue.add(new PacketWrapper(packet, genericFutureListener));
        }
    }

    private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        NetworkState networkState = NetworkState.getPacketHandlerState(packet);
        NetworkState networkState2 = this.channel.attr(ATTR_KEY_PROTOCOL).get();
        ++this.packetsSentCounter;
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
                ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
                if (genericFutureListener != null) {
                    channelFuture.addListener(genericFutureListener);
                }
                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendQueuedPackets() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        Queue<PacketWrapper> queue = this.packetQueue;
        synchronized (queue) {
            PacketWrapper packetWrapper;
            while ((packetWrapper = this.packetQueue.poll()) != null) {
                this.sendImmediately(packetWrapper.packet, packetWrapper.listener);
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
            this.avgPacketsSent = this.avgPacketsSent * 0.75f + (float)this.packetsSentCounter * 0.25f;
            this.avgPacketsReceived = this.avgPacketsReceived * 0.75f + (float)this.packetsReceivedCounter * 0.25f;
            this.packetsSentCounter = 0;
            this.packetsReceivedCounter = 0;
        }
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public void disconnect(Text text) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectReason = text;
        }
    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    @Environment(value=EnvType.CLIENT)
    public static ClientConnection connect(InetAddress inetAddress, int i, boolean bl) {
        Lazy<MultithreadEventLoopGroup> lazy;
        Class class_;
        final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        if (Epoll.isAvailable() && bl) {
            class_ = EpollSocketChannel.class;
            lazy = CLIENT_IO_GROUP_EPOLL;
        } else {
            class_ = NioSocketChannel.class;
            lazy = CLIENT_IO_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(lazy.get())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new SplitterHandler()).addLast("decoder", (ChannelHandler)new DecoderHandler(NetworkSide.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new SizePrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(NetworkSide.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)clientConnection);
            }
        })).channel(class_)).connect(inetAddress, i).syncUninterruptibly();
        return clientConnection;
    }

    @Environment(value=EnvType.CLIENT)
    public static ClientConnection connect(SocketAddress socketAddress) {
        final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(CLIENT_IO_GROUP_LOCAL.get())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast("packet_handler", (ChannelHandler)clientConnection);
            }
        })).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return clientConnection;
    }

    public void setupEncryption(SecretKey secretKey) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(NetworkEncryptionUtils.cipherFromKey(2, secretKey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(NetworkEncryptionUtils.cipherFromKey(1, secretKey)));
    }

    @Environment(value=EnvType.CLIENT)
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
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
        } else {
            this.disconnected = true;
            if (this.getDisconnectReason() != null) {
                this.getPacketListener().onDisconnected(this.getDisconnectReason());
            } else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnected(new TranslatableText("multiplayer.disconnect.generic", new Object[0]));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getAveragePacketsReceived() {
        return this.avgPacketsReceived;
    }

    @Environment(value=EnvType.CLIENT)
    public float getAveragePacketsSent() {
        return this.avgPacketsSent;
    }

    @Override
    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.method_10770(channelHandlerContext, (Packet)object);
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

