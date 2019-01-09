package net.minecraft;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class class_3364 extends class_3359 {
	private long field_14443;
	private final int field_14442;
	private final int field_14457;
	private final int field_14456;
	private final String field_14445;
	private final String field_14447;
	private DatagramSocket field_14449;
	private final byte[] field_14452 = new byte[1460];
	private DatagramPacket field_14455;
	private final Map<SocketAddress, String> field_14448;
	private String field_14444;
	private String field_14454;
	private final Map<SocketAddress, class_3364.class_3365> field_14453;
	private final long field_14451;
	private final class_3345 field_14446;
	private long field_14450;

	public class_3364(class_2994 arg) {
		super(arg, "Query Listener");
		this.field_14442 = arg.method_16705().field_16831;
		this.field_14454 = arg.method_12929();
		this.field_14457 = arg.method_12918();
		this.field_14445 = arg.method_12930();
		this.field_14456 = arg.method_3802();
		this.field_14447 = arg.method_3865();
		this.field_14450 = 0L;
		this.field_14444 = "0.0.0.0";
		if (!this.field_14454.isEmpty() && !this.field_14444.equals(this.field_14454)) {
			this.field_14444 = this.field_14454;
		} else {
			this.field_14454 = "0.0.0.0";

			try {
				InetAddress inetAddress = InetAddress.getLocalHost();
				this.field_14444 = inetAddress.getHostAddress();
			} catch (UnknownHostException var3) {
				this.method_14727("Unable to determine local host IP, please set server-ip in server.properties: " + var3.getMessage());
			}
		}

		this.field_14448 = Maps.<SocketAddress, String>newHashMap();
		this.field_14446 = new class_3345(1460);
		this.field_14453 = Maps.<SocketAddress, class_3364.class_3365>newHashMap();
		this.field_14451 = new Date().getTime();
	}

	private void method_14751(byte[] bs, DatagramPacket datagramPacket) throws IOException {
		this.field_14449.send(new DatagramPacket(bs, bs.length, datagramPacket.getSocketAddress()));
	}

	private boolean method_14750(DatagramPacket datagramPacket) throws IOException {
		byte[] bs = datagramPacket.getData();
		int i = datagramPacket.getLength();
		SocketAddress socketAddress = datagramPacket.getSocketAddress();
		this.method_14722("Packet len " + i + " [" + socketAddress + "]");
		if (3 <= i && -2 == bs[0] && -3 == bs[1]) {
			this.method_14722("Packet '" + class_3347.method_14699(bs[2]) + "' [" + socketAddress + "]");
			switch (bs[2]) {
				case 0:
					if (!this.method_14753(datagramPacket)) {
						this.method_14722("Invalid challenge [" + socketAddress + "]");
						return false;
					} else if (15 == i) {
						this.method_14751(this.method_14747(datagramPacket), datagramPacket);
						this.method_14722("Rules [" + socketAddress + "]");
					} else {
						class_3345 lv = new class_3345(1460);
						lv.method_14692(0);
						lv.method_14694(this.method_14748(datagramPacket.getSocketAddress()));
						lv.method_14690(this.field_14445);
						lv.method_14690("SMP");
						lv.method_14690(this.field_14447);
						lv.method_14690(Integer.toString(this.method_14724()));
						lv.method_14690(Integer.toString(this.field_14456));
						lv.method_14691((short)this.field_14457);
						lv.method_14690(this.field_14444);
						this.method_14751(lv.method_14689(), datagramPacket);
						this.method_14722("Status [" + socketAddress + "]");
					}
				default:
					return true;
				case 9:
					this.method_14749(datagramPacket);
					this.method_14722("Challenge [" + socketAddress + "]");
					return true;
			}
		} else {
			this.method_14722("Invalid packet [" + socketAddress + "]");
			return false;
		}
	}

	private byte[] method_14747(DatagramPacket datagramPacket) throws IOException {
		long l = class_156.method_658();
		if (l < this.field_14450 + 5000L) {
			byte[] bs = this.field_14446.method_14689();
			byte[] cs = this.method_14748(datagramPacket.getSocketAddress());
			bs[1] = cs[0];
			bs[2] = cs[1];
			bs[3] = cs[2];
			bs[4] = cs[3];
			return bs;
		} else {
			this.field_14450 = l;
			this.field_14446.method_14693();
			this.field_14446.method_14692(0);
			this.field_14446.method_14694(this.method_14748(datagramPacket.getSocketAddress()));
			this.field_14446.method_14690("splitnum");
			this.field_14446.method_14692(128);
			this.field_14446.method_14692(0);
			this.field_14446.method_14690("hostname");
			this.field_14446.method_14690(this.field_14445);
			this.field_14446.method_14690("gametype");
			this.field_14446.method_14690("SMP");
			this.field_14446.method_14690("game_id");
			this.field_14446.method_14690("MINECRAFT");
			this.field_14446.method_14690("version");
			this.field_14446.method_14690(this.field_14425.method_3827());
			this.field_14446.method_14690("plugins");
			this.field_14446.method_14690(this.field_14425.method_12916());
			this.field_14446.method_14690("map");
			this.field_14446.method_14690(this.field_14447);
			this.field_14446.method_14690("numplayers");
			this.field_14446.method_14690("" + this.method_14724());
			this.field_14446.method_14690("maxplayers");
			this.field_14446.method_14690("" + this.field_14456);
			this.field_14446.method_14690("hostport");
			this.field_14446.method_14690("" + this.field_14457);
			this.field_14446.method_14690("hostip");
			this.field_14446.method_14690(this.field_14444);
			this.field_14446.method_14692(0);
			this.field_14446.method_14692(1);
			this.field_14446.method_14690("player_");
			this.field_14446.method_14692(0);
			String[] strings = this.field_14425.method_3858();

			for (String string : strings) {
				this.field_14446.method_14690(string);
			}

			this.field_14446.method_14692(0);
			return this.field_14446.method_14689();
		}
	}

	private byte[] method_14748(SocketAddress socketAddress) {
		return ((class_3364.class_3365)this.field_14453.get(socketAddress)).method_14758();
	}

	private Boolean method_14753(DatagramPacket datagramPacket) {
		SocketAddress socketAddress = datagramPacket.getSocketAddress();
		if (!this.field_14453.containsKey(socketAddress)) {
			return false;
		} else {
			byte[] bs = datagramPacket.getData();
			return ((class_3364.class_3365)this.field_14453.get(socketAddress)).method_14756() != class_3347.method_14698(bs, 7, datagramPacket.getLength())
				? false
				: true;
		}
	}

	private void method_14749(DatagramPacket datagramPacket) throws IOException {
		class_3364.class_3365 lv = new class_3364.class_3365(datagramPacket);
		this.field_14453.put(datagramPacket.getSocketAddress(), lv);
		this.method_14751(lv.method_14757(), datagramPacket);
	}

	private void method_14746() {
		if (this.field_14431) {
			long l = class_156.method_658();
			if (l >= this.field_14443 + 30000L) {
				this.field_14443 = l;
				Iterator<Entry<SocketAddress, class_3364.class_3365>> iterator = this.field_14453.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<SocketAddress, class_3364.class_3365> entry = (Entry<SocketAddress, class_3364.class_3365>)iterator.next();
					if (((class_3364.class_3365)entry.getValue()).method_14755(l)) {
						iterator.remove();
					}
				}
			}
		}
	}

	public void run() {
		this.method_14729("Query running on " + this.field_14454 + ":" + this.field_14442);
		this.field_14443 = class_156.method_658();
		this.field_14455 = new DatagramPacket(this.field_14452, this.field_14452.length);

		try {
			while (this.field_14431) {
				try {
					this.field_14449.receive(this.field_14455);
					this.method_14746();
					this.method_14750(this.field_14455);
				} catch (SocketTimeoutException var7) {
					this.method_14746();
				} catch (PortUnreachableException var8) {
				} catch (IOException var9) {
					this.method_14752(var9);
				}
			}
		} finally {
			this.method_14733();
		}
	}

	@Override
	public void method_14728() {
		if (!this.field_14431) {
			if (0 < this.field_14442 && 65535 >= this.field_14442) {
				if (this.method_14754()) {
					super.method_14728();
				}
			} else {
				this.method_14727("Invalid query port " + this.field_14442 + " found in server.properties (queries disabled)");
			}
		}
	}

	private void method_14752(Exception exception) {
		if (this.field_14431) {
			this.method_14727("Unexpected exception, buggy JRE? (" + exception + ")");
			if (!this.method_14754()) {
				this.method_14726("Failed to recover from buggy JRE, shutting down!");
				this.field_14431 = false;
			}
		}
	}

	private boolean method_14754() {
		try {
			this.field_14449 = new DatagramSocket(this.field_14442, InetAddress.getByName(this.field_14454));
			this.method_14723(this.field_14449);
			this.field_14449.setSoTimeout(500);
			return true;
		} catch (SocketException var2) {
			this.method_14727("Unable to initialise query system on " + this.field_14454 + ":" + this.field_14442 + " (Socket): " + var2.getMessage());
		} catch (UnknownHostException var3) {
			this.method_14727("Unable to initialise query system on " + this.field_14454 + ":" + this.field_14442 + " (Unknown Host): " + var3.getMessage());
		} catch (Exception var4) {
			this.method_14727("Unable to initialise query system on " + this.field_14454 + ":" + this.field_14442 + " (E): " + var4.getMessage());
		}

		return false;
	}

	class class_3365 {
		private final long field_14459 = new Date().getTime();
		private final int field_14458;
		private final byte[] field_14460;
		private final byte[] field_14461;
		private final String field_14462;

		public class_3365(DatagramPacket datagramPacket) {
			byte[] bs = datagramPacket.getData();
			this.field_14460 = new byte[4];
			this.field_14460[0] = bs[3];
			this.field_14460[1] = bs[4];
			this.field_14460[2] = bs[5];
			this.field_14460[3] = bs[6];
			this.field_14462 = new String(this.field_14460, StandardCharsets.UTF_8);
			this.field_14458 = new Random().nextInt(16777216);
			this.field_14461 = String.format("\t%s%d\u0000", this.field_14462, this.field_14458).getBytes(StandardCharsets.UTF_8);
		}

		public Boolean method_14755(long l) {
			return this.field_14459 < l;
		}

		public int method_14756() {
			return this.field_14458;
		}

		public byte[] method_14757() {
			return this.field_14461;
		}

		public byte[] method_14758() {
			return this.field_14460;
		}
	}
}
