package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class class_3351 {
	private static final class_2960 field_14409 = new class_2960("igloo/top");
	private static final class_2960 field_14407 = new class_2960("igloo/middle");
	private static final class_2960 field_14410 = new class_2960("igloo/bottom");
	private static final Map<class_2960, class_2338> field_14408 = ImmutableMap.of(
		field_14409, new class_2338(3, 5, 5), field_14407, new class_2338(1, 3, 1), field_14410, new class_2338(3, 6, 7)
	);
	private static final Map<class_2960, class_2338> field_14406 = ImmutableMap.of(
		field_14409, new class_2338(0, 0, 0), field_14407, new class_2338(2, -3, 4), field_14410, new class_2338(0, -3, -2)
	);

	public static void method_14705(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3443> list, Random random, class_3111 arg4) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			list.add(new class_3351.class_3352(arg, field_14410, arg2, arg3, i * 3));

			for (int j = 0; j < i - 1; j++) {
				list.add(new class_3351.class_3352(arg, field_14407, arg2, arg3, j * 3));
			}
		}

		list.add(new class_3351.class_3352(arg, field_14409, arg2, arg3, 0));
	}

	public static class class_3352 extends class_3470 {
		private final class_2960 field_14411;
		private final class_2470 field_14412;

		public class_3352(class_3485 arg, class_2960 arg2, class_2338 arg3, class_2470 arg4, int i) {
			super(class_3773.field_16909, 0);
			this.field_14411 = arg2;
			class_2338 lv = (class_2338)class_3351.field_14406.get(arg2);
			this.field_15432 = arg3.method_10069(lv.method_10263(), lv.method_10264() - i, lv.method_10260());
			this.field_14412 = arg4;
			this.method_14708(arg);
		}

		public class_3352(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16909, arg2);
			this.field_14411 = new class_2960(arg2.method_10558("Template"));
			this.field_14412 = class_2470.valueOf(arg2.method_10558("Rot"));
			this.method_14708(arg);
		}

		private void method_14708(class_3485 arg) {
			class_3499 lv = arg.method_15091(this.field_14411);
			class_3492 lv2 = new class_3492()
				.method_15123(this.field_14412)
				.method_15125(class_2415.field_11302)
				.method_15119((class_2338)class_3351.field_14408.get(this.field_14411))
				.method_16184(class_3793.field_16718);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10582("Template", this.field_14411.toString());
			arg.method_10582("Rot", this.field_14412.name());
		}

		@Override
		protected void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3) {
			if ("chest".equals(string)) {
				arg2.method_8652(arg, class_2246.field_10124.method_9564(), 3);
				class_2586 lv = arg2.method_8321(arg.method_10074());
				if (lv instanceof class_2595) {
					((class_2595)lv).method_11285(class_39.field_662, random.nextLong());
				}
			}
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			class_3492 lv = new class_3492()
				.method_15123(this.field_14412)
				.method_15125(class_2415.field_11302)
				.method_15119((class_2338)class_3351.field_14408.get(this.field_14411))
				.method_16184(class_3793.field_16718);
			class_2338 lv2 = (class_2338)class_3351.field_14406.get(this.field_14411);
			class_2338 lv3 = this.field_15432.method_10081(class_3499.method_15171(lv, new class_2338(3 - lv2.method_10263(), 0, 0 - lv2.method_10260())));
			int i = arg.method_8589(class_2902.class_2903.field_13194, lv3.method_10263(), lv3.method_10260());
			class_2338 lv4 = this.field_15432;
			this.field_15432 = this.field_15432.method_10069(0, i - 90 - 1, 0);
			boolean bl = super.method_14931(arg, random, arg2, arg3);
			if (this.field_14411.equals(class_3351.field_14409)) {
				class_2338 lv5 = this.field_15432.method_10081(class_3499.method_15171(lv, new class_2338(3, 0, 5)));
				class_2680 lv6 = arg.method_8320(lv5.method_10074());
				if (!lv6.method_11588() && lv6.method_11614() != class_2246.field_9983) {
					arg.method_8652(lv5, class_2246.field_10491.method_9564(), 3);
				}
			}

			this.field_15432 = lv4;
			return bl;
		}
	}
}
