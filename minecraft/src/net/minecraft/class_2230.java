package net.minecraft;

import javax.annotation.Nullable;

public class class_2230 extends class_2248 implements class_3737 {
	public static final class_2746 field_9940 = class_2741.field_12508;
	private static final class_265 field_9939 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected class_2230(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9940, Boolean.valueOf(true)));
	}

	protected void method_9430(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		if (!method_9431(arg, arg2, arg3)) {
			arg2.method_8397().method_8676(arg3, this, 60 + arg2.method_8409().nextInt(40));
		}
	}

	protected static boolean method_9431(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if ((Boolean)arg.method_11654(field_9940)) {
			return true;
		} else {
			for (class_2350 lv : class_2350.values()) {
				if (arg2.method_8316(arg3.method_10093(lv)).method_15767(class_3486.field_15517)) {
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return this.method_9564().method_11657(field_9940, Boolean.valueOf(lv.method_15767(class_3486.field_15517) && lv.method_15761() == 8));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_9939;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_9940)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2 == class_2350.field_11033 && !this.method_9558(arg, arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return arg2.method_8320(lv).method_11631(arg2, lv);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9940);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_9940) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}
}
