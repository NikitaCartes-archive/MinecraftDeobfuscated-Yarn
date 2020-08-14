package net.minecraft.world.border;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum WorldBorderStage {
	GROWING(4259712),
	SHRINKING(16724016),
	STATIONARY(2138367);

	private final int color;

	private WorldBorderStage(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}
}
