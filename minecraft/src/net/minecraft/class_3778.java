package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3778 {
	private static final Logger field_16665 = LogManager.getLogger();
	public static final class_3787 field_16666 = new class_3787();

	public static void method_16605(
		class_2960 arg, int i, class_3778.class_3779 arg2, class_2794<?> arg3, class_3485 arg4, class_2338 arg5, List<class_3443> list, Random random
	) {
		class_3420.method_16651();
		class_2470 lv = class_2470.method_16548(random);
		class_3785 lv2 = field_16666.method_16639(arg);
		class_3784 lv3 = lv2.method_16631(random);
		class_3790 lv4 = arg2.create(arg4, lv3, arg5, 1, lv);
		class_3341 lv5 = lv4.method_14935();
		int j = (lv5.field_14378 + lv5.field_14381) / 2;
		int k = (lv5.field_14376 + lv5.field_14379) / 2;
		lv4.method_14922(0, arg3.method_16397(j, k, class_2902.class_2903.field_13194) - lv5.field_14380, 0);
		method_16607(arg2, lv4, arg3, arg4, list, random, 0, i);
	}

	private static void method_16607(
		class_3778.class_3779 arg, class_3790 arg2, class_2794<?> arg3, class_3485 arg4, List<class_3443> list, Random random, int i, int j
	) {
		list.add(arg2);
		if (i <= j) {
			class_3784 lv = arg2.method_16644();
			class_2338 lv2 = arg2.method_16648();
			class_2470 lv3 = arg2.method_16888();
			List<class_3341> list2 = Lists.<class_3341>newArrayList();

			for (class_3499.class_3501 lv4 : lv.method_16627(arg4, lv2, lv3, random)) {
				class_2350 lv5 = lv4.field_15596.method_11654(class_3748.field_10927);
				class_2338 lv6 = lv4.field_15597.method_10093(lv5);
				class_3785 lv7 = field_16666.method_16639(new class_2960(lv4.field_15595.method_10558("target_pool")));
				class_3785 lv8 = field_16666.method_16639(lv7.method_16634());
				if (lv7 != class_3785.field_16746 && (lv7.method_16632() != 0 || lv7 == class_3785.field_16679)) {
					class_3341 lv9 = lv.method_16628(arg4, lv2, lv3);
					if (i == j || !method_16606(arg, arg2, arg3, arg4, list, random, lv, lv4, lv9, list2, lv6, lv7, i, j)) {
						method_16606(arg, arg2, arg3, arg4, list, random, lv, lv4, lv9, list2, lv6, lv8, i, j);
					}
				} else {
					field_16665.warn("Empty or none existent pool: {}", lv4.field_15595.method_10558("target_pool"));
				}
			}
		}
	}

	private static boolean method_16606(
		class_3778.class_3779 arg,
		class_3790 arg2,
		class_2794<?> arg3,
		class_3485 arg4,
		List<class_3443> list,
		Random random,
		class_3784 arg5,
		class_3499.class_3501 arg6,
		class_3341 arg7,
		List<class_3341> list2,
		class_2338 arg8,
		class_3785 arg9,
		int i,
		int j
	) {
		boolean bl = arg7.method_14662(arg8);

		for (int k : arg9.method_16633(random)) {
			class_3784 lv = arg9.method_16630(k);
			if (lv == class_3777.field_16663) {
				return true;
			}

			for (class_2470 lv2 : class_2470.method_16547(random)) {
				for (class_3499.class_3501 lv3 : lv.method_16627(arg4, new class_2338(0, 0, 0), lv2, random)) {
					if (class_3748.method_16546(arg6, lv3)) {
						class_2338 lv4 = new class_2338(
							arg8.method_10263() - lv3.field_15597.method_10263(),
							arg8.method_10264() - lv3.field_15597.method_10264(),
							arg8.method_10260() - lv3.field_15597.method_10260()
						);
						class_3785.class_3786 lv5 = arg2.method_16644().method_16624();
						class_3785.class_3786 lv6 = lv.method_16624();
						int l = arg6.field_15597.method_10264() - arg7.field_14380;
						int m = lv3.field_15597.method_10264();
						int n = l - m + ((class_2350)arg6.field_15596.method_11654(class_3748.field_10927)).method_10164();
						int o = arg2.method_16646();
						int p;
						if (lv6 == class_3785.class_3786.field_16687) {
							p = o - n;
						} else {
							p = 1;
						}

						class_3341 lv7 = lv.method_16628(arg4, lv4, lv2);
						int q;
						if (lv5 == class_3785.class_3786.field_16687 && lv6 == class_3785.class_3786.field_16687) {
							q = arg7.field_14380 + n;
						} else {
							q = arg3.method_16397(arg6.field_15597.method_10263(), arg6.field_15597.method_10260(), class_2902.class_2903.field_13194) - 1 + n;
						}

						int r = q - lv7.field_14380;
						lv7.method_14661(0, r, 0);
						if (lv7.field_14377 - lv7.field_14380 < 16) {
							lv7.field_14377 += 8;
							lv7.field_14380 -= 8;
						}

						lv4 = lv4.method_10069(0, r, 0);
						if (bl && method_16608(arg7, list2, lv7) || method_16604(list, lv7)) {
							if (bl) {
								list2.add(lv7);
							}

							class_3790 lv8 = arg.create(arg4, lv, lv4, p, lv2);
							int s;
							if (lv5 == class_3785.class_3786.field_16687) {
								s = arg7.field_14380 + l;
							} else if (lv6 == class_3785.class_3786.field_16687) {
								s = q + m;
							} else {
								s = arg3.method_16397(arg6.field_15597.method_10263(), arg6.field_15597.method_10260(), class_2902.class_2903.field_13194) - 1 + n / 2;
							}

							arg2.method_16647(new class_3780(arg8.method_10263(), s - l + o, arg8.method_10260(), n, lv6));
							lv8.method_16647(new class_3780(arg6.field_15597.method_10263(), s - m + p, arg6.field_15597.method_10260(), -n, arg5.method_16624()));
							method_16607(arg, lv8, arg3, arg4, list, random, i + 1, j);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private static boolean method_16608(class_3341 arg, List<class_3341> list, class_3341 arg2) {
		if (arg2.field_14381 >= arg.field_14381 && arg2.field_14378 <= arg.field_14378 && arg2.field_14379 >= arg.field_14379 && arg2.field_14376 <= arg.field_14376) {
			for (class_3341 lv : list) {
				if (arg2.method_14657(lv)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private static boolean method_16604(List<class_3443> list, class_3341 arg) {
		for (class_3443 lv : list) {
			if (arg.method_14657(lv.method_14935())) {
				return false;
			}
		}

		return true;
	}

	static {
		field_16666.method_16640(class_3785.field_16679);
	}

	public interface class_3779 {
		class_3790 create(class_3485 arg, class_3784 arg2, class_2338 arg3, int i, class_2470 arg4);
	}
}
