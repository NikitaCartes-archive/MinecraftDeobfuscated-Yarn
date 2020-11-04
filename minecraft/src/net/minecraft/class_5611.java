package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5611 {
	private final float field_27731;
	private final float field_27732;

	public class_5611(float f, float g) {
		this.field_27731 = f;
		this.field_27732 = g;
	}

	public float method_32118() {
		return this.field_27731;
	}

	public float method_32119() {
		return this.field_27732;
	}

	public String toString() {
		return "(" + this.field_27731 + "," + this.field_27732 + ")";
	}
}
