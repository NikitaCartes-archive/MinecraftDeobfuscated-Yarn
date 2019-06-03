/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerEntryNetworkPart {
    private static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

    public void method_3003(final ServerEntry serverEntry) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
        final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
        this.clientConnections.add(clientConnection);
        serverEntry.label = I18n.translate("multiplayer.status.pinging", new Object[0]);
        serverEntry.ping = -1L;
        serverEntry.playerListSummary = null;
        clientConnection.setPacketListener(new ClientQueryPacketListener(){
            private boolean field_3775;
            private boolean field_3773;
            private long field_3772;

            @Override
            public void onResponse(QueryResponseS2CPacket queryResponseS2CPacket) {
                if (this.field_3773) {
                    clientConnection.disconnect(new TranslatableText("multiplayer.status.unrequested", new Object[0]));
                    return;
                }
                this.field_3773 = true;
                ServerMetadata serverMetadata = queryResponseS2CPacket.getServerMetadata();
                serverEntry.label = serverMetadata.getDescription() != null ? serverMetadata.getDescription().asFormattedString() : "";
                if (serverMetadata.getVersion() != null) {
                    serverEntry.version = serverMetadata.getVersion().getGameVersion();
                    serverEntry.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
                } else {
                    serverEntry.version = I18n.translate("multiplayer.status.old", new Object[0]);
                    serverEntry.protocolVersion = 0;
                }
                if (serverMetadata.getPlayers() != null) {
                    serverEntry.playerCountLabel = (Object)((Object)Formatting.GRAY) + "" + serverMetadata.getPlayers().getOnlinePlayerCount() + "" + (Object)((Object)Formatting.DARK_GRAY) + "/" + (Object)((Object)Formatting.GRAY) + serverMetadata.getPlayers().getPlayerLimit();
                    if (ArrayUtils.isNotEmpty(serverMetadata.getPlayers().getSample())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (GameProfile gameProfile : serverMetadata.getPlayers().getSample()) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append("\n");
                            }
                            stringBuilder.append(gameProfile.getName());
                        }
                        if (serverMetadata.getPlayers().getSample().length < serverMetadata.getPlayers().getOnlinePlayerCount()) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append("\n");
                            }
                            stringBuilder.append(I18n.translate("multiplayer.status.and_more", serverMetadata.getPlayers().getOnlinePlayerCount() - serverMetadata.getPlayers().getSample().length));
                        }
                        serverEntry.playerListSummary = stringBuilder.toString();
                    }
                } else {
                    serverEntry.playerCountLabel = (Object)((Object)Formatting.DARK_GRAY) + I18n.translate("multiplayer.status.unknown", new Object[0]);
                }
                if (serverMetadata.getFavicon() != null) {
                    String string = serverMetadata.getFavicon();
                    if (string.startsWith("data:image/png;base64,")) {
                        serverEntry.setIcon(string.substring("data:image/png;base64,".length()));
                    } else {
                        LOGGER.error("Invalid server icon (unknown format)");
                    }
                } else {
                    serverEntry.setIcon(null);
                }
                this.field_3772 = SystemUtil.getMeasuringTimeMs();
                clientConnection.send(new QueryPingC2SPacket(this.field_3772));
                this.field_3775 = true;
            }

            @Override
            public void onPong(QueryPongS2CPacket queryPongS2CPacket) {
                long l = this.field_3772;
                long m = SystemUtil.getMeasuringTimeMs();
                serverEntry.ping = m - l;
                clientConnection.disconnect(new TranslatableText("multiplayer.status.finished", new Object[0]));
            }

            @Override
            public void onDisconnected(Text text) {
                if (!this.field_3775) {
                    LOGGER.error("Can't ping {}: {}", (Object)serverEntry.address, (Object)text.getString());
                    serverEntry.label = (Object)((Object)Formatting.DARK_RED) + I18n.translate("multiplayer.status.cannot_connect", new Object[0]);
                    serverEntry.playerCountLabel = "";
                    ServerEntryNetworkPart.this.ping(serverEntry);
                }
            }
        });
        try {
            clientConnection.send(new HandshakeC2SPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
            clientConnection.send(new QueryRequestC2SPacket());
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
        }
    }

    private void ping(final ServerEntry serverEntry) {
        final ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(ClientConnection.CLIENT_IO_GROUP.get())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) throws Exception {
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
                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                        super.channelActive(channelHandlerContext);
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
                            byteBuf.writeShort(7 + 2 * serverAddress.getAddress().length());
                            byteBuf.writeByte(127);
                            cs = serverAddress.getAddress().toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar(c);
                            }
                            byteBuf.writeInt(serverAddress.getPort());
                            channelHandlerContext.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            byteBuf.release();
                        }
                    }

                    protected void method_3005(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        short s = byteBuf.readUnsignedByte();
                        if (s == 255) {
                            String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                            String[] strings = Iterables.toArray(ZERO_SPLITTER.split(string), String.class);
                            if ("\u00a71".equals(strings[0])) {
                                int i = MathHelper.parseInt(strings[1], 0);
                                String string2 = strings[2];
                                String string3 = strings[3];
                                int j = MathHelper.parseInt(strings[4], -1);
                                int k = MathHelper.parseInt(strings[5], -1);
                                serverEntry.protocolVersion = -1;
                                serverEntry.version = string2;
                                serverEntry.label = string3;
                                serverEntry.playerCountLabel = (Object)((Object)Formatting.GRAY) + "" + j + "" + (Object)((Object)Formatting.DARK_GRAY) + "/" + (Object)((Object)Formatting.GRAY) + k;
                            }
                        }
                        channelHandlerContext.close();
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                        channelHandlerContext.close();
                    }

                    @Override
                    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
                        this.method_3005(channelHandlerContext, (ByteBuf)object);
                    }
                });
            }
        })).channel(NioSocketChannel.class)).connect(serverAddress.getAddress(), serverAddress.getPort());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void method_3000() {
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
    public void method_3004() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (!clientConnection.isOpen()) continue;
                iterator.remove();
                clientConnection.disconnect(new TranslatableText("multiplayer.status.cancelled", new Object[0]));
            }
        }
    }
}

