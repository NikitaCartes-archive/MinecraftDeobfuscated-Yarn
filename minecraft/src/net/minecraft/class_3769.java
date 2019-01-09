package net.minecraft;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class class_3769 {
	private static final List<class_3769.class_3731> field_16651 = Arrays.asList(
		new class_3769.class_3731(class_1299.field_6105, 80), new class_3769.class_3731(class_1299.field_6117, 20)
	);
	private int field_16652;

	public int method_16574(class_1937 arg, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = arg.field_9229;
			this.field_16652--;
			if (this.field_16652 > 0) {
				return 0;
			} else {
				this.field_16652 = this.field_16652 + 6000 + random.nextInt(1200);
				long l = arg.method_8532() / 24000L;
				if (l < 5L || !arg.method_8530()) {
					return 0;
				} else if (random.nextInt(5) != 0) {
					return 0;
				} else {
					int i = arg.field_9228.size();
					if (i < 1) {
						return 0;
					} else {
						class_1657 lv = (class_1657)arg.field_9228.get(random.nextInt(i));
						if (lv.method_7325()) {
							return 0;
						} else {
							int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							class_2338 lv2 = new class_2338(lv).method_10069(j, 0, k);
							if (!arg.method_8627(
								lv2.method_10263() - 10, lv2.method_10264() - 10, lv2.method_10260() - 10, lv2.method_10263() + 10, lv2.method_10264() + 10, lv2.method_10260() + 10
							)) {
								return 0;
							} else {
								class_1959 lv3 = arg.method_8310(lv2);
								class_1959.class_1961 lv4 = lv3.method_8688();
								if (lv4 != class_1959.class_1961.field_9355
									&& lv4 != class_1959.class_1961.field_9361
									&& lv4 != class_1959.class_1961.field_9368
									&& lv4 != class_1959.class_1961.field_9356) {
									return 0;
								} else {
									int m = 1;
									this.method_16575(arg, lv2, random, true);
									int n = (int)Math.ceil((double)arg.method_8404(lv2).method_5457());

									for (int o = 0; o < n; o++) {
										m++;
										this.method_16575(arg, lv2, random, false);
									}

									return m;
								}
							}
						}
					}
				}
			}
		}
	}

	private void method_16575(class_1937 arg, class_2338 arg2, Random random, boolean bl) {
		class_3769.class_3731 lv = class_3549.method_15446(random, field_16651);
		class_3732 lv2 = lv.field_16476.method_5883(arg);
		if (lv2 != null) {
			double d = (double)(arg2.method_10263() + random.nextInt(5) - random.nextInt(5));
			double e = (double)(arg2.method_10260() + random.nextInt(5) - random.nextInt(5));
			class_2338 lv3 = lv2.field_6002.method_8598(class_2902.class_2903.field_13203, new class_2338(d, (double)arg2.method_10264(), e));
			if (bl) {
				lv2.method_16217(true);
				lv2.method_16218();
			}

			lv2.method_5814((double)lv3.method_10263(), (double)lv3.method_10264(), (double)lv3.method_10260());
			lv2.method_5943(arg, arg.method_8404(lv3), class_3730.field_16527, null, null);
			arg.method_8649(lv2);
		}
	}

	public static class class_3731 extends class_3549.class_3550 {
		public final class_1299<? extends class_3732> field_16476;

		public class_3731(class_1299<? extends class_3732> arg, int i) {
			super(i);
			this.field_16476 = arg;
		}
	}
}
