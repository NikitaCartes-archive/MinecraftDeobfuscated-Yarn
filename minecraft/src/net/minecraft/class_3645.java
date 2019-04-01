package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;

public class class_3645 {
	protected static final int field_16115 = class_2378.field_11153.method_10249(class_1972.field_9408);
	protected static final int field_16114 = class_2378.field_11153.method_10249(class_1972.field_9441);
	protected static final int field_16113 = class_2378.field_11153.method_10249(class_1972.field_9423);
	protected static final int field_16112 = class_2378.field_11153.method_10249(class_1972.field_9467);
	protected static final int field_16111 = class_2378.field_11153.method_10249(class_1972.field_9435);
	protected static final int field_16110 = class_2378.field_11153.method_10249(class_1972.field_9448);
	protected static final int field_16109 = class_2378.field_11153.method_10249(class_1972.field_9439);
	protected static final int field_16108 = class_2378.field_11153.method_10249(class_1972.field_9446);
	protected static final int field_16107 = class_2378.field_11153.method_10249(class_1972.field_9470);
	protected static final int field_16116 = class_2378.field_11153.method_10249(class_1972.field_9418);

	private static <T extends class_3625, C extends class_3628<T>> class_3627<T> method_15848(
		long l, class_3660 arg, class_3627<T> arg2, int i, LongFunction<C> longFunction
	) {
		class_3627<T> lv = arg2;

		for (int j = 0; j < i; j++) {
			lv = arg.method_15862((class_3628<T>)longFunction.apply(l + (long)j), lv);
		}

		return lv;
	}

