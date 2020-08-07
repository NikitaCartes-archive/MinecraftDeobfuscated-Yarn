package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class LanServerInfo {
	private final String motd;
	private final String addressPort;
	private long lastTimeMillis;

	public LanServerInfo(String motd, String addressPort) {
		this.motd = motd;
		this.addressPort = addressPort;
		this.lastTimeMillis = Util.getMeasuringTimeMs();
	}

	public String getMotd() {
		return this.motd;
	}

	public String getAddressPort() {
		return this.addressPort;
	}

	public void updateLastTime() {
		this.lastTimeMillis = Util.getMeasuringTimeMs();
	}
}
