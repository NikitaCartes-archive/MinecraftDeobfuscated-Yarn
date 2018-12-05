package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LanServerEntry {
	private final String motd;
	private final String addressPort;
	private long lastTimeMillis;

	public LanServerEntry(String string, String string2) {
		this.motd = string;
		this.addressPort = string2;
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMili();
	}

	public String getMotd() {
		return this.motd;
	}

	public String getAddressPort() {
		return this.addressPort;
	}

	public void updateLastTime() {
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMili();
	}
}
