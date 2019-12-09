/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanServerPinger
extends Thread {
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private final String motd;
    private final DatagramSocket socket;
    private boolean isRunning = true;
    private final String addressPort;

    public LanServerPinger(String motd, String addressPort) throws IOException {
        super("LanServerPinger #" + THREAD_ID.incrementAndGet());
        this.motd = motd;
        this.addressPort = addressPort;
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        String string = LanServerPinger.createAnnouncement(this.motd, this.addressPort);
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        while (!this.isInterrupted() && this.isRunning) {
            try {
                InetAddress inetAddress = InetAddress.getByName("224.0.2.60");
                DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length, inetAddress, 4445);
                this.socket.send(datagramPacket);
            } catch (IOException iOException) {
                LOGGER.warn("LanServerPinger: {}", (Object)iOException.getMessage());
                break;
            }
            try {
                LanServerPinger.sleep(1500L);
            } catch (InterruptedException interruptedException) {}
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.isRunning = false;
    }

    public static String createAnnouncement(String motd, String addressPort) {
        return "[MOTD]" + motd + "[/MOTD][AD]" + addressPort + "[/AD]";
    }

    public static String parseAnnouncementMotd(String announcement) {
        int i = announcement.indexOf("[MOTD]");
        if (i < 0) {
            return "missing no";
        }
        int j = announcement.indexOf("[/MOTD]", i + "[MOTD]".length());
        if (j < i) {
            return "missing no";
        }
        return announcement.substring(i + "[MOTD]".length(), j);
    }

    public static String parseAnnouncementAddressPort(String announcement) {
        int i = announcement.indexOf("[/MOTD]");
        if (i < 0) {
            return null;
        }
        int j = announcement.indexOf("[/MOTD]", i + "[/MOTD]".length());
        if (j >= 0) {
            return null;
        }
        int k = announcement.indexOf("[AD]", i + "[/MOTD]".length());
        if (k < 0) {
            return null;
        }
        int l = announcement.indexOf("[/AD]", k + "[AD]".length());
        if (l < k) {
            return null;
        }
        return announcement.substring(k + "[AD]".length(), l);
    }
}

