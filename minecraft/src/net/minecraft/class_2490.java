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
		} else if (arg2.field_5984 < 0.0) {
			arg2.field_5984 = -arg2.field_5984;
			if (!(arg2 instanceof class_1309)) {
				arg2.field_5984 *= 0.8;
			}
		}
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		if (Math.abs(arg3.field_5984) < 0.1 && !arg3.method_5715()) {
			double d = 0.4 + Math.abs(arg3.field_5984) * 0.2;
			arg3.field_5967 *= d;
			arg3.field_6006 *= d;
		}

		super.method_9591(arg, arg2, arg3);
	}
}
