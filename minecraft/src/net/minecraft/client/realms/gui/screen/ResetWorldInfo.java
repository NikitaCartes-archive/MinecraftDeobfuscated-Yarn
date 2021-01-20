package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResetWorldInfo {
	private final String seed;
	private final RealmsWorldGeneratorType levelType;
	private final boolean generateStructures;

	public ResetWorldInfo(String seed, RealmsWorldGeneratorType levelType, boolean generateStructures) {
		this.seed = seed;
		this.levelType = levelType;
		this.generateStructures = generateStructures;
	}

	public String getSeed() {
		return this.seed;
	}

	public RealmsWorldGeneratorType getLevelType() {
		return this.levelType;
	}

	public boolean shouldGenerateStructures() {
		return this.generateStructures;
	}
}
