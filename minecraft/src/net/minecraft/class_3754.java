package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;

public abstract class class_3754<T extends class_2888> extends class_2794<T> {
	private static final float[] field_16649 = class_156.method_654(new float[13824], fs -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					fs[i * 24 * 24 + j * 24 + k] = (float)method_16571(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final class_2680 field_16648 = class_2246.field_10124.method_9564();
	private final int field_16572;
	private final int field_16570;
	private final int field_16580;
	private final int field_16579;
	private final int field_16578;
	protected final class_2919 field_16577;
	private final class_3537 field_16574;
	private final class_3537 field_16581;
	private final class_3537 field_16575;
	private final class_3757 field_16571;
	protected final class_2680 field_16576;
	protected final class_2680 field_16573;

	public class_3754(class_1936 arg, class_1966 arg2, int i, int j, int k, T arg3, boolean bl) {
		super(arg, arg2, arg3);
		this.field_16572 = j;
		this.field_16570 = i;
		this.field_16576 = arg3.method_12569();
		this.field_16573 = arg3.method_12570();
		this.field_16580 = 16 / this.field_16570;
		this.field_16579 = k / this.field_16572;
		this.field_16578 = 16 / this.field_16570;
		this.field_16577 = new class_2919(this.field_12759);
		this.field_16574 = new class_3537(this.field_16577, 16);
		this.field_16581 = new class_3537(this.field_16577, 16);
		this.field_16575 = new class_3537(this.field_16577, 8);
		this.field_16571 = (class_3757)(bl ? new class_3543(this.field_16577, 4) : new class_3537(this.field_16577, 4));
	}

	private double method_16411(int i, int j, int k, double d, double e, double f, double g) {
		double h = 0.0;
		double l = 0.0;
		double m = 0.0;
		double n = 1.0;

		for (int o = 0; o < 16; o++) {
			double p = class_3537.method_16452((double)i * d * n);
			double q = class_3537.method_16452((double)j * e * n);
			double r = class_3537.method_16452((double)k * d * n);
			double s = e * n;
			h += this.field_16574.method_16668(o).method_16447(p, q, r, s, (double)j * s) / n;
			l += this.field_16581.method_16668(o).method_16447(p, q, r, s, (double)j * s) / n;
			if (o < 8) {
				m += this.field_16575
						.method_16668(o)
						.method_16447(
							class_3537.method_16452((double)i * f * n),
							class_3537.method_16452((double)j * g * n),
							class_3537.method_16452((double)k * f * n),
							g * n,
							(double)j * g * n
						)
					/ n;
			}

			n /= 2.0;
		}

		return class_3532.method_15390(h / 512.0, l / 512.0, (m / 10.0 + 1.0) / 2.0);
	}

	protected double[] method_16406(int i, int j) {
		double[] ds = new double[this.field_16579 + 1];
		this.method_16405(ds, i, j);
		return ds;
	}

	protected void method_16413(double[] ds, int i, int j, double d, double e, double f, double g, int k, int l) {
		double[] es = this.method_12090(i, j);
		double h = es[0];
		double m = es[1];
		double n = this.method_16409();
		double o = this.method_16410();

		for (int p = 0; p < this.method_16408(); p++) {
			double q = this.method_16411(i, p, j, d, e, f, g);
			q -= this.method_16404(h, m, p);
			if ((double)p > n) {
				q = class_3532.method_15390(q, (double)l, ((double)p - n) / (double)k);
			} else if ((double)p < o) {
				q = class_3532.method_15390(q, -30.0, (o - (double)p) / (o - 1.0));
			}

			ds[p] = q;
		}
	}

	protected abstract double[] method_12090(int i, int j);

	protected abstract double method_16404(double d, double e, int i);

	protected double method_16409() {
		return (double)(this.method_16408() - 4);
	}

	protected double method_16410() {
		return 0.0;
	}

	@Override
	public int method_16397(int i, int j, class_2902.class_2903 arg) {
		int k = Math.floorDiv(i, this.field_16570);
		int l = Math.floorDiv(j, this.field_16570);
		int m = Math.floorMod(i, this.field_16570);
		int n = Math.floorMod(j, this.field_16570);
		double d = (double)m / (double)this.field_16570;
		double e = (double)n / (double)this.field_16570;
		double[][] ds = new double[][]{this.method_16406(k, l), this.method_16406(k, l + 1), this.method_16406(k + 1, l), this.method_16406(k + 1, l + 1)};
		int o = this.method_16398();

		for (int p = this.field_16579 - 1; p >= 0; p--) {
			double f = ds[0][p];
			double g = ds[1][p];
			double h = ds[2][p];
			double q = ds[3][p];
			double r = ds[0][p + 1];
			double s = ds[1][p + 1];
			double t = ds[2][p + 1];
			double u = ds[3][p + 1];

			for (int v = this.field_16572 - 1; v >= 0; v--) {
				double w = (double)v / (double)this.field_16572;
				double x = class_3532.method_16438(w, d, e, f, r, h, t, g, s, q, u);
				int y = p * this.field_16572 + v;
				if (x > 0.0 || y < o) {
					class_2680 lv;
					if (x > 0.0) {
						lv = this.field_16576;
					} else {
						lv = this.field_16573;
					}

					if (arg.method_16402().test(lv)) {
						return y + 1;
					}
				}
			}
		}

		return 0;
	}

	protected abstract void method_16405(double[] ds, int i, int j);

	public int method_16408() {
		return this.field_16579 + 1;
	}

	@Override
	public void method_12110(class_2791 arg) {
		class_1923 lv = arg.method_12004();
		int i = lv.field_9181;
		int j = lv.field_9180;
		class_2919 lv2 = new class_2919();
		lv2.method_12659(i, j);
		class_1923 lv3 = arg.method_12004();
		int k = lv3.method_8326();
		int l = lv3.method_8328();
		double d = 0.0625;
		class_1959[] lvs = arg.method_12036();

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				int o = k + m;
				int p = l + n;
				int q = arg.method_12005(class_2902.class_2903.field_13194, m, n) + 1;
				double e = this.field_16571.method_16454((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625);
				lvs[n * 16 + m]
					.method_8703(lv2, arg, o, p, q, e, this.method_12109().method_12569(), this.method_12109().method_12570(), 63, this.field_12760.method_8412());
			}
		}

		this.method_16412(arg, lv2);
	}

	protected void method_16412(class_2791 arg, Random random) {
		class_2338.class_2339 lv = new class_2338.class_2339();
		int i = arg.method_12004().method_8326();
		int j = arg.method_12004().method_8328();
		T lv2 = this.method_12109();
		int k = lv2.method_16401();
		int l = lv2.method_16400();

		for (class_2338 lv3 : class_2338.method_10094(i, 0, j, i + 15, 0, j + 15)) {
			if (l > 0) {
				for (int m = l; m >= l - 4; m--) {
					if (m >= l - random.nextInt(5)) {
						arg.method_12010(lv.method_10103(lv3.method_10263(), m, lv3.method_10260()), class_2246.field_9987.method_9564(), false);
					}
				}
			}

			if (k < 256) {
				for (int mx = k + 4; mx >= k; mx--) {
					if (mx <= k + random.nextInt(5)) {
						arg.method_12010(lv.method_10103(lv3.method_10263(), mx, lv3.method_10260()), class_2246.field_9987.method_9564(), false);
					}
				}
			}
		}
	}

	@Override
	public void method_12088(class_1936 arg, class_2791 arg2) {
		int i = this.method_16398();
		ObjectList<class_3790> objectList = new ObjectArrayList<>(10);
		ObjectList<class_3780> objectList2 = new ObjectArrayList<>(32);
		class_1923 lv = arg2.method_12004();
		int j = lv.field_9181;
		int k = lv.field_9180;
		int l = j << 4;
		int m = k << 4;

		for (class_3195<?> lv2 : class_3031.field_16654) {
			String string = lv2.method_14019();
			LongIterator longIterator = arg2.method_12180(string).iterator();

			while (longIterator.hasNext()) {
				long n = longIterator.nextLong();
				class_1923 lv3 = new class_1923(n);
				class_2791 lv4 = arg.method_8392(lv3.field_9181, lv3.field_9180);
				class_3449 lv5 = lv4.method_12181(string);
				if (lv5 != null && lv5.method_16657()) {
					for (class_3443 lv6 : lv5.method_14963()) {
						if (lv6.method_16654(lv, 12) && lv6 instanceof class_3790) {
							class_3790 lv7 = (class_3790)lv6;
							class_3785.class_3786 lv8 = lv7.method_16644().method_16624();
							if (lv8 == class_3785.class_3786.field_16687) {
								objectList.add(lv7);
							}

							for (class_3780 lv9 : lv7.method_16645()) {
								int o = lv9.method_16610();
								int p = lv9.method_16609();
								if (o > l - 12 && p > m - 12 && o < l + 15 + 12 && p < m + 15 + 12) {
									objectList2.add(lv9);
								}
							}
						}
					}
				}
			}
		}

		double[][][] ds = new double[2][this.field_16578 + 1][this.field_16579 + 1];

		for (int q = 0; q < this.field_16578 + 1; q++) {
			ds[0][q] = new double[this.field_16579 + 1];
			this.method_16405(ds[0][q], j * this.field_16580, k * this.field_16578 + q);
			ds[1][q] = new double[this.field_16579 + 1];
		}

		class_2839 lv10 = (class_2839)arg2;
		class_2902 lv11 = lv10.method_12032(class_2902.class_2903.field_13195);
		class_2902 lv12 = lv10.method_12032(class_2902.class_2903.field_13194);
		class_2338.class_2339 lv13 = new class_2338.class_2339();
		ObjectListIterator<class_3790> objectListIterator = objectList.iterator();
		ObjectListIterator<class_3780> objectListIterator2 = objectList2.iterator();

		for (int r = 0; r < this.field_16580; r++) {
			for (int s = 0; s < this.field_16578 + 1; s++) {
				this.method_16405(ds[1][s], j * this.field_16580 + r + 1, k * this.field_16578 + s);
			}

			for (int s = 0; s < this.field_16578; s++) {
				class_2826 lv14 = lv10.method_16679(15);
				lv14.method_16676();

				for (int t = this.field_16579 - 1; t >= 0; t--) {
					double d = ds[0][s][t];
					double e = ds[0][s + 1][t];
					double f = ds[1][s][t];
					double g = ds[1][s + 1][t];
					double h = ds[0][s][t + 1];
					double u = ds[0][s + 1][t + 1];
					double v = ds[1][s][t + 1];
					double w = ds[1][s + 1][t + 1];

					for (int x = this.field_16572 - 1; x >= 0; x--) {
						int y = t * this.field_16572 + x;
						int z = y & 15;
						int aa = y >> 4;
						if (lv14.method_12259() >> 4 != aa) {
							lv14.method_16677();
							lv14 = lv10.method_16679(aa);
							lv14.method_16676();
						}

						double ab = (double)x / (double)this.field_16572;
						double ac = class_3532.method_16436(ab, d, h);
						double ad = class_3532.method_16436(ab, f, v);
						double ae = class_3532.method_16436(ab, e, u);
						double af = class_3532.method_16436(ab, g, w);

						for (int ag = 0; ag < this.field_16570; ag++) {
							int ah = l + r * this.field_16570 + ag;
							int ai = ah & 15;
							double aj = (double)ag / (double)this.field_16570;
							double ak = class_3532.method_16436(aj, ac, ad);
							double al = class_3532.method_16436(aj, ae, af);

							for (int am = 0; am < this.field_16570; am++) {
								int an = m + s * this.field_16570 + am;
								int ao = an & 15;
								double ap = (double)am / (double)this.field_16570;
								double aq = class_3532.method_16436(ap, ak, al);
								double ar = class_3532.method_15350(aq / 200.0, -1.0, 1.0);
								ar = ar / 2.0 - ar * ar * ar / 24.0;

								while (objectListIterator.hasNext()) {
									class_3790 lv15 = (class_3790)objectListIterator.next();
									class_3341 lv16 = lv15.method_14935();
									int as = Math.max(0, Math.max(lv16.field_14381 - ah, ah - lv16.field_14378));
									int at = y - (lv16.field_14380 + lv15.method_16646());
									int au = Math.max(0, Math.max(lv16.field_14379 - an, an - lv16.field_14376));
									ar += method_16572(as, at, au) * 0.8;
								}

								objectListIterator.back(objectList.size());

								while (objectListIterator2.hasNext()) {
									class_3780 lv17 = (class_3780)objectListIterator2.next();
									int av = ah - lv17.method_16610();
									int as = y - lv17.method_16611();
									int at = an - lv17.method_16609();
									ar += method_16572(av, as, at) * 0.4;
								}

								objectListIterator2.back(objectList2.size());
								class_2680 lv18;
								if (ar > 0.0) {
									lv18 = this.field_16576;
								} else if (y < i) {
									lv18 = this.field_16573;
								} else {
									lv18 = field_16648;
								}

								if (lv18 != field_16648) {
									if (lv18.method_11630() != 0) {
										lv13.method_10103(ah, y, an);
										lv10.method_12315(lv13);
									}

									lv14.method_12256(ai, z, ao, lv18, false);
									lv11.method_12597(ai, y, ao, lv18);
									lv12.method_12597(ai, y, ao, lv18);
								}
							}
						}
					}
				}

				lv14.method_16677();
			}

			double[][] es = ds[0];
			ds[0] = ds[1];
			ds[1] = es;
		}
	}

	private static double method_16572(int i, int j, int k) {
		int l = i + 12;
		int m = j + 12;
		int n = k + 12;
		if (l < 0 || l >= 24) {
			return 0.0;
		} else if (m < 0 || m >= 24) {
			return 0.0;
		} else {
			return n >= 0 && n < 24 ? (double)field_16649[n * 24 * 24 + l * 24 + m] : 0.0;
		}
	}

	private static double method_16571(int i, int j, int k) {
		double d = (double)(i * i + k * k);
		double e = (double)j + 0.5;
		double f = e * e;
		double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
		double h = -e * class_3532.method_15345(f / 2.0 + d / 2.0) / 2.0;
		return h * g;
	}
}
