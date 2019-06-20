package net.minecraft;

import java.util.Random;

public class class_2426 extends class_2318 {
	public static final class_2746 field_11322 = class_2741.field_12484;

	public class_2426(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, class_2350.field_11035).method_11657(field_11322, Boolean.valueOf(false)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10927, field_11322);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10927, arg2.method_10503(arg.method_11654(field_10927)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10927)));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11322)) {
			arg2.method_8652(arg3, arg.method_11657(field_11322, Boolean.valueOf(false)), 2);
		} else {
			arg2.method_8652(arg3, arg.method_11657(field_11322, Boolean.valueOf(true)), 2);
			arg2.method_8397().method_8676(arg3, this, 2);
		}

		this.method_10365(arg2, arg3, arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg.method_11654(field_10927) == arg2 && !(Boolean)arg.method_11654(field_11322)) {
			this.method_10366(arg4, arg5);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	private void method_10366(class_1936 arg, class_2338 arg2) {
		if (!arg.method_8608() && !arg.method_8397().method_8674(arg2, this)) {
			arg.method_8397().method_8676(arg2, this, 2);
		}
	}

	protected void method_10365(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = arg3.method_11654(field_10927);
		class_2338 lv2 = arg2.method_10093(lv.method_10153());
		arg.method_8492(lv2, this, arg2);
		arg.method_8508(lv2, this, lv);
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11597(arg2, arg3, arg4);
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11322) && arg.method_11654(field_10927) == arg4 ? 15 : 0;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			if (!arg2.method_8608() && (Boolean)arg.method_11654(field_11322) && !arg2.method_8397().method_8674(arg3, this)) {
				class_2680 lv = arg.method_11657(field_11322, Boolean.valueOf(false));
				arg2.method_8652(arg3, lv, 18);
				this.method_10365(arg2, arg3, lv);
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			if (!arg2.field_9236 && (Boolean)arg.method_11654(field_11322) && arg2.method_8397().method_8674(arg3, this)) {
				this.method_10365(arg2, arg3, arg.method_11657(field_11322, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10927, arg.method_7715().method_10153().method_10153());
	}
}
