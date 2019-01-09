package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2488 extends class_2248 {
	public static final class_2758 field_11518 = class_2741.field_12536;
	protected static final class_265[] field_11517 = new class_265[]{
		class_259.method_1073(),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected class_2488(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11518, Integer.valueOf(1)));
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return (Integer)arg.method_11654(field_11518) < 5;
			case field_48:
				return false;
			case field_51:
				return false;
			default:
				return false;
		}
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11517[arg.method_11654(field_11518)];
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11517[arg.method_11654(field_11518) - 1];
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3.method_10074());
		class_2248 lv2 = lv.method_11614();
		return lv2 != class_2246.field_10295 && lv2 != class_2246.field_10225 && lv2 != class_2246.field_10499
			? class_2248.method_9501(lv.method_11628(arg2, arg3.method_10074()), class_2350.field_11036)
				|| lv.method_11602(class_3481.field_15503)
				|| lv2 == this && (Integer)lv.method_11654(field_11518) == 8
			: false;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.method_8314(class_1944.field_9282, arg3) > 11) {
			method_9497(arg, arg2, arg3);
			arg2.method_8650(arg3);
		}
	}

	@Override
	public boolean method_9616(class_2680 arg, class_1750 arg2) {
		int i = (Integer)arg.method_11654(field_11518);
		if (arg2.method_8041().method_7909() != this.method_8389() || i >= 8) {
			return i == 1;
		} else {
			return arg2.method_7717() ? arg2.method_8038() == class_2350.field_11036 : true;
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037());
		if (lv.method_11614() == this) {
			int i = (Integer)lv.method_11654(field_11518);
			return lv.method_11657(field_11518, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.method_9605(arg);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11518);
	}
}
