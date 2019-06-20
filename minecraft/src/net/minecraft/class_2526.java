package net.minecraft;

import java.util.Random;

public class class_2526 extends class_2261 implements class_2256 {
	protected static final class_265 field_11617 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected class_2526(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11617;
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		class_2320 lv = (class_2320)(this == class_2246.field_10112 ? class_2246.field_10313 : class_2246.field_10214);
		if (lv.method_9564().method_11591(arg, arg2) && arg.method_8623(arg2.method_10084())) {
			lv.method_10021(arg, arg2, 2);
		}
	}

	@Override
	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10655;
	}
}
