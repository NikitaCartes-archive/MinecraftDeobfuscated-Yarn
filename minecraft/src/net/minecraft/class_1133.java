package net.minecraft;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1133 extends Thread {
	private static final AtomicInteger field_5525 = new AtomicInteger(0);
	private static final Logger field_5529 = LogManager.getLogger();
	private final String field_5526;
	private final DatagramSocket field_5528;
	private boolean field_5527 = true;
	private final String field_5530;

	public class_1133(String string, String string2) throws IOException {
		super("LanServerPinger #" + field_5525.incrementAndGet());
		this.field_5526 = string;
		this.field_5530 = string2;
		this.setDaemon(true);
		this.setUncaughtExceptionHandler(new class_140(field_5529));
		this.field_5528 = new DatagramSocket();
	}

	public void run() {
		String string = method_4818(this.field_5526, this.field_5530);
		byte[] bs = string.getBytes(StandardCharsets.UTF_8);

		while (!this.isInterrupted() && this.field_5527) {
			try {
				InetAddress inetAddress = InetAddress.getByName("224.0.2.60");
				DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length, inetAddress, 4445);
				this.field_5528.send(datagramPacket);
			} catch (IOException var6) {
				field_5529.warn("LanServerPinger: {}", var6.getMessage());
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
		this.field_5527 = false;
	}

	public static String method_4818(String string, String string2) {
		return "[MOTD]" + string + "[/MOTD][AD]" + string2 + "[/AD]";
	}

	public static String method_4819(String string) {
		int i = string.indexOf("[MOTD]");
		if (i < 0) {
			return "missing no";
		} else {
			int j = string.indexOf("[/MOTD]", i + "[MOTD]".length());
			return j < i ? "missing no" : string.substring(i + "[MOTD]".length(), j);
		}
	}

	public static String method_4820(String string) {
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
