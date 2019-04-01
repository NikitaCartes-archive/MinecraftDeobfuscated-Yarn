package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1163 {
	private static final class_1163.class_1164 field_5665 = class_1959::method_8711;
	private static final class_1163.class_1164 field_5664 = class_1959::method_8698;
	private static final class_1163.class_1164 field_5666 = (arg, arg2) -> arg.method_8687();
	private static final class_1163.class_1164 field_5667 = (arg, arg2) -> arg.method_8713();

	private static int method_4965(class_1920 arg, class_2338 arg2, class_1163.class_1164 arg3) {
		int i = 0;
		int j = 0;
		int k = 0;
		int l = class_310.method_1551().field_1690.field_1878;
		int m = (l * 2 + 1) * (l * 2 + 1);

		for (class_2338 lv : class_2338.method_10094(
			arg2.method_10263() - l, arg2.method_10264(), arg2.method_10260() - l, arg2.method_10263() + l, arg2.method_10264(), arg2.method_10260() + l
		)) {
			int n = arg3.getColor(arg.method_8310(lv), lv);
			i += (n & 0xFF0000) >> 16;
			j += (n & 0xFF00) >> 8;
			k += n & 0xFF;
		}

		return (i / m & 0xFF) << 16 | (j / m & 0xFF) << 8 | k / m & 0xFF;
	}

	public static int method_4962(class_1920 arg, class_2338 arg2) {
		return method_4965(arg, arg2, field_5665);
	}

	public static int method_4966(class_1920 arg, class_2338 arg2) {
		return method_4965(arg, arg2, field_5664);
	}

	public static int method_4961(class_1920 arg, class_2338 arg2) {
		return method_4965(arg, arg2, field_5666);
	}

	@Environment(EnvType.CLIENT)
	interface class_1164 {
		int getColor(class_1959 arg, class_2338 arg2);
	}
}
