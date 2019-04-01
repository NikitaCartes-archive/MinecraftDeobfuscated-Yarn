package net.minecraft;

import java.util.Random;

public class class_2301 extends class_2230 {
	private final class_2248 field_10833;
	protected static final class_265 field_10834 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

	protected class_2301(class_2248 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_10833 = arg;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		this.method_9430(arg, arg2, arg3);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!method_9431(arg, arg2, arg3)) {
			arg2.method_8652(arg3, this.field_10833.method_9564().method_11657(field_9940, Boolean.valueOf(false)), 2);
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)) {
			return class_2246.field_10124.method_9564();
		} else {
			this.method_9430(arg, arg4, arg5);
			if ((Boolean)arg.method_11654(field_9940)) {
				arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			}

			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10834;
	}
}
