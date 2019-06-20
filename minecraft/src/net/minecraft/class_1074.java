package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1074 {
	private static class_1078 field_5319;

	static void method_4661(class_1078 arg) {
		field_5319 = arg;
	}

	public static String method_4662(String string, Object... objects) {
		return field_5319.method_4677(string, objects);
	}

	public static boolean method_4663(String string) {
		return field_5319.method_4678(string);
	}
}
