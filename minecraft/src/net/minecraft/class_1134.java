package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1134 {
	private static final AtomicInteger field_5531 = new AtomicInteger(0);
	private static final Logger field_5532 = LogManager.getLogger();

	@Environment(EnvType.CLIENT)
	public static class class_1135 extends Thread {
		private final class_1134.class_1136 field_5533;
		private final InetAddress field_5534;
		private final MulticastSocket field_5535;

		public class_1135(class_1134.class_1136 arg) throws IOException {
			super("LanServerDetector #" + class_1134.field_5531.incrementAndGet());
			this.field_5533 = arg;
			this.setDaemon(true);
			this.setUncaughtExceptionHandler(new class_140(class_1134.field_5532));
			this.field_5535 = new MulticastSocket(4445);
			this.field_5534 = InetAddress.getByName("224.0.2.60");
			this.field_5535.setSoTimeout(5000);
			this.field_5535.joinGroup(this.field_5534);
		}

		public void run() {
			byte[] bs = new byte[1024];

			while (!this.isInterrupted()) {
				DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length);

				try {
					this.field_5535.receive(datagramPacket);
				} catch (SocketTimeoutException var5) {
					continue;
				} catch (IOException var6) {
					class_1134.field_5532.error("Couldn't ping server", (Throwable)var6);
					break;
				}

				String string = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength(), StandardCharsets.UTF_8);
				class_1134.field_5532.debug("{}: {}", datagramPacket.getAddress(), string);
				this.field_5533.method_4824(string, datagramPacket.getAddress());
			}

			try {
				this.field_5535.leaveGroup(this.field_5534);
			} catch (IOException var4) {
			}

			this.field_5535.close();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1136 {
		private final List<class_1131> field_5536 = Lists.<class_1131>newArrayList();
		private boolean field_5537;

		public synchronized boolean method_4823() {
			return this.field_5537;
		}

		public synchronized void method_4825() {
			this.field_5537 = false;
		}

		public synchronized List<class_1131> method_4826() {
			return Collections.unmodifiableList(this.field_5536);
		}

		public synchronized void method_4824(String string, InetAddress inetAddress) {
			String string2 = class_1133.method_4819(string);
			String string3 = class_1133.method_4820(string);
			if (string3 != null) {
				string3 = inetAddress.getHostAddress() + ":" + string3;
				boolean bl = false;

				for (class_1131 lv : this.field_5536) {
					if (lv.method_4812().equals(string3)) {
						lv.method_4814();
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.field_5536.add(new class_1131(string2, string3));
					this.field_5537 = true;
				}
			}
		}
	}
}
