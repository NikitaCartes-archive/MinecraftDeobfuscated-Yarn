package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2476 extends class_2261 implements class_2256, class_2402 {
	protected static final class_265 field_11485 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	protected class_2476(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11485;
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return class_2248.method_9501(arg.method_11628(arg2, arg3), class_2350.field_11036) && arg.method_11614() != class_2246.field_10092;
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return lv.method_15767(class_3486.field_15517) && lv.method_15761() == 8 ? super.method_9605(arg) : null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_2680 lv = super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		if (!lv.method_11588()) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return lv;
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
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15910.method_15729(false);
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		class_2680 lv = class_2246.field_10238.method_9564();
		class_2680 lv2 = lv.method_11657(class_2525.field_11616, class_2756.field_12609);
		class_2338 lv3 = arg2.method_10084();
		if (arg.method_8320(lv3).method_11614() == class_2246.field_10382) {
			arg.method_8652(arg2, lv, 2);
			arg.method_8652(lv3, lv2, 2);
		}
	}

	@Override
	public boolean method_10310(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		return false;
	}

	@Override
	public boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		return false;
	}
}
