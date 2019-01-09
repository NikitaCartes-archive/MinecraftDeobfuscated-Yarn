package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2391 extends class_2248 implements class_2402 {
	private final class_2393 field_11190;

	protected class_2391(class_2393 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11190 = arg;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15910.method_15729(false);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			return class_2246.field_10124.method_9564();
		} else {
			if (arg2 == class_2350.field_11036) {
				class_2248 lv = arg3.method_11614();
				if (lv != this && lv != this.field_11190) {
					return this.field_11190.method_10292(arg4);
				}
			}

			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		class_2248 lv3 = lv2.method_11614();
		return lv3 != class_2246.field_10092 && (lv3 == this || class_2248.method_9501(lv2.method_11628(arg2, lv), class_2350.field_11036));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(class_2246.field_9993);
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
