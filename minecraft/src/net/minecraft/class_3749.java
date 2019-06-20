package net.minecraft;

import javax.annotation.Nullable;

public class class_3749 extends class_2248 {
	public static final class_2746 field_16545 = class_2741.field_16561;
	protected static final class_265 field_16546 = class_259.method_1084(
		class_2248.method_9541(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), class_2248.method_9541(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
	);
	protected static final class_265 field_16544 = class_259.method_1084(
		class_2248.method_9541(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), class_2248.method_9541(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
	);

	public class_3749(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16545, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		for (class_2350 lv : arg.method_7718()) {
			if (lv.method_10166() == class_2350.class_2351.field_11052) {
				class_2680 lv2 = this.method_9564().method_11657(field_16545, Boolean.valueOf(lv == class_2350.field_11036));
				if (lv2.method_11591(arg.method_8045(), arg.method_8037())) {
					return lv2;
				}
			}
		}

		return null;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return arg.method_11654(field_16545) ? field_16544 : field_16546;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_16545);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = method_16370(arg).method_10153();
		return class_2248.method_20044(arg2, arg3.method_10093(lv), lv.method_10153());
	}

	protected static class_2350 method_16370(class_2680 arg) {
		return arg.method_11654(field_16545) ? class_2350.field_11033 : class_2350.field_11036;
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return method_16370(arg).method_10153() == arg2 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
