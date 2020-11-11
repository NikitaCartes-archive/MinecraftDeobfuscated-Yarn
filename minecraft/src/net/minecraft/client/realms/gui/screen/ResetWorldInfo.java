package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5672;

@Environment(EnvType.CLIENT)
public class ResetWorldInfo {
	private final String seed;
	private final class_5672 levelType;
	private final boolean generateStructures;

	public ResetWorldInfo(String seed, class_5672 arg, boolean generateStructures) {
		this.seed = seed;
		this.levelType = arg;
		this.generateStructures = generateStructures;
	}

	public String method_32508() {
		return this.seed;
	}

	public class_5672 method_32509() {
		return this.levelType;
	}

	public boolean method_32510() {
		return this.generateStructures;
	}
}
