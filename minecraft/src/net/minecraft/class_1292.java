package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1292 {
	@Environment(EnvType.CLIENT)
	public static String method_5577(class_1293 arg, float f) {
		if (arg.method_5593()) {
			return "**:**";
		} else {
			int i = class_3532.method_15375((float)arg.method_5584() * f);
			return class_3544.method_15439(i);
		}
	}

	public static boolean method_5576(class_1309 arg) {
		return arg.method_6059(class_1294.field_5917) || arg.method_6059(class_1294.field_5927);
	}

	public static int method_5575(class_1309 arg) {
		int i = 0;
		int j = 0;
		if (arg.method_6059(class_1294.field_5917)) {
			i = arg.method_6112(class_1294.field_5917).method_5578();
		}

		if (arg.method_6059(class_1294.field_5927)) {
			j = arg.method_6112(class_1294.field_5927).method_5578();
		}

		return Math.max(i, j);
	}

	public static boolean method_5574(class_1309 arg) {
		return arg.method_6059(class_1294.field_5923) || arg.method_6059(class_1294.field_5927);
	}
}
