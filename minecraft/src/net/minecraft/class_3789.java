package net.minecraft;

import java.util.Random;

public class class_3789 {
	public static class class_3339 extends class_3443 {
		public class_3339(class_2338 arg) {
			super(class_3773.field_16960, 0);
			this.field_15315 = new class_3341(arg.method_10263(), arg.method_10264(), arg.method_10260(), arg.method_10263(), arg.method_10264(), arg.method_10260());
		}

		public class_3339(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16960, arg2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
		}

		@Override
		public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
			int i = arg.method_8589(class_2902.class_2903.field_13195, this.field_15315.field_14381, this.field_15315.field_14379);
			class_2338.class_2339 lv = new class_2338.class_2339(this.field_15315.field_14381, i, this.field_15315.field_14379);

			while (lv.method_10264() > 0) {
				class_2680 lv2 = arg.method_8320(lv);
				class_2680 lv3 = arg.method_8320(lv.method_10074());
				if (lv3 == class_2246.field_9979.method_9564()
					|| lv3 == class_2246.field_10340.method_9564()
					|| lv3 == class_2246.field_10115.method_9564()
					|| lv3 == class_2246.field_10474.method_9564()
					|| lv3 == class_2246.field_10508.method_9564()) {
					class_2680 lv4 = !lv2.method_11588() && !this.method_14655(lv2) ? lv2 : class_2246.field_10102.method_9564();

					for (class_2350 lv5 : class_2350.values()) {
						class_2338 lv6 = lv.method_10093(lv5);
						class_2680 lv7 = arg.method_8320(lv6);
						if (lv7.method_11588() || this.method_14655(lv7)) {
							class_2338 lv8 = lv6.method_10074();
							class_2680 lv9 = arg.method_8320(lv8);
							if ((lv9.method_11588() || this.method_14655(lv9)) && lv5 != class_2350.field_11036) {
								arg.method_8652(lv6, lv3, 3);
							} else {
								arg.method_8652(lv6, lv4, 3);
							}
						}
					}

					return this.method_14921(
						arg, arg2, random, new class_2338(this.field_15315.field_14381, lv.method_10264(), this.field_15315.field_14379), class_39.field_251, null
					);
				}

				lv.method_10100(0, -1, 0);
			}

			return false;
		}

		private boolean method_14655(class_2680 arg) {
			return arg == class_2246.field_10382.method_9564() || arg == class_2246.field_10164.method_9564();
		}
	}
}
