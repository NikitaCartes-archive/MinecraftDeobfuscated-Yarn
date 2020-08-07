package net.minecraft.world.border;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum WorldBorderStage {
	field_12754(4259712),
	field_12756(16724016),
	field_12753(2138367);

	private final int color;

	private WorldBorderStage(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}
}
