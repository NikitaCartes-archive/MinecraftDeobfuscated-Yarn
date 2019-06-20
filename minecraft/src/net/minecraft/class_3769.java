package net.minecraft;

import java.util.Random;

public class class_3769 {
	private int field_16652;

	public int method_16574(class_3218 arg, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = arg.field_9229;
			this.field_16652--;
			if (this.field_16652 > 0) {
				return 0;
			} else {
				this.field_16652 = this.field_16652 + 12000 + random.nextInt(1200);
				long l = arg.method_8532() / 24000L;
				if (l < 5L || !arg.method_8530()) {
					return 0;
				} else if (random.nextInt(5) != 0) {
					return 0;
				} else {
					int i = arg.method_18456().size();
					if (i < 1) {
						return 0;
					} else {
						class_1657 lv = (class_1657)arg.method_18456().get(random.nextInt(i));
						if (lv.method_7325()) {
							return 0;
						} else if (arg.method_19500(lv.method_5704())) {
							return 0;
						} else {
							int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							class_2338.class_2339 lv2 = new class_2338.class_2339();
							lv2.method_10102(lv.field_5987, lv.field_6010, lv.field_6035).method_10100(j, 0, k);
							if (!arg.method_8627(
								lv2.method_10263() - 10, lv2.method_10264() - 10, lv2.method_10260() - 10, lv2.method_10263() + 10, lv2.method_10264() + 10, lv2.method_10260() + 10
							)) {
								return 0;
							} else {
								class_1959 lv3 = arg.method_8310(lv2);
								class_1959.class_1961 lv4 = lv3.method_8688();
								if (lv4 == class_1959.class_1961.field_9365) {
									return 0;
								} else {
									int m = 0;
									int n = (int)Math.ceil((double)arg.method_8404(lv2).method_5457()) + 1;

									for (int o = 0; o < n; o++) {
										m++;
										lv2.method_10099(arg.method_8598(class_2902.class_2903.field_13203, lv2).method_10264());
										if (o == 0) {
											if (!this.method_16575(arg, lv2, random, true)) {
												break;
											}
										} else {
											this.method_16575(arg, lv2, random, false);
										}

										lv2.method_20787(lv2.method_10263() + random.nextInt(5) - random.nextInt(5));
										lv2.method_20788(lv2.method_10260() + random.nextInt(5) - random.nextInt(5));
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

	private boolean method_16575(class_1937 arg, class_2338 arg2, Random random, boolean bl) {
		if (!class_3732.method_20739(class_1299.field_6105, arg, class_3730.field_16527, arg2, random)) {
			return false;
		} else {
			class_3732 lv = class_1299.field_6105.method_5883(arg);
			if (lv != null) {
				if (bl) {
					lv.method_16217(true);
					lv.method_16218();
				}

				lv.method_5814((double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260());
				lv.method_5943(arg, arg.method_8404(arg2), class_3730.field_16527, null, null);
				arg.method_8649(lv);
				return true;
			} else {
				return false;
			}
		}
	}
}
