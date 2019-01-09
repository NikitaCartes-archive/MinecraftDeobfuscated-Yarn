package net.minecraft;

public class class_2553 extends class_2261 {
	protected static final class_265 field_11728 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

	protected class_2553(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		super.method_9548(arg, arg2, arg3, arg4);
		if (arg4 instanceof class_1690) {
			arg2.method_8651(new class_2338(arg3), true);
		}
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11728;
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_3610 lv = arg2.method_8316(arg3);
		return lv.method_15772() == class_3612.field_15910 || arg.method_11620() == class_3614.field_15958;
	}
}
