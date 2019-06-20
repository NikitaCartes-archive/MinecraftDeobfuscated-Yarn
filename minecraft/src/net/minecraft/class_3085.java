package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3085 extends class_3031<class_3087> {
	private static final class_2680 field_13668 = class_2246.field_10543.method_9564();

	public class_3085(Function<Dynamic<?>, ? extends class_3087> function) {
		super(function);
	}

	public boolean method_13471(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3087 arg4) {
		while (arg3.method_10264() > 5 && arg.method_8623(arg3)) {
			arg3 = arg3.method_10074();
		}

		if (arg3.method_10264() <= 4) {
			return false;
		} else {
			arg3 = arg3.method_10087(4);
			class_1923 lv = new class_1923(arg3);
			if (!arg.method_16956(lv.field_9181, lv.field_9180, class_2806.field_16422).method_12180(class_3031.field_13587.method_14019()).isEmpty()) {
				return false;
			} else {
				boolean[] bls = new boolean[2048];
				int i = random.nextInt(4) + 4;

				for (int j = 0; j < i; j++) {
					double d = random.nextDouble() * 6.0 + 3.0;
					double e = random.nextDouble() * 4.0 + 2.0;
					double f = random.nextDouble() * 6.0 + 3.0;
					double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
					double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
					double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;

					for (int l = 1; l < 15; l++) {
						for (int m = 1; m < 15; m++) {
							for (int n = 1; n < 7; n++) {
								double o = ((double)l - g) / (d / 2.0);
								double p = ((double)n - h) / (e / 2.0);
								double q = ((double)m - k) / (f / 2.0);
								double r = o * o + p * p + q * q;
								if (r < 1.0) {
									bls[(l * 16 + m) * 8 + n] = true;
								}
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int t = 0; t < 8; t++) {
							boolean bl = !bls[(j * 16 + s) * 8 + t]
								&& (
									j < 15 && bls[((j + 1) * 16 + s) * 8 + t]
										|| j > 0 && bls[((j - 1) * 16 + s) * 8 + t]
										|| s < 15 && bls[(j * 16 + s + 1) * 8 + t]
										|| s > 0 && bls[(j * 16 + (s - 1)) * 8 + t]
										|| t < 7 && bls[(j * 16 + s) * 8 + t + 1]
										|| t > 0 && bls[(j * 16 + s) * 8 + (t - 1)]
								);
							if (bl) {
								class_3614 lv2 = arg.method_8320(arg3.method_10069(j, t, s)).method_11620();
								if (t >= 4 && lv2.method_15797()) {
									return false;
								}

								if (t < 4 && !lv2.method_15799() && arg.method_8320(arg3.method_10069(j, t, s)) != arg4.field_13670) {
									return false;
								}
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int tx = 0; tx < 8; tx++) {
							if (bls[(j * 16 + s) * 8 + tx]) {
								arg.method_8652(arg3.method_10069(j, tx, s), tx >= 4 ? field_13668 : arg4.field_13670, 2);
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int txx = 4; txx < 8; txx++) {
							if (bls[(j * 16 + s) * 8 + txx]) {
								class_2338 lv3 = arg3.method_10069(j, txx - 1, s);
								if (class_2248.method_9519(arg.method_8320(lv3).method_11614()) && arg.method_8314(class_1944.field_9284, arg3.method_10069(j, txx, s)) > 0) {
									class_1959 lv4 = arg.method_8310(lv3);
									if (lv4.method_8722().method_15337().method_11614() == class_2246.field_10402) {
										arg.method_8652(lv3, class_2246.field_10402.method_9564(), 2);
									} else {
										arg.method_8652(lv3, class_2246.field_10219.method_9564(), 2);
									}
								}
							}
						}
					}
				}

				if (arg4.field_13670.method_11620() == class_3614.field_15922) {
					for (int j = 0; j < 16; j++) {
						for (int s = 0; s < 16; s++) {
							for (int txxx = 0; txxx < 8; txxx++) {
								boolean bl = !bls[(j * 16 + s) * 8 + txxx]
									&& (
										j < 15 && bls[((j + 1) * 16 + s) * 8 + txxx]
											|| j > 0 && bls[((j - 1) * 16 + s) * 8 + txxx]
											|| s < 15 && bls[(j * 16 + s + 1) * 8 + txxx]
											|| s > 0 && bls[(j * 16 + (s - 1)) * 8 + txxx]
											|| txxx < 7 && bls[(j * 16 + s) * 8 + txxx + 1]
											|| txxx > 0 && bls[(j * 16 + s) * 8 + (txxx - 1)]
									);
								if (bl && (txxx < 4 || random.nextInt(2) != 0) && arg.method_8320(arg3.method_10069(j, txxx, s)).method_11620().method_15799()) {
									arg.method_8652(arg3.method_10069(j, txxx, s), class_2246.field_10340.method_9564(), 2);
								}
							}
						}
					}
				}

				if (arg4.field_13670.method_11620() == class_3614.field_15920) {
					for (int j = 0; j < 16; j++) {
						for (int s = 0; s < 16; s++) {
							int txxxx = 4;
							class_2338 lv3 = arg3.method_10069(j, 4, s);
							if (arg.method_8310(lv3).method_8685(arg, lv3, false)) {
								arg.method_8652(lv3, class_2246.field_10295.method_9564(), 2);
							}
						}
					}
				}

				return true;
			}
		}
	}
}
