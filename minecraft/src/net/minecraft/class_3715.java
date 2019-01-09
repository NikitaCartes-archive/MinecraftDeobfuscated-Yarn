package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_3715 extends class_2237 {
	public static final class_2753 field_16404 = class_2383.field_11177;
	public static final class_2746 field_17365 = class_2741.field_12484;
	public static final class_2746 field_17366 = class_2741.field_17393;
	public static final class_265 field_16406 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final class_265 field_16405 = class_2248.method_9541(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
	public static final class_265 field_16403 = class_259.method_1084(field_16406, field_16405);
	public static final class_265 field_17367 = class_2248.method_9541(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	public static final class_265 field_17368 = class_259.method_1084(field_16403, field_17367);
	public static final class_265 field_17369 = class_259.method_1084(
		class_259.method_1084(class_2248.method_9541(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), class_2248.method_9541(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0)),
		class_259.method_1084(class_2248.method_9541(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), field_16403)
	);
	public static final class_265 field_17370 = class_259.method_1084(
		class_259.method_1084(class_2248.method_9541(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), class_2248.method_9541(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667)),
		class_259.method_1084(class_2248.method_9541(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), field_16403)
	);
	public static final class_265 field_17371 = class_259.method_1084(
		class_259.method_1084(class_2248.method_9541(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0), class_2248.method_9541(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0)),
		class_259.method_1084(class_2248.method_9541(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0), field_16403)
	);
	public static final class_265 field_17372 = class_259.method_1084(
		class_259.method_1084(class_2248.method_9541(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667), class_2248.method_9541(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333)),
		class_259.method_1084(class_2248.method_9541(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0), field_16403)
	);

	protected class_3715(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_16404, class_2350.field_11043)
				.method_11657(field_17365, Boolean.valueOf(false))
				.method_11657(field_17366, Boolean.valueOf(false))
		);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_16404, arg.method_8042().method_10153());
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_17368;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		switch ((class_2350)arg.method_11654(field_16404)) {
			case field_11043:
				return field_17370;
			case field_11035:
				return field_17372;
			case field_11034:
				return field_17371;
			case field_11039:
				return field_17369;
			default:
				return field_16403;
		}
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_16404, arg2.method_10503(arg.method_11654(field_16404)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_16404)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_16404, field_17365, field_17366);
	}

	@Nullable
	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3722();
	}

	public static boolean method_17472(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1799 arg4) {
		if (!(Boolean)arg3.method_11654(field_17366)) {
			if (!arg.field_9236) {
				method_17475(arg, arg2, arg3, arg4);
			}

			return true;
		} else {
			return false;
		}
	}

	private static void method_17475(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1799 arg4) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_3722) {
			class_3722 lv2 = (class_3722)lv;
			lv2.method_17513(arg4.method_7971(1));
			method_17473(arg, arg2, arg3, true);
			arg.method_8396(null, arg2, class_3417.field_17482, class_3419.field_15245, 1.0F, 1.0F);
		}
	}

	public static void method_17473(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		arg.method_8652(arg2, arg3.method_11657(field_17365, Boolean.valueOf(false)).method_11657(field_17366, Boolean.valueOf(bl)), 3);
		method_17474(arg, arg2, arg3);
	}

	public static void method_17471(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		method_17476(arg, arg2, arg3, true);
		arg.method_8397().method_8676(arg2, arg3.method_11614(), 2);
		arg.method_8535(1043, arg2, 0);
	}

	private static void method_17476(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		arg.method_8652(arg2, arg3.method_11657(field_17365, Boolean.valueOf(bl)), 3);
		method_17474(arg, arg2, arg3);
	}

	private static void method_17474(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		arg.method_8452(arg2.method_10074(), arg3.method_11614());
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			method_17476(arg2, arg3, arg, false);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			if ((Boolean)arg.method_11654(field_17366)) {
				this.method_17477(arg, arg2, arg3);
			}

			if ((Boolean)arg.method_11654(field_17365)) {
				arg2.method_8452(arg3.method_10074(), this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	private void method_17477(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_3722) {
			class_3722 lv2 = (class_3722)lv;
			class_2350 lv3 = arg.method_11654(field_16404);
			class_1799 lv4 = lv2.method_17520().method_7972();
			float f = 0.25F * (float)lv3.method_10148();
			float g = 0.25F * (float)lv3.method_10165();
			class_1542 lv5 = new class_1542(
				arg2, (double)arg3.method_10263() + 0.5 + (double)f, (double)(arg3.method_10264() + 1), (double)arg3.method_10260() + 0.5 + (double)g, lv4
			);
			lv5.method_6988();
			arg2.method_8649(lv5);
			lv2.method_5448();
		}
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_17365) ? 15 : 0;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg4 == class_2350.field_11036 && arg.method_11654(field_17365) ? 15 : 0;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		if ((Boolean)arg.method_11654(field_17366)) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_3722) {
				return ((class_3722)lv).method_17524();
			}
		}

		return 0;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if ((Boolean)arg.method_11654(field_17366)) {
			if (!arg2.field_9236) {
				this.method_17470(arg2, arg3, arg4);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return !arg.method_11654(field_17366) ? null : super.method_17454(arg, arg2, arg3);
	}

	private void method_17470(class_1937 arg, class_2338 arg2, class_1657 arg3) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_3722) {
			arg3.method_17355((class_3722)lv);
			arg3.method_7281(class_3468.field_17485);
		}
	}
}
