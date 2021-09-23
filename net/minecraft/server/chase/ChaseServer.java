/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.chase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ChaseCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChaseServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String ip;
    private final int port;
    private final PlayerManager playerManager;
    private final int interval;
    private volatile boolean running;
    @Nullable
    private ServerSocket socket;
    private final CopyOnWriteArrayList<Socket> clientSockets = new CopyOnWriteArrayList();

    public ChaseServer(String ip, int port, PlayerManager playerManager, int interval) {
        this.ip = ip;
        this.port = port;
        this.playerManager = playerManager;
        this.interval = interval;
    }

    public void start() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            LOGGER.warn("Remote control server was asked to start, but it is already running. Will ignore.");
            return;
        }
        this.running = true;
        this.socket = new ServerSocket(this.port, 50, InetAddress.getByName(this.ip));
        Thread thread = new Thread(this::runAcceptor, "chase-server-acceptor");
        thread.setDaemon(true);
        thread.start();
        Thread thread2 = new Thread(this::runSender, "chase-server-sender");
        thread2.setDaemon(true);
        thread2.start();
    }

    private void runSender() {
        while (this.running) {
            if (!this.clientSockets.isEmpty()) {
                String string = this.getTeleportMessage();
                if (string != null) {
                    byte[] bs = string.getBytes(StandardCharsets.US_ASCII);
                    for (Socket socket : this.clientSockets) {
                        if (socket.isClosed()) continue;
                        Util.getIoWorkerExecutor().submit(() -> {
                            try {
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(bs);
                                outputStream.flush();
                            } catch (IOException iOException) {
                                LOGGER.info("Remote control client socket got an IO exception and will be closed", (Throwable)iOException);
                                IOUtils.closeQuietly(socket);
                            }
                        });
                    }
                }
                List list = this.clientSockets.stream().filter(Socket::isClosed).collect(Collectors.toList());
                this.clientSockets.removeAll(list);
            }
            if (!this.running) continue;
            try {
                Thread.sleep(this.interval);
            } catch (InterruptedException interruptedException) {}
        }
    }

    public void stop() {
        this.running = false;
        IOUtils.closeQuietly(this.socket);
        this.socket = null;
    }

    private void runAcceptor() {
        try {
            while (this.running) {
                if (this.socket == null) continue;
                LOGGER.info("Remote control server is listening for connections on port {}", (Object)this.port);
                Socket socket = this.socket.accept();
                LOGGER.info("Remote control server received client connection on port {}", (Object)socket.getPort());
                this.clientSockets.add(socket);
            }
        } catch (ClosedByInterruptException closedByInterruptException) {
            if (this.running) {
                LOGGER.info("Remote control server closed by interrupt");
            }
        } catch (IOException iOException) {
            if (this.running) {
                LOGGER.error("Remote control server closed because of an IO exception", (Throwable)iOException);
            }
        } finally {
            IOUtils.closeQuietly(this.socket);
        }
        LOGGER.info("Remote control server is now stopped");
        this.running = false;
    }

    @Nullable
    private String getTeleportMessage() {
        List<ServerPlayerEntity> list = this.playerManager.getPlayerList();
        if (list.isEmpty()) {
            return null;
        }
        ServerPlayerEntity serverPlayerEntity = list.get(0);
        String string = (String)ChaseCommand.DIMENSIONS.inverse().get(serverPlayerEntity.getWorld().getRegistryKey());
        if (string == null) {
            return null;
        }
        return String.format(Locale.ROOT, "t %s %.2f %.2f %.2f %.2f %.2f\n", string, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), Float.valueOf(serverPlayerEntity.getYaw()), Float.valueOf(serverPlayerEntity.getPitch()));
    }
}

