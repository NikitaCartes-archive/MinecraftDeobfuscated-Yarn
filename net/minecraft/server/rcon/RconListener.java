/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.rcon.RconBase;
import net.minecraft.server.rcon.RconClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class RconListener
extends RconBase {
    private static final Logger SERVER_LOGGER = LogManager.getLogger();
    private final ServerSocket listener;
    private final String password;
    private final List<RconClient> clients = Lists.newArrayList();
    private final DedicatedServer server;

    private RconListener(DedicatedServer dedicatedServer, ServerSocket serverSocket, String string) {
        super("RCON Listener");
        this.server = dedicatedServer;
        this.listener = serverSocket;
        this.password = string;
    }

    private void removeStoppedClients() {
        this.clients.removeIf(rconClient -> !rconClient.isRunning());
    }

    @Override
    public void run() {
        try {
            while (this.running) {
                try {
                    Socket socket = this.listener.accept();
                    RconClient rconClient = new RconClient(this.server, this.password, socket);
                    rconClient.start();
                    this.clients.add(rconClient);
                    this.removeStoppedClients();
                } catch (SocketTimeoutException socketTimeoutException) {
                    this.removeStoppedClients();
                } catch (IOException iOException) {
                    if (!this.running) continue;
                    SERVER_LOGGER.info("IO exception: ", (Throwable)iOException);
                }
            }
        } finally {
            this.closeSocket(this.listener);
        }
    }

    @Nullable
    public static RconListener create(DedicatedServer server) {
        int i;
        ServerPropertiesHandler serverPropertiesHandler = server.getProperties();
        String string = server.getHostname();
        if (string.isEmpty()) {
            string = "0.0.0.0";
        }
        if (0 >= (i = serverPropertiesHandler.rconPort) || 65535 < i) {
            SERVER_LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", (Object)i);
            return null;
        }
        String string2 = serverPropertiesHandler.rconPassword;
        if (string2.isEmpty()) {
            SERVER_LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
            return null;
        }
        try {
            ServerSocket serverSocket = new ServerSocket(i, 0, InetAddress.getByName(string));
            serverSocket.setSoTimeout(500);
            RconListener rconListener = new RconListener(server, serverSocket, string2);
            if (!rconListener.start()) {
                return null;
            }
            SERVER_LOGGER.info("RCON running on {}:{}", (Object)string, (Object)i);
            return rconListener;
        } catch (IOException iOException) {
            SERVER_LOGGER.warn("Unable to initialise RCON on {}:{}", (Object)string, (Object)i, (Object)iOException);
            return null;
        }
    }

    @Override
    public void stop() {
        this.running = false;
        this.closeSocket(this.listener);
        super.stop();
        for (RconClient rconClient : this.clients) {
            if (!rconClient.isRunning()) continue;
            rconClient.stop();
        }
        this.clients.clear();
    }

    private void closeSocket(ServerSocket socket) {
        SERVER_LOGGER.debug("closeSocket: {}", (Object)socket);
        try {
            socket.close();
        } catch (IOException iOException) {
            SERVER_LOGGER.warn("Failed to close socket", (Throwable)iOException);
        }
    }
}

