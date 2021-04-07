package net.minecraft.client.network;

import com.google.common.net.HostAndPort;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerAddress {
	private static final Logger LOGGER = LogManager.getLogger();
	private final HostAndPort hostAndPort;
	private static final ServerAddress INVALID = new ServerAddress(HostAndPort.fromParts("server.invalid", 25565));

	private ServerAddress(HostAndPort hostAndPort) {
		this.hostAndPort = hostAndPort;
	}

	public String getAddress() {
		try {
			return IDN.toASCII(this.hostAndPort.getHost());
		} catch (IllegalArgumentException var2) {
			return "";
		}
	}

	public int getPort() {
		return this.hostAndPort.getPort();
	}

	public static ServerAddress parse(String address) {
		if (address == null) {
			return INVALID;
		} else {
			HostAndPort hostAndPort;
			try {
				hostAndPort = HostAndPort.fromString(address).withDefaultPort(25565);
				if (hostAndPort.getHost().isEmpty()) {
					return INVALID;
				}
			} catch (IllegalArgumentException var3) {
				LOGGER.info("Failed to parse URL {}", address, var3);
				return INVALID;
			}

			return new ServerAddress(resolveServer(hostAndPort));
		}
	}

	public static boolean isValid(String address) {
		try {
			HostAndPort hostAndPort = HostAndPort.fromString(address);
			String string = hostAndPort.getHost();
			if (!string.isEmpty()) {
				IDN.toASCII(string);
				return true;
			}
		} catch (IllegalArgumentException var3) {
		}

		return false;
	}

	private static HostAndPort resolveServer(HostAndPort hostAndPort) {
		if (hostAndPort.getPort() != 25565) {
			return hostAndPort;
		} else {
			try {
				String string = "com.sun.jndi.dns.DnsContextFactory";
				Class.forName("com.sun.jndi.dns.DnsContextFactory");
				Hashtable<String, String> hashtable = new Hashtable();
				hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
				hashtable.put("java.naming.provider.url", "dns:");
				hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
				DirContext dirContext = new InitialDirContext(hashtable);
				Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + hostAndPort.getHost(), new String[]{"SRV"});
				Attribute attribute = attributes.get("srv");
				if (attribute != null) {
					String[] strings = attribute.get().toString().split(" ", 4);
					return HostAndPort.fromParts(strings[3], portOrDefault(strings[2], 25565));
				}
			} catch (Throwable var7) {
			}

			return hostAndPort;
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
