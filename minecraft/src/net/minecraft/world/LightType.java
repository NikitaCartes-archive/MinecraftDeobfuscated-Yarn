package net.minecraft.world;

public enum LightType {
	SKY_LIGHT(15),
	BLOCK_LIGHT(0);

	public final int field_9283;

	private LightType(int j) {
		this.field_9283 = j;
	}
}
