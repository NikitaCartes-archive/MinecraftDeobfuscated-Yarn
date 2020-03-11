/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.server.LanServerPinger;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanServerQueryManager {
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();

    @Environment(value=EnvType.CLIENT)
    public static class LanServerDetector
    extends Thread {
        private final LanServerEntryList entryList;
        private final InetAddress multicastAddress;
        private final MulticastSocket socket;

        public LanServerDetector(LanServerEntryList lanServerEntryList) throws IOException {
            super("LanServerDetector #" + THREAD_ID.incrementAndGet());
            this.entryList = lanServerEntryList;
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            this.socket = new MulticastSocket(4445);
            this.multicastAddress = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.multicastAddress);
        }

        @Override
        public void run() {
            byte[] bs = new byte[1024];
            while (!this.isInterrupted()) {
                DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length);
                try {
                    this.socket.receive(datagramPacket);
                } catch (SocketTimeoutException socketTimeoutException) {
                    continue;
                } catch (IOException iOException) {
                    LOGGER.error("Couldn't ping server", (Throwable)iOException);
                    break;
                }
                String string = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength(), StandardCharsets.UTF_8);
                LOGGER.debug("{}: {}", (Object)datagramPacket.getAddress(), (Object)string);
                this.entryList.addServer(string, datagramPacket.getAddress());
            }
            try {
                this.socket.leaveGroup(this.multicastAddress);
            } catch (IOException iOException) {
                // empty catch block
            }
            this.socket.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LanServerEntryList {
        private final List<LanServerInfo> serverEntries = Lists.newArrayList();
        private boolean dirty;

        public synchronized boolean needsUpdate() {
            return this.dirty;
        }

        public synchronized void markClean() {
            this.dirty = false;
        }

        public synchronized List<LanServerInfo> getServers() {
            return Collections.unmodifiableList(this.serverEntries);
        }

        public synchronized void addServer(String string, InetAddress inetAddress) {
            String string2 = LanServerPinger.parseAnnouncementMotd(string);
            String string3 = LanServerPinger.parseAnnouncementAddressPort(string);
            if (string3 == null) {
                return;
            }
            string3 = inetAddress.getHostAddress() + ":" + string3;
            boolean bl = false;
            for (LanServerInfo lanServerInfo : this.serverEntries) {
                if (!lanServerInfo.getAddressPort().equals(string3)) continue;
                lanServerInfo.updateLastTime();
                bl = true;
                break;
            }
            if (!bl) {
                this.serverEntries.add(new LanServerInfo(string2, string3));
                this.dirty = true;
            }
        }
    }
}

