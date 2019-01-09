package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_639;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress {
	private final String host;
	private final int port;

	protected RealmsServerAddress(String string, int i) {
		this.host = string;
		this.port = i;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public static RealmsServerAddress parseString(String string) {
		class_639 lv = class_639.method_2950(string);
		return new RealmsServerAddress(lv.method_2952(), lv.method_2954());
	}
}
