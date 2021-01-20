package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelUtil {
	public static float interpolateAngle(float angle1, float angle2, float progress) {
		float f = angle2 - angle1;

		while (f < (float) -Math.PI) {
			f += (float) (Math.PI * 2);
		}

		while (f >= (float) Math.PI) {
			f -= (float) (Math.PI * 2);
		}

		return angle1 + progress * f;
	}
}
