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
    private final DedicatedServer field_23965;

    RconClient(DedicatedServer dedicatedServer, String string, Socket socket) {
        super("RCON Client " + socket.getInetAddress());
        this.field_23965 = dedicatedServer;
        this.socket = socket;
        try {
            this.socket.setSoTimeout(0);
        } catch (Exception exception) {
            this.running = false;
        }
        this.password = string;
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
                            this.method_14790(l, 2, "");
                            break;
                        }
                        this.authenticated = false;
                        this.method_14787();
                        break;
                    }
                    case 2: {
                        if (this.authenticated) {
                            String string2 = BufferHelper.getString(this.packetBuffer, j, i);
                            try {
                                this.method_14789(l, this.field_23965.executeRconCommand(string2));
                            } catch (Exception exception) {
                                this.method_14789(l, "Error executing: " + string2 + " (" + exception.getMessage() + ")");
                            }
                            break;
                        }
                        this.method_14787();
                        break;
                    }
                    default: {
                        this.method_14789(l, String.format("Unknown request %s", Integer.toHexString(m)));
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

    private void method_14790(int i, int j, String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1248);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        byte[] bs = string.getBytes("UTF-8");
        dataOutputStream.writeInt(Integer.reverseBytes(bs.length + 10));
        dataOutputStream.writeInt(Integer.reverseBytes(i));
        dataOutputStream.writeInt(Integer.reverseBytes(j));
        dataOutputStream.write(bs);
        dataOutputStream.write(0);
        dataOutputStream.write(0);
        this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
    }

    private void method_14787() throws IOException {
        this.method_14790(-1, 2, "");
    }

    private void method_14789(int i, String string) throws IOException {
        int k;
        int j = string.length();
        do {
            k = 4096 <= j ? 4096 : j;
            this.method_14790(i, 0, string.substring(0, k));
        } while (0 != (j = (string = string.substring(k)).length()));
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

