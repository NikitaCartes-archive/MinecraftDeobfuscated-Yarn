package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1926 {
	private static int[] field_9183 = new int[65536];

	public static void method_8340(int[] is) {
		field_9183 = is;
	}

	public static int method_8344(double d, double e) {
		e *= d;
		int i = (int)((1.0 - d) * 255.0);
		int j = (int)((1.0 - e) * 255.0);
		return field_9183[j << 8 | i];
	}

	public static int method_8342() {
		return 6396257;
	}

	public static int method_8343() {
		return 8431445;
	}

	public static int method_8341() {
		return 4764952;
	}
}
