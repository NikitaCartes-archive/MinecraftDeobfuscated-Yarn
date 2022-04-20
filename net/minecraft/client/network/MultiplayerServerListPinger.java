/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListPinger {
    static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

    public void add(final ServerInfo entry, final Runnable runnable) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(serverAddress).map(Address::getInetSocketAddress);
        if (!optional.isPresent()) {
            this.showError(ConnectScreen.BLOCKED_HOST_TEXT, entry);
            return;
        }
        final InetSocketAddress inetSocketAddress = optional.get();
        final ClientConnection clientConnection = ClientConnection.connect(inetSocketAddress, false);
        this.clientConnections.add(clientConnection);
        entry.label = Text.translatable("multiplayer.status.pinging");
        entry.ping = -1L;
        entry.playerListSummary = null;
        clientConnection.setPacketListener(new ClientQueryPacketListener(){
            private boolean sentQuery;
            private boolean received;
            private long startTime;

            @Override
            public void onResponse(QueryResponseS2CPacket packet) {
                if (this.received) {
                    clientConnection.disconnect(Text.translatable("multiplayer.status.unrequested"));
                    return;
                }
                this.received = true;
                ServerMetadata serverMetadata = packet.getServerMetadata();
                entry.label = serverMetadata.getDescription() != null ? serverMetadata.getDescription() : ScreenTexts.EMPTY;
                if (serverMetadata.getVersion() != null) {
                    entry.version = Text.literal(serverMetadata.getVersion().getGameVersion());
                    entry.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
                } else {
                    entry.version = Text.translatable("multiplayer.status.old");
                    entry.protocolVersion = 0;
                }
                if (serverMetadata.getPlayers() != null) {
                    entry.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(serverMetadata.getPlayers().getOnlinePlayerCount(), serverMetadata.getPlayers().getPlayerLimit());
                    ArrayList<Text> list = Lists.newArrayList();
                    GameProfile[] gameProfiles = serverMetadata.getPlayers().getSample();
                    if (gameProfiles != null && gameProfiles.length > 0) {
                        for (GameProfile gameProfile : gameProfiles) {
                            list.add(Text.literal(gameProfile.getName()));
                        }
                        if (gameProfiles.length < serverMetadata.getPlayers().getOnlinePlayerCount()) {
                            list.add(Text.translatable("multiplayer.status.and_more", serverMetadata.getPlayers().getOnlinePlayerCount() - gameProfiles.length));
                        }
                        entry.playerListSummary = list;
                    }
                } else {
                    entry.playerCountLabel = Text.translatable("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY);
                }
                String string = null;
                if (serverMetadata.getFavicon() != null) {
                    String string2 = serverMetadata.getFavicon();
                    if (string2.startsWith("data:image/png;base64,")) {
                        string = string2.substring("data:image/png;base64,".length());
                    } else {
                        LOGGER.error("Invalid server icon (unknown format)");
                    }
                }
                if (!Objects.equals(string, entry.getIcon())) {
                    entry.setIcon(string);
                    runnable.run();
                }
                this.startTime = Util.getMeasuringTimeMs();
                clientConnection.send(new QueryPingC2SPacket(this.startTime));
                this.sentQuery = true;
            }

            @Override
            public void onPong(QueryPongS2CPacket packet) {
                long l = this.startTime;
                long m = Util.getMeasuringTimeMs();
                entry.ping = m - l;
                clientConnection.disconnect(Text.translatable("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnected(Text reason) {
                if (!this.sentQuery) {
                    MultiplayerServerListPinger.this.showError(reason, entry);
                    MultiplayerServerListPinger.this.ping(inetSocketAddress, entry);
                }
            }

            @Override
            public ClientConnection getConnection() {
                return clientConnection;
            }
        });
        try {
            clientConnection.send(new HandshakeC2SPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
            clientConnection.send(new QueryRequestC2SPacket());
        } catch (Throwable throwable) {
            LOGGER.error("Failed to ping server {}", (Object)serverAddress, (Object)throwable);
        }
    }

    void showError(Text error, ServerInfo info) {
        LOGGER.error("Can't ping {}: {}", (Object)info.address, (Object)error.getString());
        info.label = CANNOT_CONNECT_TEXT;
        info.playerCountLabel = ScreenTexts.EMPTY;
    }

    void ping(final InetSocketAddress address, final ServerInfo info) {
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(ClientConnection.CLIENT_IO_GROUP.get())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void channelActive(ChannelHandlerContext context) throws Exception {
                        super.channelActive(context);
                        ByteBuf byteBuf = Unpooled.buffer();
                        try {
                            byteBuf.writeByte(254);
                            byteBuf.writeByte(1);
                            byteBuf.writeByte(250);
                            char[] cs = "MC|PingHost".toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar(c);
                            }
                            byteBuf.writeShort(7 + 2 * address.getHostName().length());
                            byteBuf.writeByte(127);
                            cs = address.getHostName().toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar(c);
                            }
                            byteBuf.writeInt(address.getPort());
                            context.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            byteBuf.release();
                        }
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
                        String string;
                        String[] strings;
                        short s = byteBuf.readUnsignedByte();
                        if (s == 255 && "\u00a71".equals((strings = Iterables.toArray(ZERO_SPLITTER.split(string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE)), String.class))[0])) {
                            int i = MathHelper.parseInt(strings[1], 0);
                            String string2 = strings[2];
                            String string3 = strings[3];
                            int j = MathHelper.parseInt(strings[4], -1);
                            int k = MathHelper.parseInt(strings[5], -1);
                            info.protocolVersion = -1;
                            info.version = Text.literal(string2);
                            info.label = Text.literal(string3);
                            info.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(j, k);
                        }
                        channelHandlerContext.close();
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
                        channelHandlerContext.close();
                    }

                    @Override
                    protected /* synthetic */ void channelRead0(ChannelHandlerContext context, Object buf) throws Exception {
                        this.channelRead0(context, (ByteBuf)buf);
                    }
                });
            }
        })).channel(NioSocketChannel.class)).connect(address.getAddress(), address.getPort());
    }

    static Text createPlayerCountText(int current, int max) {
        return Text.literal(Integer.toString(current)).append(Text.literal("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(max)).formatted(Formatting.GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (clientConnection.isOpen()) {
                    clientConnection.tick();
                    continue;
                }
                iterator.remove();
                clientConnection.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (!clientConnection.isOpen()) continue;
                iterator.remove();
                clientConnection.disconnect(Text.translatable("multiplayer.status.cancelled"));
            }
        }
    }
}

