/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.rcon.RconBase;
import net.minecraft.server.rcon.RconClient;

public class RconServer
extends RconBase {
    private final int port;
    private String hostname;
    private ServerSocket listener;
    private final String password;
    private Map<SocketAddress, RconClient> clients;

    public RconServer(DedicatedServer dedicatedServer) {
        super(dedicatedServer, "RCON Listener");
        ServerPropertiesHandler serverPropertiesHandler = dedicatedServer.getProperties();
        this.port = serverPropertiesHandler.rconPort;
        this.password = serverPropertiesHandler.rconPassword;
        this.hostname = dedicatedServer.getHostname();
        if (this.hostname.isEmpty()) {
            this.hostname = "0.0.0.0";
        }
        this.cleanClientList();
        this.listener = null;
    }

    private void cleanClientList() {
        this.clients = Maps.newHashMap();
    }

    private void removeStoppedClients() {
        Iterator<Map.Entry<SocketAddress, RconClient>> iterator = this.clients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SocketAddress, RconClient> entry = iterator.next();
            if (entry.getValue().isRunning()) continue;
            iterator.remove();
        }
    }

    @Override
    public void run() {
        this.info("RCON running on " + this.hostname + ":" + this.port);
        try {
            while (this.running) {
                try {
                    Socket socket = this.listener.accept();
                    socket.setSoTimeout(500);
                    RconClient rconClient = new RconClient(this.server, this.password, socket);
                    rconClient.start();
                    this.clients.put(socket.getRemoteSocketAddress(), rconClient);
                    this.removeStoppedClients();
                } catch (SocketTimeoutException socketTimeoutException) {
                    this.removeStoppedClients();
                } catch (IOException iOException) {
                    if (!this.running) continue;
                    this.info("IO: " + iOException.getMessage());
                }
            }
        } finally {
            this.closeSocket(this.listener);
        }
    }

    @Override
    public void start() {
        if (this.password.isEmpty()) {
            this.warn("No rcon password set in server.properties, rcon disabled!");
            return;
        }
        if (0 >= this.port || 65535 < this.port) {
            this.warn("Invalid rcon port " + this.port + " found in server.properties, rcon disabled!");
            return;
        }
        if (this.running) {
            return;
        }
        try {
            this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
            this.listener.setSoTimeout(500);
            super.start();
        } catch (IOException iOException) {
            this.warn("Unable to initialise rcon on " + this.hostname + ":" + this.port + " : " + iOException.getMessage());
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (Map.Entry<SocketAddress, RconClient> entry : this.clients.entrySet()) {
            entry.getValue().stop();
        }
        this.closeSocket(this.listener);
        this.cleanClientList();
    }
}

