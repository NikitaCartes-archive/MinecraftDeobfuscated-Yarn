package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsWorldResetDto extends ValueObject {
	private final String seed;
	private final long worldTemplateId;
	private final int levelType;
	private final boolean generateStructures;

	public RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures) {
		this.seed = seed;
		this.worldTemplateId = worldTemplateId;
		this.levelType = levelType;
		this.generateStructures = generateStructures;
	}
}
