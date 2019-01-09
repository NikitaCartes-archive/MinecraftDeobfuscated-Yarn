package net.minecraft;

import javax.annotation.Nullable;

public class class_2570 extends class_2484 {
	private static class_2700 field_11765;
	private static class_2700 field_11764;

	protected class_2570(class_2248.class_2251 arg) {
		super(class_2484.class_2486.field_11513, arg);
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		super.method_9567(arg, arg2, arg3, arg4, arg5);
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2631) {
			method_10898(arg, arg2, (class_2631)lv);
		}
	}

	public static void method_10898(class_1937 arg, class_2338 arg2, class_2631 arg3) {
		class_2248 lv = arg3.method_11010().method_11614();
		boolean bl = lv == class_2246.field_10177 || lv == class_2246.field_10101;
		if (bl && arg2.method_10264() >= 2 && arg.method_8407() != class_1267.field_5801 && !arg.field_9236) {
			class_2700 lv2 = method_10900();
			class_2700.class_2702 lv3 = lv2.method_11708(arg, arg2);
			if (lv3 != null) {
				for (int i = 0; i < lv2.method_11710(); i++) {
					for (int j = 0; j < lv2.method_11713(); j++) {
						arg.method_8652(lv3.method_11717(i, j, 0).method_11683(), class_2246.field_10124.method_9564(), 2);
					}
				}

				class_2338 lv4 = lv3.method_11717(1, 0, 0).method_11683();
				class_1528 lv5 = new class_1528(arg);
				class_2338 lv6 = lv3.method_11717(1, 2, 0).method_11683();
				lv5.method_5808(
					(double)lv6.method_10263() + 0.5,
					(double)lv6.method_10264() + 0.55,
					(double)lv6.method_10260() + 0.5,
					lv3.method_11719().method_10166() == class_2350.class_2351.field_11048 ? 0.0F : 90.0F,
					0.0F
				);
				lv5.field_6283 = lv3.method_11719().method_10166() == class_2350.class_2351.field_11048 ? 0.0F : 90.0F;
				lv5.method_6885();

				for (class_3222 lv7 : arg.method_8403(class_3222.class, lv5.method_5829().method_1014(50.0))) {
					class_174.field_1182.method_9124(lv7, lv5);
				}

				arg.method_8649(lv5);

				for (int k = 0; k < 120; k++) {
					arg.method_8406(
						class_2398.field_11230,
						(double)lv4.method_10263() + arg.field_9229.nextDouble(),
						(double)(lv4.method_10264() - 2) + arg.field_9229.nextDouble() * 3.9,
						(double)lv4.method_10260() + arg.field_9229.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int k = 0; k < lv2.method_11710(); k++) {
					for (int l = 0; l < lv2.method_11713(); l++) {
						arg.method_8408(lv3.method_11717(k, l, 0).method_11683(), class_2246.field_10124);
					}
				}
			}
		}
	}

	public static boolean method_10899(class_1937 arg, class_2338 arg2, class_1799 arg3) {
		return arg3.method_7909() == class_1802.field_8791 && arg2.method_10264() >= 2 && arg.method_8407() != class_1267.field_5801 && !arg.field_9236
			? method_10897().method_11708(arg, arg2) != null
			: false;
	}

	protected static class_2700 method_10900() {
		if (field_11765 == null) {
			field_11765 = class_2697.method_11701()
				.method_11702("^^^", "###", "~#~")
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10114)))
				.method_11700('^', class_2694.method_11678(class_2715.method_11758(class_2246.field_10177).or(class_2715.method_11758(class_2246.field_10101))))
				.method_11700('~', class_2694.method_11678(class_2710.method_11746(class_3614.field_15959)))
				.method_11704();
		}

		return field_11765;
	}

	protected static class_2700 method_10897() {
		if (field_11764 == null) {
			field_11764 = class_2697.method_11701()
				.method_11702("   ", "###", "~#~")
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10114)))
				.method_11700('~', class_2694.method_11678(class_2710.method_11746(class_3614.field_15959)))
				.method_11704();
		}

		return field_11764;
	}
}
