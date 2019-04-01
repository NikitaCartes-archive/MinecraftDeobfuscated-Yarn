package net.minecraft;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_639 {
	private final String field_3734;
	private final int field_3735;

	private class_639(String string, int i) {
		this.field_3734 = string;
		this.field_3735 = i;
	}

	public String method_2952() {
		try {
			return IDN.toASCII(this.field_3734);
		} catch (IllegalArgumentException var2) {
			return "";
		}
	}

	public int method_2954() {
		return this.field_3735;
	}

	public static class_639 method_2950(String string) {
		if (string == null) {
			return null;
		} else {
			String[] strings = string.split(":");
			if (string.startsWith("[")) {
				int i = string.indexOf("]");
				if (i > 0) {
					String string2 = string.substring(1, i);
					String string3 = string.substring(i + 1).trim();
					if (string3.startsWith(":") && !string3.isEmpty()) {
						string3 = string3.substring(1);
						strings = new String[]{string2, string3};
					} else {
						strings = new String[]{string2};
					}
				}
			}

			if (strings.length > 2) {
				strings = new String[]{string};
			}

			String string4 = strings[0];
			int j = strings.length > 1 ? method_2951(strings[1], 25565) : 25565;
			if (j == 25565) {
				String[] strings2 = method_2953(string4);
				string4 = strings2[0];
				j = method_2951(strings2[1], 25565);
			}

			return new class_639(string4, j);
		}
	}

	private static String[] method_2953(String string) {
		try {
			String string2 = "com.sun.jndi.dns.DnsContextFactory";
			Class.forName("com.sun.jndi.dns.DnsContextFactory");
			Hashtable<String, String> hashtable = new Hashtable();
			hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hashtable.put("java.naming.provider.url", "dns:");
			hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
			DirContext dirContext = new InitialDirContext(hashtable);
			Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + string, new String[]{"SRV"});
			String[] strings = attributes.get("srv").get().toString().split(" ", 4);
			return new String[]{strings[3], strings[2]};
		} catch (Throwable var6) {
			return new String[]{string, Integer.toString(25565)};
		}
	}

	private static int method_2951(String string, int i) {
		try {
			return Integer.parseInt(string.trim());
		} catch (Exception var3) {
			return i;
		}
	}
}
