/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.rcon.BufferHelper;
import net.minecraft.server.rcon.RconBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconClient
extends RconBase {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean authenticated;
    private Socket socket;
    private final byte[] packetBuffer = new byte[1460];
    private final String password;
    private final DedicatedServer server;

    RconClient(DedicatedServer server, String password, Socket socket) {
        super("RCON Client " + socket.getInetAddress());
        this.server = server;
        this.socket = socket;
        try {
            this.socket.setSoTimeout(0);
        } catch (Exception exception) {
            this.running = false;
        }
        this.password = password;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            while (this.running) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
                int i = bufferedInputStream.read(this.packetBuffer, 0, 1460);
                if (10 > i) {
                    return;
                }
                int j = 0;
                int k = BufferHelper.getIntLE(this.packetBuffer, 0, i);
                if (k != i - 4) {
                    return;
                }
                int l = BufferHelper.getIntLE(this.packetBuffer, j += 4, i);
                int m = BufferHelper.getIntLE(this.packetBuffer, j += 4);
                j += 4;
                switch (m) {
                    case 3: {
                        String string = BufferHelper.getString(this.packetBuffer, j, i);
                        j += string.length();
                        if (!string.isEmpty() && string.equals(this.password)) {
                            this.authenticated = true;
                            this.respond(l, 2, "");
                            break;
                        }
                        this.authenticated = false;
                        this.fail();
                        break;
                    }
                    case 2: {
                        if (this.authenticated) {
                            String string2 = BufferHelper.getString(this.packetBuffer, j, i);
                            try {
                                this.respond(l, this.server.executeRconCommand(string2));
                            } catch (Exception exception) {
                                this.respond(l, "Error executing: " + string2 + " (" + exception.getMessage() + ")");
                            }
                            break;
                        }
                        this.fail();
                        break;
                    }
                    default: {
                        this.respond(l, String.format("Unknown request %s", Integer.toHexString(m)));
                    }
                }
            }
        } catch (SocketTimeoutException bufferedInputStream) {
        } catch (IOException bufferedInputStream) {
        } catch (Exception exception2) {
            LOGGER.error("Exception whilst parsing RCON input", (Throwable)exception2);
        } finally {
            this.close();
            LOGGER.info("Thread {} shutting down", (Object)this.description);
            this.running = false;
        }
    }

    private void respond(int sessionToken, int responseType, String message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1248);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        byte[] bs = message.getBytes("UTF-8");
        dataOutputStream.writeInt(Integer.reverseBytes(bs.length + 10));
        dataOutputStream.writeInt(Integer.reverseBytes(sessionToken));
        dataOutputStream.writeInt(Integer.reverseBytes(responseType));
        dataOutputStream.write(bs);
        dataOutputStream.write(0);
        dataOutputStream.write(0);
        this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
    }

    private void fail() throws IOException {
        this.respond(-1, 2, "");
    }

    private void respond(int sessionToken, String message) throws IOException {
        int j;
        int i = message.length();
        do {
            j = 4096 <= i ? 4096 : i;
            this.respond(sessionToken, 0, message.substring(0, j));
        } while (0 != (i = (message = message.substring(j)).length()));
    }

    @Override
    public void stop() {
        this.running = false;
        this.close();
        super.stop();
    }

    private void close() {
        if (null == this.socket) {
            return;
        }
        try {
            this.socket.close();
        } catch (IOException iOException) {
            LOGGER.warn("Failed to close socket", (Throwable)iOException);
        }
        this.socket = null;
    }
}

