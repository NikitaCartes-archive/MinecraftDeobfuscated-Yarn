package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AdvancementObtainedStatus {
	OBTAINED(0),
	UNOBTAINED(1);

	private final int spriteIndex;

	private AdvancementObtainedStatus(int spriteIndex) {
		this.spriteIndex = spriteIndex;
	}

	public int getSpriteIndex() {
		return this.spriteIndex;
	}
}
