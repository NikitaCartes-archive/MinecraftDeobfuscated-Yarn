package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;

public class DamageUtil {
	public static float getDamageLeft(float f, float g, float h) {
		float i = 2.0F + h / 4.0F;
		float j = MathHelper.clamp(g - f / i, g * 0.2F, 20.0F);
		return f * (1.0F - j / 25.0F);
	}

	public static float getInflictedDamage(float f, float g) {
		float h = MathHelper.clamp(g, 0.0F, 20.0F);
		return f * (1.0F - h / 25.0F);
	}
}
