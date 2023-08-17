package net.minecraft.client.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AbuseReportType {
	CHAT("chat"),
	SKIN("skin"),
	USERNAME("username");

	private final String name;

	private AbuseReportType(String name) {
		this.name = name.toUpperCase(Locale.ROOT);
	}

	public String getName() {
		return this.name;
	}
}
