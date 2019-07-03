package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(EnvType.CLIENT)
public class RealmsWorldResetDto extends class_4352 {
	private final String seed;
	private final long worldTemplateId;
	private final int levelType;
	private final boolean generateStructures;

	public RealmsWorldResetDto(String string, long l, int i, boolean bl) {
		this.seed = string;
		this.worldTemplateId = l;
		this.levelType = i;
		this.generateStructures = bl;
	}
}
