package net.minecraft;

import java.util.Random;

public class class_2344 extends class_2248 {
	public static final class_2758 field_11009 = class_2741.field_12510;
	protected static final class_265 field_11010 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

	protected class_2344(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11009, Integer.valueOf(0)));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11036 && !arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3.method_10084());
		return !lv.method_11620().method_15799() || lv.method_11614() instanceof class_2349;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return !this.method_9564().method_11591(arg.method_8045(), arg.method_8037()) ? class_2246.field_10566.method_9564() : super.method_9605(arg);
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11010;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			method_10125(arg, arg2, arg3);
		} else {
			int i = (Integer)arg.method_11654(field_11009);
			if (!method_10126(arg2, arg3) && !arg2.method_8520(arg3.method_10084())) {
				if (i > 0) {
					arg2.method_8652(arg3, arg.method_11657(field_11009, Integer.valueOf(i - 1)), 2);
				} else if (!method_10124(arg2, arg3)) {
					method_10125(arg, arg2, arg3);
				}
			} else if (i < 7) {
				arg2.method_8652(arg3, arg.method_11657(field_11009, Integer.valueOf(7)), 2);
			}
		}
	}

	@Override
	public void method_9554(class_1937 arg, class_2338 arg2, class_1297 arg3, float f) {
		if (!arg.field_9236
			&& arg.field_9229.nextFloat() < f - 0.5F
			&& arg3 instanceof class_1309
			&& (arg3 instanceof class_1657 || arg.method_8450().method_8355("mobGriefing"))
			&& arg3.method_17681() * arg3.method_17681() * arg3.method_17682() > 0.512F) {
			method_10125(arg.method_8320(arg2), arg, arg2);
		}

		super.method_9554(arg, arg2, arg3, f);
	}

	public static void method_10125(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		arg2.method_8501(arg3, method_9582(arg, class_2246.field_10566.method_9564(), arg2, arg3));
	}

	private static boolean method_10124(class_1922 arg, class_2338 arg2) {
		class_2248 lv = arg.method_8320(arg2.method_10084()).method_11614();
		return lv instanceof class_2302 || lv instanceof class_2513 || lv instanceof class_2195;
	}

	private static boolean method_10126(class_1941 arg, class_2338 arg2) {
		for (class_2338 lv : class_2338.method_10097(arg2.method_10069(-4, 0, -4), arg2.method_10069(4, 1, 4))) {
			if (arg.method_8316(lv).method_15767(class_3486.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11009);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
