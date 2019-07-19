/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.BufferHelper;
import net.minecraft.server.rcon.DataStreamHelper;
import net.minecraft.server.rcon.RconBase;
import net.minecraft.util.Util;

public class QueryResponseHandler
extends RconBase {
    private long lastQueryTime;
    private final int queryPort;
    private final int port;
    private final int maxPlayerCount;
    private final String motd;
    private final String levelName;
    private DatagramSocket socket;
    private final byte[] packetBuffer = new byte[1460];
    private DatagramPacket currentPacket;
    private final Map<SocketAddress, String> field_14448;
    private String ip;
    private String hostname;
    private final Map<SocketAddress, class_3365> field_14453;
    private final long field_14451;
    private final DataStreamHelper data;
    private long field_14450;

    public QueryResponseHandler(DedicatedServer dedicatedServer) {
        super(dedicatedServer, "Query Listener");
        this.queryPort = dedicatedServer.getProperties().queryPort;
        this.hostname = dedicatedServer.getHostname();
        this.port = dedicatedServer.getPort();
        this.motd = dedicatedServer.getMotd();
        this.maxPlayerCount = dedicatedServer.getMaxPlayerCount();
        this.levelName = dedicatedServer.getLevelName();
        this.field_14450 = 0L;
        this.ip = "0.0.0.0";
        if (this.hostname.isEmpty() || this.ip.equals(this.hostname)) {
            this.hostname = "0.0.0.0";
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                this.ip = inetAddress.getHostAddress();
            } catch (UnknownHostException unknownHostException) {
                this.warn("Unable to determine local host IP, please set server-ip in server.properties: " + unknownHostException.getMessage());
            }
        } else {
            this.ip = this.hostname;
        }
        this.field_14448 = Maps.newHashMap();
        this.data = new DataStreamHelper(1460);
        this.field_14453 = Maps.newHashMap();
        this.field_14451 = new Date().getTime();
    }

    private void reply(byte[] bs, DatagramPacket datagramPacket) throws IOException {
        this.socket.send(new DatagramPacket(bs, bs.length, datagramPacket.getSocketAddress()));
    }

    private boolean handle(DatagramPacket datagramPacket) throws IOException {
        byte[] bs = datagramPacket.getData();
        int i = datagramPacket.getLength();
        SocketAddress socketAddress = datagramPacket.getSocketAddress();
        this.log("Packet len " + i + " [" + socketAddress + "]");
        if (3 > i || -2 != bs[0] || -3 != bs[1]) {
            this.log("Invalid packet [" + socketAddress + "]");
            return false;
        }
        this.log("Packet '" + BufferHelper.toHex(bs[2]) + "' [" + socketAddress + "]");
        switch (bs[2]) {
            case 9: {
                this.method_14749(datagramPacket);
                this.log("Challenge [" + socketAddress + "]");
                return true;
            }
            case 0: {
                if (!this.method_14753(datagramPacket).booleanValue()) {
                    this.log("Invalid challenge [" + socketAddress + "]");
                    return false;
                }
                if (15 == i) {
                    this.reply(this.createRulesReply(datagramPacket), datagramPacket);
                    this.log("Rules [" + socketAddress + "]");
                    break;
                }
                DataStreamHelper dataStreamHelper = new DataStreamHelper(1460);
                dataStreamHelper.write(0);
                dataStreamHelper.write(this.method_14748(datagramPacket.getSocketAddress()));
                dataStreamHelper.writeBytes(this.motd);
                dataStreamHelper.writeBytes("SMP");
                dataStreamHelper.writeBytes(this.levelName);
                dataStreamHelper.writeBytes(Integer.toString(this.getCurrentPlayerCount()));
                dataStreamHelper.writeBytes(Integer.toString(this.maxPlayerCount));
                dataStreamHelper.writeShort((short)this.port);
                dataStreamHelper.writeBytes(this.ip);
                this.reply(dataStreamHelper.bytes(), datagramPacket);
                this.log("Status [" + socketAddress + "]");
            }
        }
        return true;
    }

    private byte[] createRulesReply(DatagramPacket datagramPacket) throws IOException {
        String[] strings;
        long l = Util.getMeasuringTimeMs();
        if (l < this.field_14450 + 5000L) {
            byte[] bs = this.data.bytes();
            byte[] cs = this.method_14748(datagramPacket.getSocketAddress());
            bs[1] = cs[0];
            bs[2] = cs[1];
            bs[3] = cs[2];
            bs[4] = cs[3];
            return bs;
        }
        this.field_14450 = l;
        this.data.reset();
        this.data.write(0);
        this.data.write(this.method_14748(datagramPacket.getSocketAddress()));
        this.data.writeBytes("splitnum");
        this.data.write(128);
        this.data.write(0);
        this.data.writeBytes("hostname");
        this.data.writeBytes(this.motd);
        this.data.writeBytes("gametype");
        this.data.writeBytes("SMP");
        this.data.writeBytes("game_id");
        this.data.writeBytes("MINECRAFT");
        this.data.writeBytes("version");
        this.data.writeBytes(this.server.getVersion());
        this.data.writeBytes("plugins");
        this.data.writeBytes(this.server.getPlugins());
        this.data.writeBytes("map");
        this.data.writeBytes(this.levelName);
        this.data.writeBytes("numplayers");
        this.data.writeBytes("" + this.getCurrentPlayerCount());
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

    private byte[] method_14748(SocketAddress socketAddress) {
        return this.field_14453.get(socketAddress).method_14758();
    }

    private Boolean method_14753(DatagramPacket datagramPacket) {
        SocketAddress socketAddress = datagramPacket.getSocketAddress();
        if (!this.field_14453.containsKey(socketAddress)) {
            return false;
        }
        byte[] bs = datagramPacket.getData();
        if (this.field_14453.get(socketAddress).method_14756() != BufferHelper.getIntBE(bs, 7, datagramPacket.getLength())) {
            return false;
        }
        return true;
    }

    private void method_14749(DatagramPacket datagramPacket) throws IOException {
        class_3365 lv = new class_3365(datagramPacket);
        this.field_14453.put(datagramPacket.getSocketAddress(), lv);
        this.reply(lv.method_14757(), datagramPacket);
    }

    private void method_14746() {
        if (!this.running) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l < this.lastQueryTime + 30000L) {
            return;
        }
        this.lastQueryTime = l;
        Iterator<Map.Entry<SocketAddress, class_3365>> iterator = this.field_14453.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SocketAddress, class_3365> entry = iterator.next();
            if (!entry.getValue().method_14755(l).booleanValue()) continue;
            iterator.remove();
        }
    }

    @Override
    public void run() {
        this.info("Query running on " + this.hostname + ":" + this.queryPort);
        this.lastQueryTime = Util.getMeasuringTimeMs();
        this.currentPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(this.currentPacket);
                    this.method_14746();
                    this.handle(this.currentPacket);
                } catch (SocketTimeoutException socketTimeoutException) {
                    this.method_14746();
                } catch (PortUnreachableException socketTimeoutException) {
                } catch (IOException iOException) {
                    this.handleIoException(iOException);
                }
            }
        } finally {
            this.forceClose();
        }
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        if (0 >= this.queryPort || 65535 < this.queryPort) {
            this.warn("Invalid query port " + this.queryPort + " found in server.properties (queries disabled)");
            return;
        }
        if (this.initialize()) {
            super.start();
        }
    }

    private void handleIoException(Exception exception) {
        if (!this.running) {
            return;
        }
        this.warn("Unexpected exception, buggy JRE? (" + exception + ")");
        if (!this.initialize()) {
            this.logError("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
        }
    }

    private boolean initialize() {
        try {
            this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname));
            this.registerSocket(this.socket);
            this.socket.setSoTimeout(500);
            return true;
        } catch (SocketException socketException) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Socket): " + socketException.getMessage());
        } catch (UnknownHostException unknownHostException) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Unknown Host): " + unknownHostException.getMessage());
        } catch (Exception exception) {
            this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (E): " + exception.getMessage());
        }
        return false;
    }

    class class_3365 {
        private final long field_14459 = new Date().getTime();
        private final int field_14458;
        private final byte[] field_14460;
        private final byte[] field_14461;
        private final String field_14462;

        public class_3365(DatagramPacket datagramPacket) {
            byte[] bs = datagramPacket.getData();
            this.field_14460 = new byte[4];
            this.field_14460[0] = bs[3];
            this.field_14460[1] = bs[4];
            this.field_14460[2] = bs[5];
            this.field_14460[3] = bs[6];
            this.field_14462 = new String(this.field_14460, StandardCharsets.UTF_8);
            this.field_14458 = new Random().nextInt(0x1000000);
            this.field_14461 = String.format("\t%s%d\u0000", this.field_14462, this.field_14458).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean method_14755(long l) {
            return this.field_14459 < l;
        }

        public int method_14756() {
            return this.field_14458;
        }

        public byte[] method_14757() {
            return this.field_14461;
        }

        public byte[] method_14758() {
            return this.field_14460;
        }
    }
}

