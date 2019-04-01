package net.minecraft;

public class class_2490 extends class_2373 {
	public class_2490(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public void method_9554(class_1937 arg, class_2338 arg2, class_1297 arg3, float f) {
		if (arg3.method_5715()) {
			super.method_9554(arg, arg2, arg3, f);
		} else {
			arg3.method_5747(f, 0.0F);
		}
	}

	@Override
	public void method_9502(class_1922 arg, class_1297 arg2) {
		if (arg2.method_5715()) {
			super.method_9502(arg, arg2);
		} else {
			class_243 lv = arg2.method_18798();
			if (lv.field_1351 < 0.0) {
				double d = arg2 instanceof class_1309 ? 1.0 : 0.8;
				arg2.method_18800(lv.field_1352, -lv.field_1351 * d, lv.field_1350);
			}
		}
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		double d = Math.abs(arg3.method_18798().field_1351);
		if (d < 0.1 && !arg3.method_5715()) {
			double e = 0.4 + d * 0.2;
			arg3.method_18799(arg3.method_18798().method_18805(e, 1.0, e));
		}

		super.method_9591(arg, arg2, arg3);
	}
}
