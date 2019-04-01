package net.minecraft;

import java.util.EnumSet;

public abstract class class_4017 extends class_1352 {
	public class_4017() {
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18407));
	}

	protected float method_18251(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
