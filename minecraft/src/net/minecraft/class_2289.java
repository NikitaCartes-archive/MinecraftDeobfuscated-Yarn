package net.minecraft;

import javax.annotation.Nullable;

public class class_2289 extends class_2237 implements class_3737 {
	public static final class_2746 field_10794 = class_2741.field_12508;
	protected static final class_265 field_10795 = class_2248.method_9541(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	public class_2289(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10794, Boolean.valueOf(true)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10794);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2597();
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11456;
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_10794) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10794)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10795;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2580) {
				((class_2580)lv).method_10936(arg5.method_7964());
			}
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return this.method_9564().method_11657(field_10794, Boolean.valueOf(lv.method_15767(class_3486.field_15517) && lv.method_15761() == 8));
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
