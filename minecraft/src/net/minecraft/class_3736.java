package net.minecraft;

import java.util.Random;

public class class_3736 extends class_2248 implements class_3737 {
	private static final class_265 field_16494;
	private static final class_265 field_16497;
	private static final class_265 field_17577 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	private static final class_265 field_17578 = class_259.method_1077().method_1096(0.0, -1.0, 0.0);
	public static final class_2758 field_16495 = class_2741.field_16503;
	public static final class_2746 field_16496 = class_2741.field_12508;
	public static final class_2746 field_16547 = class_2741.field_16562;

	protected class_3736(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_16495, Integer.valueOf(7))
				.method_11657(field_16496, Boolean.valueOf(false))
				.method_11657(field_16547, Boolean.valueOf(false))
		);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_16495, field_16496, field_16547);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		if (!arg4.method_17785(arg.method_11614().method_8389())) {
			return arg.method_11654(field_16547) ? field_16497 : field_16494;
		} else {
			return class_259.method_1077();
		}
	}

	@Override
	public class_265 method_9584(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return class_259.method_1077();
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		return arg2.method_8041().method_7909() == this.method_8389();
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		class_1937 lv2 = arg.method_8045();
		int i = this.method_16372(lv2, lv);
		return this.method_9564()
			.method_11657(field_16496, Boolean.valueOf(lv2.method_8316(lv).method_15772() == class_3612.field_15910))
			.method_11657(field_16495, Integer.valueOf(i))
			.method_11657(field_16547, Boolean.valueOf(this.method_16373(lv2, lv, i)));
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!arg2.field_9236) {
			arg2.method_8397().method_8676(arg3, this, 1);
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_16496)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		if (!arg4.method_8608()) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return arg;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		int i = this.method_16372(arg2, arg3);
		class_2680 lv = arg.method_11657(field_16495, Integer.valueOf(i)).method_11657(field_16547, Boolean.valueOf(this.method_16373(arg2, arg3, i)));
		if ((Integer)lv.method_11654(field_16495) == 7) {
			if ((Integer)arg.method_11654(field_16495) == 7) {
				arg2.method_8649(
					new class_1540(
						arg2,
						(double)arg3.method_10263() + 0.5,
						(double)arg3.method_10264(),
						(double)arg3.method_10260() + 0.5,
						lv.method_11657(field_16496, Boolean.valueOf(false))
					)
				);
			} else {
				arg2.method_8651(arg3, true);
			}
		} else if (arg != lv) {
			arg2.method_8652(arg3, lv, 3);
		}
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		if (arg4.method_16192(class_259.method_1077(), arg3, true) && !arg4.method_16193()) {
			return field_16494;
		} else {
			return arg.method_11654(field_16495) != 0 && arg.method_11654(field_16547) && arg4.method_16192(field_17578, arg3, true)
				? field_17577
				: class_259.method_1073();
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_16496) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	private boolean method_16373(class_1922 arg, class_2338 arg2, int i) {
		return i > 0 && arg.method_8320(arg2.method_10074()).method_11614() != this;
	}

	private int method_16372(class_1922 arg, class_2338 arg2) {
		class_2338.class_2339 lv = new class_2338.class_2339(arg2).method_10098(class_2350.field_11033);
		class_2680 lv2 = arg.method_8320(lv);
		int i = 7;
		if (lv2.method_11614() == this) {
			i = (Integer)lv2.method_11654(field_16495);
		} else if (class_2248.method_20045(lv2, arg, lv, class_2350.field_11036)) {
			return 0;
		}

		for (class_2350 lv3 : class_2350.class_2353.field_11062) {
			class_2680 lv4 = arg.method_8320(lv.method_10101(arg2).method_10098(lv3));
			if (lv4.method_11614() == this) {
				i = Math.min(i, (Integer)lv4.method_11654(field_16495) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return i;
	}

	static {
		class_265 lv = class_2248.method_9541(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
		class_265 lv2 = class_2248.method_9541(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
		class_265 lv3 = class_2248.method_9541(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
		class_265 lv4 = class_2248.method_9541(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
		class_265 lv5 = class_2248.method_9541(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
		field_16494 = class_259.method_17786(lv, lv2, lv3, lv4, lv5);
		class_265 lv6 = class_2248.method_9541(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
		class_265 lv7 = class_2248.method_9541(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
		class_265 lv8 = class_2248.method_9541(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
		class_265 lv9 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
		field_16497 = class_259.method_17786(class_3736.field_17577, field_16494, lv7, lv6, lv9, lv8);
	}
}
