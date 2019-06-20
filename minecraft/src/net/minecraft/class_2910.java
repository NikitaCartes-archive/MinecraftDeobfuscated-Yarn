package net.minecraft;

import java.util.Random;

public class class_2910 {
	private int field_13244;

	public int method_12639(class_3218 arg, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = arg.field_9229;
			this.field_13244--;
			if (this.field_13244 > 0) {
				return 0;
			} else {
				this.field_13244 = this.field_13244 + (60 + random.nextInt(60)) * 20;
				if (arg.method_8594() < 5 && arg.field_9247.method_12451()) {
					return 0;
				} else {
					int i = 0;

					for (class_1657 lv : arg.method_18456()) {
						if (!lv.method_7325()) {
							class_2338 lv2 = new class_2338(lv);
							if (!arg.field_9247.method_12451() || lv2.method_10264() >= arg.method_8615() && arg.method_8311(lv2)) {
								class_1266 lv3 = arg.method_8404(lv2);
								if (lv3.method_5455(random.nextFloat() * 3.0F)) {
									class_3442 lv4 = ((class_3222)lv).method_14248();
									int j = class_3532.method_15340(lv4.method_15025(class_3468.field_15419.method_14956(class_3468.field_15429)), 1, Integer.MAX_VALUE);
									int k = 24000;
									if (random.nextInt(j) >= 72000) {
										class_2338 lv5 = lv2.method_10086(20 + random.nextInt(15)).method_10089(-10 + random.nextInt(21)).method_10077(-10 + random.nextInt(21));
										class_2680 lv6 = arg.method_8320(lv5);
										class_3610 lv7 = arg.method_8316(lv5);
										if (class_1948.method_8662(arg, lv5, lv6, lv7)) {
											class_1315 lv8 = null;
											int l = 1 + random.nextInt(lv3.method_5454().method_5461() + 1);

											for (int m = 0; m < l; m++) {
												class_1593 lv9 = class_1299.field_6078.method_5883(arg);
												lv9.method_5725(lv5, 0.0F, 0.0F);
												lv8 = lv9.method_5943(arg, lv3, class_3730.field_16459, lv8, null);
												arg.method_8649(lv9);
											}

											i += l;
										}
									}
								}
							}
						}
					}

					return i;
				}
			}
		}
	}
}