	public static <T extends class_3625, C extends class_3628<T>> ImmutableList<class_3627<T>> method_15847(
		class_1942 arg, class_2906 arg2, LongFunction<C> longFunction
	) {
		class_3627<T> lv = class_3643.field_16103.method_15854((class_3628<T>)longFunction.apply(1L));
		lv = class_3656.field_16198.method_15862((class_3628<T>)longFunction.apply(2000L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(1L), lv);
		lv = class_3656.field_16196.method_15862((class_3628<T>)longFunction.apply(2001L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(2L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(50L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(70L), lv);
		lv = class_3651.field_16158.method_15862((class_3628<T>)longFunction.apply(2L), lv);
		class_3627<T> lv2 = class_3644.field_16105.method_15854((class_3628<T>)longFunction.apply(2L));
		lv2 = method_15848(2001L, class_3656.field_16196, lv2, 6, longFunction);
		lv = class_3639.field_16059.method_15862((class_3628<T>)longFunction.apply(2L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(3L), lv);
		lv = class_3632.class_3926.field_17399.method_15862((class_3628<T>)longFunction.apply(2L), lv);
		lv = class_3632.class_3927.field_17401.method_15862((class_3628<T>)longFunction.apply(2L), lv);
		lv = class_3632.class_3635.field_16051.method_15862((class_3628<T>)longFunction.apply(3L), lv);
		lv = class_3656.field_16196.method_15862((class_3628<T>)longFunction.apply(2002L), lv);
		lv = class_3656.field_16196.method_15862((class_3628<T>)longFunction.apply(2003L), lv);
		lv = class_3638.field_16058.method_15862((class_3628<T>)longFunction.apply(4L), lv);
		lv = class_3637.field_16055.method_15862((class_3628<T>)longFunction.apply(5L), lv);
		lv = class_3636.field_16052.method_15862((class_3628<T>)longFunction.apply(4L), lv);
		lv = method_15848(1000L, class_3656.field_16196, lv, 0, longFunction);
		int i = 4;
		int j = i;
		if (arg2 != null) {
			i = arg2.method_12614();
			j = arg2.method_12616();
		}

		if (arg == class_1942.field_9276) {
			i = 6;
		}

		class_3627<T> lv3 = method_15848(1000L, class_3656.field_16196, lv, 0, longFunction);
		lv3 = class_3650.field_16157.method_15862((class_3628)longFunction.apply(100L), lv3);
		class_3627<T> lv4 = new class_3640(arg, arg2).method_15862((class_3628<T>)longFunction.apply(200L), lv);
		lv4 = class_3646.field_16120.method_15862((class_3628)longFunction.apply(1001L), lv4);
		lv4 = method_15848(1000L, class_3656.field_16196, lv4, 2, longFunction);
		lv4 = class_3641.field_16091.method_15862((class_3628)longFunction.apply(1000L), lv4);
		class_3627<T> lv5 = method_15848(1000L, class_3656.field_16196, lv3, 2, longFunction);
		lv4 = class_3648.field_16134.method_15860((class_3628)longFunction.apply(1000L), lv4, lv5);
		lv3 = method_15848(1000L, class_3656.field_16196, lv3, 2, longFunction);
		lv3 = method_15848(1000L, class_3656.field_16196, lv3, j, longFunction);
		lv3 = class_3653.field_16168.method_15862((class_3628)longFunction.apply(1L), lv3);
		lv3 = class_3654.field_16171.method_15862((class_3628)longFunction.apply(1000L), lv3);
		lv4 = class_3649.field_16155.method_15862((class_3628)longFunction.apply(1001L), lv4);

		for (int k = 0; k < i; k++) {
			lv4 = class_3656.field_16196.method_15862((class_3628)longFunction.apply((long)(1000 + k)), lv4);
			if (k == 0) {
				lv4 = class_3638.field_16058.method_15862((class_3628)longFunction.apply(3L), lv4);
			}

			if (k == 1 || i == 1) {
				lv4 = class_3655.field_16184.method_15862((class_3628)longFunction.apply(1000L), lv4);
			}
		}

		lv4 = class_3654.field_16171.method_15862((class_3628)longFunction.apply(1000L), lv4);
		lv4 = class_3652.field_16161.method_15860((class_3628)longFunction.apply(100L), lv4, lv3);
		lv4 = class_3647.field_16121.method_15860((class_3628<T>)longFunction.apply(100L), lv4, lv2);
		class_3627<T> lv7 = class_3657.field_16200.method_15862((class_3628<T>)longFunction.apply(10L), lv4);
		return ImmutableList.of(lv4, lv7, lv4);
	}

	public static class_3642[] method_15843(long l, class_1942 arg, class_2906 arg2) {
		int i = 25;
		ImmutableList<class_3627<class_3626>> immutableList = method_15847(arg, arg2, m -> new class_3631(25, l, m));
		class_3642 lv = new class_3642((class_3627<class_3626>)immutableList.get(0));
		class_3642 lv2 = new class_3642((class_3627<class_3626>)immutableList.get(1));
		class_3642 lv3 = new class_3642((class_3627<class_3626>)immutableList.get(2));
		return new class_3642[]{lv, lv2, lv3};
	}

	public static boolean method_15844(int i, int j) {
		if (i == j) {
			return true;
		} else {
			class_1959 lv = class_2378.field_11153.method_10200(i);
			class_1959 lv2 = class_2378.field_11153.method_10200(j);
			if (lv == null || lv2 == null) {
				return false;
			} else if (lv != class_1972.field_9410 && lv != class_1972.field_9433) {
				return lv.method_8688() != class_1959.class_1961.field_9371
						&& lv2.method_8688() != class_1959.class_1961.field_9371
						&& lv.method_8688() == lv2.method_8688()
					? true
					: lv == lv2;
			} else {
				return lv2 == class_1972.field_9410 || lv2 == class_1972.field_9433;
			}
		}
	}

	protected static boolean method_15845(int i) {
		return i == field_16115
			|| i == field_16114
			|| i == field_16113
			|| i == field_16112
			|| i == field_16111
			|| i == field_16110
			|| i == field_16109
			|| i == field_16108
			|| i == field_16107
			|| i == field_16116;
	}

	protected static boolean method_15846(int i) {
		return i == field_16115 || i == field_16114 || i == field_16113 || i == field_16112 || i == field_16111;
	}
}
