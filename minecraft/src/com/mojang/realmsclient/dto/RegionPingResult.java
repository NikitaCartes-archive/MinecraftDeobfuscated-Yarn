package com.mojang.realmsclient.dto;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(EnvType.CLIENT)
public class RegionPingResult extends class_4352 {
	private final String regionName;
	private final int ping;

	public RegionPingResult(String string, int i) {
		this.regionName = string;
		this.ping = i;
	}

	public int ping() {
		return this.ping;
	}

	@Override
	public String toString() {
		return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
	}
}
