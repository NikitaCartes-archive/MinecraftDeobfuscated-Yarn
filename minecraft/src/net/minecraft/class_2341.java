package net.minecraft;

import javax.annotation.Nullable;

public class class_2341 extends class_2383 {
	public static final class_2754<class_2738> field_11007 = class_2741.field_12555;

	protected class_2341(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = method_10119(arg).method_10153();
		class_2338 lv2 = arg3.method_10093(lv);
		class_2680 lv3 = arg2.method_8320(lv2);
		class_2248 lv4 = lv3.method_11614();
		if (method_9553(lv4)) {
			return false;
		} else {
			boolean bl = class_2248.method_9501(lv3.method_11628(arg2, lv2), lv.method_10153());
			return lv == class_2350.field_11036 ? lv4 == class_2246.field_10312 || bl : !method_9581(lv4) && bl;
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		for (class_2350 lv : arg.method_7718()) {
			class_2680 lv2;
			if (lv.method_10166() == class_2350.class_2351.field_11052) {
				lv2 = this.method_9564()
					.method_11657(field_11007, lv == class_2350.field_11036 ? class_2738.field_12473 : class_2738.field_12475)
					.method_11657(field_11177, arg.method_8042());
			} else {
				lv2 = this.method_9564().method_11657(field_11007, class_2738.field_12471).method_11657(field_11177, lv.method_10153());
			}

			if (lv2.method_11591(arg.method_8045(), arg.method_8037())) {
				return lv2;
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return method_10119(arg).method_10153() == arg2 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	protected static class_2350 method_10119(class_2680 arg) {
		switch ((class_2738)arg.method_11654(field_11007)) {
			case field_12473:
				return class_2350.field_11033;
			case field_12475:
				return class_2350.field_11036;
			default:
				return arg.method_11654(field_11177);
		}
	}
}
