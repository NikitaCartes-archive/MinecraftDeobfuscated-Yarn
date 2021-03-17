package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerAddress;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress {
	private final String host;
	private final int port;

	protected RealmsServerAddress(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public static RealmsServerAddress parseString(String string) {
		ServerAddress serverAddress = ServerAddress.parse(string);
		return new RealmsServerAddress(serverAddress.getAddress(), serverAddress.getPort());
	}
}
