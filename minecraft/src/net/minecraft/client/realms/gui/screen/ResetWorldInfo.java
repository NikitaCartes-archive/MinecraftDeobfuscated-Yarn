package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResetWorldInfo {
	private final String seed;
	private final RealmsWorldGeneratorType levelType;
	private final boolean generateStructures;

	public ResetWorldInfo(String seed, RealmsWorldGeneratorType realmsWorldGeneratorType, boolean generateStructures) {
		this.seed = seed;
		this.levelType = realmsWorldGeneratorType;
		this.generateStructures = generateStructures;
	}

	public String method_32508() {
		return this.seed;
	}

	public RealmsWorldGeneratorType method_32509() {
		return this.levelType;
	}

	public boolean method_32510() {
		return this.generateStructures;
	}
}
