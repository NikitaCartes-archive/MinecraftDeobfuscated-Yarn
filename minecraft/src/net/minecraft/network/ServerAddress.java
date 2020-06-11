package net.minecraft.network;

import com.mojang.datafixers.util.Pair;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
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
				Pair<String, Integer> pair = resolveSrv(string3);
				string3 = pair.getFirst();
				j = pair.getSecond();
			}

			return new ServerAddress(string3, j);
		}
	}

	private static Pair<String, Integer> resolveSrv(String address) {
		try {
			String string = "com.sun.jndi.dns.DnsContextFactory";
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			DirContext dirContext = new InitialDirContext(hashtable);
			Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + address, new String[]{"SRV"});
			Attribute attribute = attributes.get("srv");
			if (attribute != null) {
				String[] strings = attribute.get().toString().split(" ", 4);
				return Pair.of(strings[3], portOrDefault(strings[2], 25565));
			}
		} catch (Throwable var7) {
		}

		return Pair.of(address, 25565);
	}

	private static int portOrDefault(String port, int def) {
		try {
			return Integer.parseInt(port.trim());
		} catch (Exception var3) {
			return def;
		}
	}
}
