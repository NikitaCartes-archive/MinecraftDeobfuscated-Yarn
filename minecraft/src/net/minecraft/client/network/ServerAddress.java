package net.minecraft.client.network;

import com.google.common.net.HostAndPort;
import com.mojang.logging.LogUtils;
import java.net.IDN;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class ServerAddress {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final HostAndPort hostAndPort;
	private static final ServerAddress INVALID = new ServerAddress(HostAndPort.fromParts("server.invalid", 25565));

	public ServerAddress(String host, int port) {
		this(HostAndPort.fromParts(host, port));
	}

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
			try {
				HostAndPort hostAndPort = HostAndPort.fromString(address).withDefaultPort(25565);
				return hostAndPort.getHost().isEmpty() ? INVALID : new ServerAddress(hostAndPort);
			} catch (IllegalArgumentException var2) {
				LOGGER.info("Failed to parse URL {}", address, var2);
				return INVALID;
			}
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

	static int portOrDefault(String port) {
		try {
			return Integer.parseInt(port.trim());
		} catch (Exception var2) {
			return 25565;
		}
	}

	public String toString() {
		return this.hostAndPort.toString();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return o instanceof ServerAddress ? this.hostAndPort.equals(((ServerAddress)o).hostAndPort) : false;
		}
	}

	public int hashCode() {
		return this.hostAndPort.hashCode();
	}
}
