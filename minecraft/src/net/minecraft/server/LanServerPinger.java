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

@Environment(EnvType.CLIENT)
public class LanServerPinger extends Thread {
	private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private final String motd;
	private final DatagramSocket socket;
	private boolean isRunning = true;
	private final String addressPort;

	public LanServerPinger(String string, String string2) throws IOException {
		super("LanServerPinger #" + THREAD_ID.incrementAndGet());
		this.motd = string;
		this.addressPort = string2;
		this.setDaemon(true);
		this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		this.socket = new DatagramSocket();
	}

	public void run() {
		String string = createAnnouncement(this.motd, this.addressPort);
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);

		while (!this.isInterrupted() && this.isRunning) {
			try {
				InetAddress inetAddress = InetAddress.getByName("224.0.2.60");
				DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length, inetAddress, 4445);
				this.socket.send(datagramPacket);
			} catch (IOException var6) {
				LOGGER.warn("LanServerPinger: {}", var6.getMessage());
				break;
			}

			try {
				sleep(1500L);
			} catch (InterruptedException var5) {
			}
		}
	}

	public void interrupt() {
		super.interrupt();
		this.isRunning = false;
	}

	public static String createAnnouncement(String string, String string2) {
		return "[MOTD]" + string + "[/MOTD][AD]" + string2 + "[/AD]";
	}

	public static String parseAnnouncementMotd(String string) {
		int i = string.indexOf("[MOTD]");
		if (i < 0) {
			return "missing no";
		} else {
			int j = string.indexOf("[/MOTD]", i + "[MOTD]".length());
			return j < i ? "missing no" : string.substring(i + "[MOTD]".length(), j);
		}
	}

	public static String parseAnnouncementAddressPort(String string) {
		int i = string.indexOf("[/MOTD]");
		if (i < 0) {
			return null;
		} else {
			int j = string.indexOf("[/MOTD]", i + "[/MOTD]".length());
			if (j >= 0) {
				return null;
			} else {
				int k = string.indexOf("[AD]", i + "[/MOTD]".length());
				if (k < 0) {
					return null;
				} else {
					int l = string.indexOf("[/AD]", k + "[AD]".length());
					return l < k ? null : string.substring(k + "[AD]".length(), l);
				}
			}
		}
	}
}
