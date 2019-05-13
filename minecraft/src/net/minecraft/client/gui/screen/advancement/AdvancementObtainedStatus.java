package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AdvancementObtainedStatus {
	field_2701(0),
	field_2699(1);

	private final int spriteIndex;

	private AdvancementObtainedStatus(int j) {
		this.spriteIndex = j;
	}

	public int getSpriteIndex() {
		return this.spriteIndex;
	}
}
