package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2282 extends class_2383 implements class_2256 {
	public static final class_2758 field_10779 = class_2741.field_12556;
	protected static final class_265[] field_10778 = new class_265[]{
		class_2248.method_9541(11.0, 7.0, 6.0, 15.0, 12.0, 10.0),
		class_2248.method_9541(9.0, 5.0, 5.0, 15.0, 12.0, 11.0),
		class_2248.method_9541(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final class_265[] field_10776 = new class_265[]{
		class_2248.method_9541(1.0, 7.0, 6.0, 5.0, 12.0, 10.0),
		class_2248.method_9541(1.0, 5.0, 5.0, 7.0, 12.0, 11.0),
		class_2248.method_9541(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final class_265[] field_10777 = new class_265[]{
		class_2248.method_9541(6.0, 7.0, 1.0, 10.0, 12.0, 5.0),
		class_2248.method_9541(5.0, 5.0, 1.0, 11.0, 12.0, 7.0),
		class_2248.method_9541(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final class_265[] field_10780 = new class_265[]{
		class_2248.method_9541(6.0, 7.0, 11.0, 10.0, 12.0, 15.0),
		class_2248.method_9541(5.0, 5.0, 9.0, 11.0, 12.0, 15.0),
		class_2248.method_9541(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	public class_2282(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11177, class_2350.field_11043).method_11657(field_10779, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.field_9229.nextInt(5) == 0) {
			int i = (Integer)arg.method_11654(field_10779);
			if (i < 2) {
				arg2.method_8652(arg3, arg.method_11657(field_10779, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2248 lv = arg2.method_8320(arg3.method_10093(arg.method_11654(field_11177))).method_11614();
		return lv.method_9525(class_3481.field_15474);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		int i = (Integer)arg.method_11654(field_10779);
		switch ((class_2350)arg.method_11654(field_11177)) {
			case field_11035:
				return field_10780[i];
			case field_11043:
			default:
				return field_10777[i];
			case field_11039:
				return field_10776[i];
			case field_11034:
				return field_10778[i];
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564();
		class_1941 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();

		for (class_2350 lv4 : arg.method_7718()) {
			if (lv4.method_10166().method_10179()) {
				lv = lv.method_11657(field_11177, lv4);
				if (lv.method_11591(lv2, lv3)) {
					return lv;
				}
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == arg.method_11654(field_11177) && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return (Integer)arg3.method_11654(field_10779) < 2;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		arg.method_8652(arg2, arg3.method_11657(field_10779, Integer.valueOf((Integer)arg3.method_11654(field_10779) + 1)), 2);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_10779);
	}
}
