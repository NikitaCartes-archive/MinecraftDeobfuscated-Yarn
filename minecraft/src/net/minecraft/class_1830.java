package net.minecraft;

public class class_1830 extends class_1792 {
	public class_1830(class_1792.class_1793 arg) {
		super(arg);
	}

	public static void method_8021(class_1799 arg, class_1291 arg2, int i) {
		class_2487 lv = arg.method_7948();
		class_2499 lv2 = lv.method_10554("Effects", 9);
		class_2487 lv3 = new class_2487();
		lv3.method_10567("EffectId", (byte)class_1291.method_5554(arg2));
		lv3.method_10569("EffectDuration", i);
		lv2.add(lv3);
		lv.method_10566("Effects", lv2);
	}

	@Override
	public class_1799 method_7861(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		super.method_7861(arg, arg2, arg3);
		class_2487 lv = arg.method_7969();
		if (lv != null && lv.method_10573("Effects", 9)) {
			class_2499 lv2 = lv.method_10554("Effects", 10);

			for (int i = 0; i < lv2.size(); i++) {
				int j = 160;
				class_2487 lv3 = lv2.method_10602(i);
				if (lv3.method_10573("EffectDuration", 3)) {
					j = lv3.method_10550("EffectDuration");
				}

				class_1291 lv4 = class_1291.method_5569(lv3.method_10571("EffectId"));
				if (lv4 != null) {
					arg3.method_6092(new class_1293(lv4, j));
				}
			}
		}

		return new class_1799(class_1802.field_8428);
	}
}
