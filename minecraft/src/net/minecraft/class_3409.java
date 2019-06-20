package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class class_3409 {
	private static final class_2960[] field_14521 = new class_2960[]{
		new class_2960("underwater_ruin/warm_1"),
		new class_2960("underwater_ruin/warm_2"),
		new class_2960("underwater_ruin/warm_3"),
		new class_2960("underwater_ruin/warm_4"),
		new class_2960("underwater_ruin/warm_5"),
		new class_2960("underwater_ruin/warm_6"),
		new class_2960("underwater_ruin/warm_7"),
		new class_2960("underwater_ruin/warm_8")
	};
	private static final class_2960[] field_14518 = new class_2960[]{
		new class_2960("underwater_ruin/brick_1"),
		new class_2960("underwater_ruin/brick_2"),
		new class_2960("underwater_ruin/brick_3"),
		new class_2960("underwater_ruin/brick_4"),
		new class_2960("underwater_ruin/brick_5"),
		new class_2960("underwater_ruin/brick_6"),
		new class_2960("underwater_ruin/brick_7"),
		new class_2960("underwater_ruin/brick_8")
	};
	private static final class_2960[] field_14519 = new class_2960[]{
		new class_2960("underwater_ruin/cracked_1"),
		new class_2960("underwater_ruin/cracked_2"),
		new class_2960("underwater_ruin/cracked_3"),
		new class_2960("underwater_ruin/cracked_4"),
		new class_2960("underwater_ruin/cracked_5"),
		new class_2960("underwater_ruin/cracked_6"),
		new class_2960("underwater_ruin/cracked_7"),
		new class_2960("underwater_ruin/cracked_8")
	};
	private static final class_2960[] field_14522 = new class_2960[]{
		new class_2960("underwater_ruin/mossy_1"),
		new class_2960("underwater_ruin/mossy_2"),
		new class_2960("underwater_ruin/mossy_3"),
		new class_2960("underwater_ruin/mossy_4"),
		new class_2960("underwater_ruin/mossy_5"),
		new class_2960("underwater_ruin/mossy_6"),
		new class_2960("underwater_ruin/mossy_7"),
		new class_2960("underwater_ruin/mossy_8")
	};
	private static final class_2960[] field_14516 = new class_2960[]{
		new class_2960("underwater_ruin/big_brick_1"),
		new class_2960("underwater_ruin/big_brick_2"),
		new class_2960("underwater_ruin/big_brick_3"),
		new class_2960("underwater_ruin/big_brick_8")
	};
	private static final class_2960[] field_14517 = new class_2960[]{
		new class_2960("underwater_ruin/big_mossy_1"),
		new class_2960("underwater_ruin/big_mossy_2"),
		new class_2960("underwater_ruin/big_mossy_3"),
		new class_2960("underwater_ruin/big_mossy_8")
	};
	private static final class_2960[] field_14520 = new class_2960[]{
		new class_2960("underwater_ruin/big_cracked_1"),
		new class_2960("underwater_ruin/big_cracked_2"),
		new class_2960("underwater_ruin/big_cracked_3"),
		new class_2960("underwater_ruin/big_cracked_8")
	};
	private static final class_2960[] field_14515 = new class_2960[]{
		new class_2960("underwater_ruin/big_warm_4"),
		new class_2960("underwater_ruin/big_warm_5"),
		new class_2960("underwater_ruin/big_warm_6"),
		new class_2960("underwater_ruin/big_warm_7")
	};

	private static class_2960 method_14824(Random random) {
		return field_14521[random.nextInt(field_14521.length)];
	}

	private static class_2960 method_14826(Random random) {
		return field_14515[random.nextInt(field_14515.length)];
	}

	public static void method_14827(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3443> list, Random random, class_3114 arg4) {
		boolean bl = random.nextFloat() <= arg4.field_13708;
		float f = bl ? 0.9F : 0.8F;
		method_14822(arg, arg2, arg3, list, random, arg4, bl, f);
		if (bl && random.nextFloat() <= arg4.field_13707) {
			method_14825(arg, random, arg3, arg2, arg4, list);
		}
	}

	private static void method_14825(class_3485 arg, Random random, class_2470 arg2, class_2338 arg3, class_3114 arg4, List<class_3443> list) {
		int i = arg3.method_10263();
		int j = arg3.method_10260();
		class_2338 lv = class_3499.method_15168(new class_2338(15, 0, 15), class_2415.field_11302, arg2, class_2338.field_10980).method_10069(i, 0, j);
		class_3341 lv2 = class_3341.method_14666(i, 0, j, lv.method_10263(), 0, lv.method_10260());
		class_2338 lv3 = new class_2338(Math.min(i, lv.method_10263()), 0, Math.min(j, lv.method_10260()));
		List<class_2338> list2 = method_14821(random, lv3.method_10263(), lv3.method_10260());
		int k = class_3532.method_15395(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list2.isEmpty()) {
				int m = random.nextInt(list2.size());
				class_2338 lv4 = (class_2338)list2.remove(m);
				int n = lv4.method_10263();
				int o = lv4.method_10260();
				class_2470 lv5 = class_2470.values()[random.nextInt(class_2470.values().length)];
				class_2338 lv6 = class_3499.method_15168(new class_2338(5, 0, 6), class_2415.field_11302, lv5, class_2338.field_10980).method_10069(n, 0, o);
				class_3341 lv7 = class_3341.method_14666(n, 0, o, lv6.method_10263(), 0, lv6.method_10260());
				if (!lv7.method_14657(lv2)) {
					method_14822(arg, lv4, lv5, list, random, arg4, false, 0.8F);
				}
			}
		}
	}

	private static List<class_2338> method_14821(Random random, int i, int j) {
		List<class_2338> list = Lists.<class_2338>newArrayList();
		list.add(new class_2338(i - 16 + class_3532.method_15395(random, 1, 8), 90, j + 16 + class_3532.method_15395(random, 1, 7)));
		list.add(new class_2338(i - 16 + class_3532.method_15395(random, 1, 8), 90, j + class_3532.method_15395(random, 1, 7)));
		list.add(new class_2338(i - 16 + class_3532.method_15395(random, 1, 8), 90, j - 16 + class_3532.method_15395(random, 4, 8)));
		list.add(new class_2338(i + class_3532.method_15395(random, 1, 7), 90, j + 16 + class_3532.method_15395(random, 1, 7)));
		list.add(new class_2338(i + class_3532.method_15395(random, 1, 7), 90, j - 16 + class_3532.method_15395(random, 4, 6)));
		list.add(new class_2338(i + 16 + class_3532.method_15395(random, 1, 7), 90, j + 16 + class_3532.method_15395(random, 3, 8)));
		list.add(new class_2338(i + 16 + class_3532.method_15395(random, 1, 7), 90, j + class_3532.method_15395(random, 1, 7)));
		list.add(new class_2338(i + 16 + class_3532.method_15395(random, 1, 7), 90, j - 16 + class_3532.method_15395(random, 4, 8)));
		return list;
	}

	private static void method_14822(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3443> list, Random random, class_3114 arg4, boolean bl, float f) {
		if (arg4.field_13709 == class_3411.class_3413.field_14532) {
			class_2960 lv = bl ? method_14826(random) : method_14824(random);
			list.add(new class_3409.class_3410(arg, lv, arg2, arg3, f, arg4.field_13709, bl));
		} else if (arg4.field_13709 == class_3411.class_3413.field_14528) {
			class_2960[] lvs = bl ? field_14516 : field_14518;
			class_2960[] lvs2 = bl ? field_14520 : field_14519;
			class_2960[] lvs3 = bl ? field_14517 : field_14522;
			int i = random.nextInt(lvs.length);
			list.add(new class_3409.class_3410(arg, lvs[i], arg2, arg3, f, arg4.field_13709, bl));
			list.add(new class_3409.class_3410(arg, lvs2[i], arg2, arg3, 0.7F, arg4.field_13709, bl));
			list.add(new class_3409.class_3410(arg, lvs3[i], arg2, arg3, 0.5F, arg4.field_13709, bl));
		}
	}

	public static class class_3410 extends class_3470 {
		private final class_3411.class_3413 field_14527;
		private final float field_14524;
		private final class_2960 field_14523;
		private final class_2470 field_14526;
		private final boolean field_14525;

		public class_3410(class_3485 arg, class_2960 arg2, class_2338 arg3, class_2470 arg4, float f, class_3411.class_3413 arg5, boolean bl) {
			super(class_3773.field_16932, 0);
			this.field_14523 = arg2;
			this.field_15432 = arg3;
			this.field_14526 = arg4;
			this.field_14524 = f;
			this.field_14527 = arg5;
			this.field_14525 = bl;
			this.method_14828(arg);
		}

		public class_3410(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16932, arg2);
			this.field_14523 = new class_2960(arg2.method_10558("Template"));
			this.field_14526 = class_2470.valueOf(arg2.method_10558("Rot"));
			this.field_14524 = arg2.method_10583("Integrity");
			this.field_14527 = class_3411.class_3413.valueOf(arg2.method_10558("BiomeType"));
			this.field_14525 = arg2.method_10577("IsLarge");
			this.method_14828(arg);
		}

		private void method_14828(class_3485 arg) {
			class_3499 lv = arg.method_15091(this.field_14523);
			class_3492 lv2 = new class_3492().method_15123(this.field_14526).method_15125(class_2415.field_11302).method_16184(class_3793.field_16721);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10582("Template", this.field_14523.toString());
			arg.method_10582("Rot", this.field_14526.name());
			arg.method_10548("Integrity", this.field_14524);
			arg.method_10582("BiomeType", this.field_14527.toString());
			arg.method_10556("IsLarge", this.field_14525);
		}

		@Override
		protected void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3) {
			if ("chest".equals(string)) {
				arg2.method_8652(
					arg,
					class_2246.field_10034.method_9564().method_11657(class_2281.field_10772, Boolean.valueOf(arg2.method_8316(arg).method_15767(class_3486.field_15517))),
					2
				);
				class_2586 lv = arg2.method_8321(arg);
				if (lv instanceof class_2595) {
					((class_2595)lv).method_11285(this.field_14525 ? class_39.field_300 : class_39.field_397, random.nextLong());
				}
			} else if ("drowned".equals(string)) {
				class_1551 lv2 = class_1299.field_6123.method_5883(arg2.method_8410());
				lv2.method_5971();
				lv2.method_5725(arg, 0.0F, 0.0F);
				lv2.method_5943(arg2, arg2.method_8404(arg), class_3730.field_16474, null, null);
				arg2.method_8649(lv2);
				if (arg.method_10264() > arg2.method_8615()) {
					arg2.method_8652(arg, class_2246.field_10124.method_9564(), 2);
				} else {
					arg2.method_8652(arg, class_2246.field_10382.method_9564(), 2);
				}
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			this.field_15434.method_16183().method_16184(new class_3488(this.field_14524)).method_16184(class_3793.field_16721);
			int i = arg.method_8589(class_2902.class_2903.field_13195, this.field_15432.method_10263(), this.field_15432.method_10260());
			this.field_15432 = new class_2338(this.field_15432.method_10263(), i, this.field_15432.method_10260());
			class_2338 lv = class_3499.method_15168(
					new class_2338(this.field_15433.method_15160().method_10263() - 1, 0, this.field_15433.method_15160().method_10260() - 1),
					class_2415.field_11302,
					this.field_14526,
					class_2338.field_10980
				)
				.method_10081(this.field_15432);
			this.field_15432 = new class_2338(this.field_15432.method_10263(), this.method_14829(this.field_15432, arg, lv), this.field_15432.method_10260());
			return super.method_14931(arg, random, arg2, arg3);
		}

		private int method_14829(class_2338 arg, class_1922 arg2, class_2338 arg3) {
			int i = arg.method_10264();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (class_2338 lv : class_2338.method_10097(arg, arg3)) {
				int m = lv.method_10263();
				int n = lv.method_10260();
				int o = arg.method_10264() - 1;
				class_2338.class_2339 lv2 = new class_2338.class_2339(m, o, n);
				class_2680 lv3 = arg2.method_8320(lv2);

				for (class_3610 lv4 = arg2.method_8316(lv2);
					(lv3.method_11588() || lv4.method_15767(class_3486.field_15517) || lv3.method_11614().method_9525(class_3481.field_15467)) && o > 1;
					lv4 = arg2.method_8316(lv2)
				) {
					lv2.method_10103(m, --o, n);
					lv3 = arg2.method_8320(lv2);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(arg.method_10263() - arg3.method_10263());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
