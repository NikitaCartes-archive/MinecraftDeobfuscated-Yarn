package net.minecraft;

public class class_1280 {
	public static float method_5496(float f, float g, float h) {
		float i = 2.0F + h / 4.0F;
		float j = class_3532.method_15363(g - f / i, g * 0.2F, 20.0F);
		return f * (1.0F - j / 25.0F);
	}

	public static float method_5497(float f, float g) {
		float h = class_3532.method_15363(g, 0.0F, 20.0F);
		return f * (1.0F - h / 25.0F);
	}
}
