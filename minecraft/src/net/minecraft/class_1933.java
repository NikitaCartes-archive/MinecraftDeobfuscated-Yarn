package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1933 {
	private static int[] field_9214 = new int[65536];

	public static void method_8376(int[] is) {
		field_9214 = is;
	}

	public static int method_8377(double d, double e) {
		e *= d;
		int i = (int)((1.0 - d) * 255.0);
		int j = (int)((1.0 - e) * 255.0);
		int k = j << 8 | i;
		return k > field_9214.length ? -65281 : field_9214[k];
	}
}
