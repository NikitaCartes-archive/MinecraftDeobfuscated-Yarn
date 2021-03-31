package net.minecraft.world.border;

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
