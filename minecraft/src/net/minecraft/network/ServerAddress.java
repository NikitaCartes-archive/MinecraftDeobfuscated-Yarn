package net.minecraft.network;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerAddress {
	private final String address;
	private final int port;

	private ServerAddress(String address, int i) {
		this.address = address;
		this.port = i;
	}

	public String getAddress() {
		try {
			return IDN.toASCII(this.address);
		} catch (IllegalArgumentException var2) {
			return "";
		}
	}

	public int getPort() {
		return this.port;
	}

	public static ServerAddress parse(String address) {
		if (address == null) {
			return null;
		} else {
			String[] strings = address.split(":");
			if (address.startsWith("[")) {
				int i = address.indexOf("]");
				if (i > 0) {
					String string = address.substring(1, i);
					String string2 = address.substring(i + 1).trim();
					if (string2.startsWith(":") && !string2.isEmpty()) {
						string2 = string2.substring(1);
						strings = new String[]{string, string2};
					} else {
						strings = new String[]{string};
					}
				}
			}

			if (strings.length > 2) {
				strings = new String[]{address};
			}

			String string3 = strings[0];
			int j = strings.length > 1 ? portOrDefault(strings[1], 25565) : 25565;
			if (j == 25565) {
				String[] strings2 = resolveSrv(string3);
				string3 = strings2[0];
				j = portOrDefault(strings2[1], 25565);
			}

			return new ServerAddress(string3, j);
		}
	}

	private static String[] resolveSrv(String address) {
		try {
			String string = "com.sun.jndi.dns.DnsContextFactory";
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			DirContext dirContext = new InitialDirContext(hashtable);
			Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + address, new String[]{"SRV"});
			String[] strings = attributes.get("srv").get().toString().split(" ", 4);
			return new String[]{strings[3], strings[2]};
		} catch (Throwable var6) {
			return new String[]{address, Integer.toString(25565)};
		}
	}

	private static int portOrDefault(String port, int def) {
		try {
			return Integer.parseInt(port.trim());
		} catch (Exception var3) {
			return def;
		}
	}
}
