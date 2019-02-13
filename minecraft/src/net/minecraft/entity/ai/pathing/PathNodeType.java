package net.minecraft.entity.ai.pathing;

public enum PathNodeType {
	field_22(-1.0F),
	field_7(0.0F),
	field_12(0.0F),
	field_19(0.0F),
	field_10(-1.0F),
	field_14(-1.0F),
	field_18(8.0F),
	field_4(8.0F),
	field_21(0.0F),
	field_9(8.0F),
	field_3(16.0F),
	field_20(8.0F),
	field_11(-1.0F),
	field_5(8.0F),
	field_17(-1.0F),
	field_15(0.0F),
	field_23(-1.0F),
	field_8(-1.0F),
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
