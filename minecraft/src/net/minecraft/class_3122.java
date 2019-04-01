package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class class_3122 extends class_3031<class_3124> {
	public class_3122(Function<Dynamic<?>, ? extends class_3124> function) {
		super(function);
	}

	public boolean method_13628(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3124 arg4) {
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)arg4.field_13723 / 8.0F;
		int i = class_3532.method_15386(((float)arg4.field_13723 / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)((float)arg3.method_10263() + class_3532.method_15374(f) * g);
		double e = (double)((float)arg3.method_10263() - class_3532.method_15374(f) * g);
		double h = (double)((float)arg3.method_10260() + class_3532.method_15362(f) * g);
		double j = (double)((float)arg3.method_10260() - class_3532.method_15362(f) * g);
		int k = 2;
		double l = (double)(arg3.method_10264() + random.nextInt(3) - 2);
		double m = (double)(arg3.method_10264() + random.nextInt(3) - 2);
		int n = arg3.method_10263() - class_3532.method_15386(g) - i;
		int o = arg3.method_10264() - 2 - i;
		int p = arg3.method_10260() - class_3532.method_15386(g) - i;
		int q = 2 * (class_3532.method_15386(g) + i);
		int r = 2 * (2 + i);

		for (int s = n; s <= n + q; s++) {
			for (int t = p; t <= p + q; t++) {
				if (o <= arg.method_8589(class_2902.class_2903.field_13195, s, t)) {
					return this.method_13629(arg, random, arg4, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean method_13629(
		class_1936 arg, Random random, class_3124 arg2, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n
	) {
		int o = 0;
		BitSet bitSet = new BitSet(m * n * m);
		class_2338.class_2339 lv = new class_2338.class_2339();
		double[] ds = new double[arg2.field_13723 * 4];

		for (int p = 0; p < arg2.field_13723; p++) {
			float q = (float)p / (float)arg2.field_13723;
			double r = class_3532.method_16436((double)q, d, e);
			double s = class_3532.method_16436((double)q, h, i);
			double t = class_3532.method_16436((double)q, f, g);
			double u = random.nextDouble() * (double)arg2.field_13723 / 16.0;
			double v = ((double)(class_3532.method_15374((float) Math.PI * q) + 1.0F) * u + 1.0) / 2.0;
			ds[p * 4 + 0] = r;
			ds[p * 4 + 1] = s;
			ds[p * 4 + 2] = t;
			ds[p * 4 + 3] = v;
		}

		for (int p = 0; p < arg2.field_13723 - 1; p++) {
			if (!(ds[p * 4 + 3] <= 0.0)) {
				for (int w = p + 1; w < arg2.field_13723; w++) {
					if (!(ds[w * 4 + 3] <= 0.0)) {
						double r = ds[p * 4 + 0] - ds[w * 4 + 0];
						double s = ds[p * 4 + 1] - ds[w * 4 + 1];
						double t = ds[p * 4 + 2] - ds[w * 4 + 2];
						double u = ds[p * 4 + 3] - ds[w * 4 + 3];
						if (u * u > r * r + s * s + t * t) {
							if (u > 0.0) {
								ds[w * 4 + 3] = -1.0;
							} else {
								ds[p * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		for (int px = 0; px < arg2.field_13723; px++) {
			double x = ds[px * 4 + 3];
			if (!(x < 0.0)) {
				double y = ds[px * 4 + 0];
				double z = ds[px * 4 + 1];
				double aa = ds[px * 4 + 2];
				int ab = Math.max(class_3532.method_15357(y - x), j);
				int ac = Math.max(class_3532.method_15357(z - x), k);
				int ad = Math.max(class_3532.method_15357(aa - x), l);
				int ae = Math.max(class_3532.method_15357(y + x), ab);
				int af = Math.max(class_3532.method_15357(z + x), ac);
				int ag = Math.max(class_3532.method_15357(aa + x), ad);

				for (int ah = ab; ah <= ae; ah++) {
					double ai = ((double)ah + 0.5 - y) / x;
					if (ai * ai < 1.0) {
						for (int aj = ac; aj <= af; aj++) {
							double ak = ((double)aj + 0.5 - z) / x;
							if (ai * ai + ak * ak < 1.0) {
								for (int al = ad; al <= ag; al++) {
									double am = ((double)al + 0.5 - aa) / x;
									if (ai * ai + ak * ak + am * am < 1.0) {
										int an = ah - j + (aj - k) * m + (al - l) * m * n;
										if (!bitSet.get(an)) {
											bitSet.set(an);
											lv.method_10103(ah, aj, al);
											if (arg2.field_13725.method_13636().test(arg.method_8320(lv))) {
												arg.method_8652(lv, arg2.field_13724, 2);
												o++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return o > 0;
	}
}
