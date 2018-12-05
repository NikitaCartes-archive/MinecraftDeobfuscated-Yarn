package net.minecraft.entity.ai.pathing;

public enum PathNodeType {
	BLOCKED(-1.0F),
	AIR(0.0F),
	NORMAL(0.0F),
	TRAPDOOR(0.0F),
	FENCE(-1.0F),
	LAVA(-1.0F),
	WATER(8.0F),
	field_4(8.0F),
	RAIL(0.0F),
	FIRE_NEAR(8.0F),
	FIRE(16.0F),
	CACTUS_NEAR(8.0F),
	CACTUS(-1.0F),
	field_5(8.0F),
	field_17(-1.0F),
	DOOR_OPEN(0.0F),
	DOOR_WOOD(-1.0F),
	DOOR_METAL(-1.0F),
	field_16(4.0F),
	field_6(-1.0F);

	private final float weight;

	private PathNodeType(float f) {
		this.weight = f;
	}

	public float getWeight() {
		return this.weight;
	}
}
