/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.BufferHelper;
import net.minecraft.server.rcon.DataStreamHelper;
import net.minecraft.server.rcon.RconBase;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class QueryResponseHandler
extends RconBase {
    private static final Logger field_23963 = LogUtils.getLogger();
    private static final String GAME_TYPE = "SMP";
    private static final String GAME_ID = "MINECRAFT";
    private static final long CLEAN_UP_THRESHOLD = 30000L;
    private static final long field_29798 = 5000L;
    private long lastQueryTime;
    private final int queryPort;
    private final int port;
    private final int maxPlayerCount;
    private final String motd;
    private final String levelName;
    private DatagramSocket socket;
    private final byte[] packetBuffer = new byte[1460];
    private String ip;
    private String hostname;
    private final Map<SocketAddress, Query> queries;
    private final DataStreamHelper data;
    private long lastResponseTime;
    private final DedicatedServer server;

    private QueryResponseHandler(DedicatedServer server, int queryPort) {
        super("Query Listener");
        this.server = server;
        this.queryPort = queryPort;
        this.hostname = server.getHostname();
        this.port = server.getPort();
        this.motd = server.getMotd();
        this.maxPlayerCount = server.getMaxPlayerCount();
        this.levelName = server.getLevelName();
        this.lastResponseTime = 0L;
        this.ip = "0.0.0.0";
        if (this.hostname.isEmpty() || this.ip.equals(this.hostname)) {
            this.hostname = "0.0.0.0";
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                this.ip = inetAddress.getHostAddress();
            } catch (UnknownHostException unknownHostException) {
                field_23963.warn("Unable to determine local host IP, please set server-ip in server.properties", unknownHostException);
            }
        } else {
            this.ip = this.hostname;
        }
        this.data = new DataStreamHelper(1460);
        this.queries = Maps.newHashMap();
    }

    @Nullable
    public static QueryResponseHandler create(DedicatedServer server) {
        int i = server.getProperties().queryPort;
        if (0 >= i || 65535 < i) {
            field_23963.warn("Invalid query port {} found in server.properties (queries disabled)", (Object)i);
            return null;
        }
        QueryResponseHandler queryResponseHandler = new QueryResponseHandler(server, i);
        if (!queryResponseHandler.start()) {
            return null;
        }
        return queryResponseHandler;
    }

    private void reply(byte[] buf, DatagramPacket packet) throws IOException {
        this.socket.send(new DatagramPacket(buf, buf.length, packet.getSocketAddress()));
    }

    private boolean handle(DatagramPacket packet) throws IOException {
        byte[] bs = packet.getData();
        int i = packet.getLength();
        SocketAddress socketAddress = packet.getSocketAddress();
        field_23963.debug("Packet len {} [{}]", (Object)i, (Object)socketAddress);
        if (3 > i || -2 != bs[0] || -3 != bs[1]) {
            field_23963.debug("Invalid packet [{}]", (Object)socketAddress);
            return false;
        }
        field_23963.debug("Packet '{}' [{}]", (Object)BufferHelper.toHex(bs[2]), (Object)socketAddress);
        switch (bs[2]) {
            case 9: {
                this.createQuery(packet);
                field_23963.debug("Challenge [{}]", (Object)socketAddress);
                return true;
            }
            case 0: {
                if (!this.isValidQuery(packet).booleanValue()) {
                    field_23963.debug("Invalid challenge [{}]", (Object)socketAddress);
                    return false;
                }
                if (15 == i) {
                    this.reply(this.createRulesReply(packet), packet);
                    field_23963.debug("Rules [{}]", (Object)socketAddress);
                    break;
                }
                DataStreamHelper dataStreamHelper = new DataStreamHelper(1460);
                dataStreamHelper.write(0);
                dataStreamHelper.write(this.getMessageBytes(packet.getSocketAddress()));
                dataStreamHelper.writeBytes(this.motd);
                dataStreamHelper.writeBytes(GAME_TYPE);
                dataStreamHelper.writeBytes(this.levelName);
                dataStreamHelper.writeBytes(Integer.toString(this.server.getCurrentPlayerCount()));
                dataStreamHelper.writeBytes(Integer.toString(this.maxPlayerCount));
                dataStreamHelper.writeShort((short)this.port);
                dataStreamHelper.writeBytes(this.ip);
                this.reply(dataStreamHelper.bytes(), packet);
                field_23963.debug("Status [{}]", (Object)socketAddress);
            }
        }
        return true;
    }

    private byte[] createRulesReply(DatagramPacket packet) throws IOException {
        String[] strings;
        long l = Util.getMeasuringTimeMs();
        if (l < this.lastResponseTime + 5000L) {
            byte[] bs = this.data.bytes();
            byte[] cs = this.getMessageBytes(packet.getSocketAddress());
            bs[1] = cs[0];
            bs[2] = cs[1];
            bs[3] = cs[2];
            bs[4] = cs[3];
            return bs;
        }
        this.lastResponseTime = l;
        this.data.reset();
        this.data.write(0);
        this.data.write(this.getMessageBytes(packet.getSocketAddress()));
        this.data.writeBytes("splitnum");
        this.data.write(128);
        this.data.write(0);
        this.data.writeBytes("hostname");
        this.data.writeBytes(this.motd);
        this.data.writeBytes("gametype");
        this.data.writeBytes(GAME_TYPE);
        this.data.writeBytes("game_id");
        this.data.writeBytes(GAME_ID);
        this.data.writeBytes("version");
        this.data.writeBytes(this.server.getVersion());
        this.data.writeBytes("plugins");
        this.data.writeBytes(this.server.getPlugins());
        this.data.writeBytes("map");
        this.data.writeBytes(this.levelName);
        this.data.writeBytes("numplayers");
        this.data.writeBytes("" + this.server.getCurrentPlayerCount());
        this.data.writeBytes("maxplayers");
        this.data.writeBytes("" + this.maxPlayerCount);
        this.data.writeBytes("hostport");
        this.data.writeBytes("" + this.port);
        this.data.writeBytes("hostip");
        this.data.writeBytes(this.ip);
        this.data.write(0);
        this.data.write(1);
        this.data.writeBytes("player_");
        this.data.write(0);
        for (String string : strings = this.server.getPlayerNames()) {
            this.data.writeBytes(string);
        }
        this.data.write(0);
        return this.data.bytes();
    }

    private byte[] getMessageBytes(SocketAddress address) {
        return this.queries.get(address).getMessageBytes();
    }

    private Boolean isValidQuery(DatagramPacket packet) {
        SocketAddress socketAddress = packet.getSocketAddress();
        if (!this.queries.containsKey(socketAddress)) {
            return false;
        }
        byte[] bs = packet.getData();
        return this.queries.get(socketAddress).getId() == BufferHelper.getIntBE(bs, 7, packet.getLength());
    }

    private void createQuery(DatagramPacket packet) throws IOException {
        Query query = new Query(packet);
        this.queries.put(packet.getSocketAddress(), query);
        this.reply(query.getReplyBuf(), packet);
    }

    private void cleanUp() {
        if (!this.running) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l < this.lastQueryTime + 30000L) {
            return;
        }
        this.lastQueryTime = l;
        this.queries.values().removeIf(query -> query.startedBefore(l));
    }

    @Override
    public void run() {
        field_23963.info("Query running on {}:{}", (Object)this.hostname, (Object)this.queryPort);
        this.lastQueryTime = Util.getMeasuringTimeMs();
        DatagramPacket datagramPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(datagramPacket);
                    this.cleanUp();
                    this.handle(datagramPacket);
                } catch (SocketTimeoutException socketTimeoutException) {
                    this.cleanUp();
                } catch (PortUnreachableException socketTimeoutException) {
                } catch (IOException iOException) {
                    this.handleIoException(iOException);
                }
            }
        } finally {
            field_23963.debug("closeSocket: {}:{}", (Object)this.hostname, (Object)this.queryPort);
            this.socket.close();
        }
    }

    @Override
    public boolean start() {
        if (this.running) {
            return true;
        }
        if (!this.initialize()) {
            return false;
        }
        return super.start();
    }

    private void handleIoException(Exception e) {
        if (!this.running) {
            return;
        }
        field_23963.warn("Unexpected exception", e);
        if (!this.initialize()) {
            field_23963.error("Failed to recover from exception, shutting down!");
            this.running = false;
        }
    }

    private boolean initialize() {
        try {
            this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname));
            this.socket.setSoTimeout(500);
            return true;
        } catch (Exception exception) {
            field_23963.warn("Unable to initialise query system on {}:{}", this.hostname, this.queryPort, exception);
            return false;
        }
    }

    static class Query {
        private final long startTime = new Date().getTime();
        private final int id;
        private final byte[] messageBytes;
        private final byte[] replyBuf;
        private final String message;

        public Query(DatagramPacket packet) {
            byte[] bs = packet.getData();
            this.messageBytes = new byte[4];
            this.messageBytes[0] = bs[3];
            this.messageBytes[1] = bs[4];
            this.messageBytes[2] = bs[5];
            this.messageBytes[3] = bs[6];
            this.message = new String(this.messageBytes, StandardCharsets.UTF_8);
            this.id = Random.create().nextInt(0x1000000);
            this.replyBuf = String.format(Locale.ROOT, "\t%s%d\u0000", this.message, this.id).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean startedBefore(long lastQueryTime) {
            return this.startTime < lastQueryTime;
        }

        public int getId() {
            return this.id;
        }

        public byte[] getReplyBuf() {
            return this.replyBuf;
        }

        public byte[] getMessageBytes() {
            return this.messageBytes;
        }

        public String getMessage() {
            return this.message;
        }
    }
}

