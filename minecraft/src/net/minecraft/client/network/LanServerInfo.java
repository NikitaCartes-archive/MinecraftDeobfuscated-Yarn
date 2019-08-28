package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LanServerInfo {
	private final String motd;
	private final String addressPort;
	private long lastTimeMillis;

	public LanServerInfo(String string, String string2) {
		this.motd = string;
		this.addressPort = string2;
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
	}

	public String getMotd() {
		return this.motd;
	}

	public String getAddressPort() {
		return this.addressPort;
	}

	public void updateLastTime() {
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
	}
}
