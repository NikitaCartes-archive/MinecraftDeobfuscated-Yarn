package net.minecraft.entity.ai.pathing;

public enum PathNodeType {
	BLOCKED(-1.0F),
	OPEN(0.0F),
	WALKABLE(0.0F),
	TRAPDOOR(0.0F),
	FENCE(-1.0F),
	LAVA(-1.0F),
	WATER(8.0F),
	WATER_BORDER(8.0F),
	RAIL(0.0F),
	DANGER_FIRE(8.0F),
	DAMAGE_FIRE(16.0F),
	DANGER_CACTUS(8.0F),
	DAMAGE_CACTUS(-1.0F),
	DANGER_OTHER(8.0F),
	DAMAGE_OTHER(-1.0F),
	DOOR_OPEN(0.0F),
	DOOR_WOOD_CLOSED(-1.0F),
	DOOR_IRON_CLOSED(-1.0F),
	BREACH(4.0F),
	LEAVES(-1.0F);

	private final float weight;

	private PathNodeType(float f) {
		this.weight = f;
	}

	public float getWeight() {
		return this.weight;
	}
}
