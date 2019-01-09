package net.minecraft;

import javax.annotation.Nullable;

public class class_2399 extends class_2248 implements class_3737 {
	public static final class_2753 field_11253 = class_2383.field_11177;
	public static final class_2746 field_11257 = class_2741.field_12508;
	protected static final class_265 field_11255 = class_2248.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final class_265 field_11252 = class_2248.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_11254 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final class_265 field_11256 = class_2248.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

	protected class_2399(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11253, class_2350.field_11043).method_11657(field_11257, Boolean.valueOf(false)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		switch ((class_2350)arg.method_11654(field_11253)) {
			case field_11043:
				return field_11256;
			case field_11035:
				return field_11254;
			case field_11039:
				return field_11252;
			case field_11034:
			default:
				return field_11255;
		}
	}

	private boolean method_10305(class_1922 arg, class_2338 arg2, class_2350 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		boolean bl = method_9581(lv.method_11614());
		return !bl && class_2248.method_9501(lv.method_11628(arg, arg2), arg3) && !lv.method_11634();
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_11253);
		return this.method_10305(arg2, arg3.method_10093(lv.method_10153()), lv);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2.method_10153() == arg.method_11654(field_11253) && !arg.method_11591(arg4, arg5)) {
			return class_2246.field_10124.method_9564();
		} else {
			if ((Boolean)arg.method_11654(field_11257)) {
				arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
			}

			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		if (!arg.method_7717()) {
			class_2680 lv = arg.method_8045().method_8320(arg.method_8037().method_10093(arg.method_8038().method_10153()));
			if (lv.method_11614() == this && lv.method_11654(field_11253) == arg.method_8038()) {
				return null;
			}
		}

		class_2680 lv = this.method_9564();
		class_1941 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_3610 lv4 = arg.method_8045().method_8316(arg.method_8037());

		for (class_2350 lv5 : arg.method_7718()) {
			if (lv5.method_10166().method_10179()) {
				lv = lv.method_11657(field_11253, lv5.method_10153());
				if (lv.method_11591(lv2, lv3)) {
					return lv.method_11657(field_11257, Boolean.valueOf(lv4.method_15772() == class_3612.field_15910));
				}
			}
		}

		return null;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11253, arg2.method_10503(arg.method_11654(field_11253)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11253)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11253, field_11257);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11257) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}
}
