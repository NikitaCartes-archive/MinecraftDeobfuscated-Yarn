package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3074 extends class_3031<class_3067> {
	public class_3074(Function<Dynamic<?>, ? extends class_3067> function) {
		super(function);
	}

	public boolean method_13423(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3067 arg4) {
		arg3 = new class_2338(arg3.method_10263(), arg.method_8615(), arg3.method_10260());
		boolean bl = random.nextDouble() > 0.7;
		class_2680 lv = arg4.field_13664;
		double d = random.nextDouble() * 2.0 * Math.PI;
		int i = 11 - random.nextInt(5);
		int j = 3 + random.nextInt(3);
		boolean bl2 = random.nextDouble() > 0.7;
		int k = 11;
		int l = bl2 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
		if (!bl2 && random.nextDouble() > 0.9) {
			l += random.nextInt(19) + 7;
		}

		int m = Math.min(l + random.nextInt(11), 18);
		int n = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
		int o = bl2 ? i : 11;

		for (int p = -o; p < o; p++) {
			for (int q = -o; q < o; q++) {
				for (int r = 0; r < l; r++) {
					int s = bl2 ? this.method_13417(r, l, n) : this.method_13419(random, r, l, n);
					if (bl2 || p < s) {
						this.method_13426(arg, random, arg3, l, p, r, q, s, o, bl2, j, d, bl, lv);
					}
				}
			}
		}

		this.method_13418(arg, arg3, n, l, bl2, i);

		for (int p = -o; p < o; p++) {
			for (int q = -o; q < o; q++) {
				for (int rx = -1; rx > -m; rx--) {
					int s = bl2 ? class_3532.method_15386((float)o * (1.0F - (float)Math.pow((double)rx, 2.0) / ((float)m * 8.0F))) : o;
					int t = this.method_13427(random, -rx, m, n);
					if (p < t) {
						this.method_13426(arg, random, arg3, m, p, rx, q, t, s, bl2, j, d, bl, lv);
					}
				}
			}
		}

		boolean bl3 = bl2 ? random.nextDouble() > 0.1 : random.nextDouble() > 0.7;
		if (bl3) {
			this.method_13428(random, arg, n, l, arg3, bl2, i, d, j);
		}

		return true;
	}

	private void method_13428(Random random, class_1936 arg, int i, int j, class_2338 arg2, boolean bl, int k, double d, int l) {
		int m = random.nextBoolean() ? -1 : 1;
		int n = random.nextBoolean() ? -1 : 1;
		int o = random.nextInt(Math.max(i / 2 - 2, 1));
		if (random.nextBoolean()) {
			o = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
		}

		int p = random.nextInt(Math.max(i / 2 - 2, 1));
		if (random.nextBoolean()) {
			p = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
		}

		if (bl) {
			o = p = random.nextInt(Math.max(k - 5, 1));
		}

		class_2338 lv = new class_2338(0, 0, 0).method_10069(m * o, 0, n * p);
		double e = bl ? d + (Math.PI / 2) : random.nextDouble() * 2.0 * Math.PI;

		for (int q = 0; q < j - 3; q++) {
			int r = this.method_13419(random, q, j, i);
			this.method_13415(r, q, arg2, arg, false, e, lv, k, l);
		}

		for (int q = -1; q > -j + random.nextInt(5); q--) {
			int r = this.method_13427(random, -q, j, i);
			this.method_13415(r, q, arg2, arg, true, e, lv, k, l);
		}
	}

	private void method_13415(int i, int j, class_2338 arg, class_1936 arg2, boolean bl, double d, class_2338 arg3, int k, int l) {
		int m = i + 1 + k / 3;
		int n = Math.min(i - 3, 3) + l / 2 - 1;

		for (int o = -m; o < m; o++) {
			for (int p = -m; p < m; p++) {
				double e = this.method_13424(o, p, arg3, m, n, d);
				if (e < 0.0) {
					class_2338 lv = arg.method_10069(o, j, p);
					class_2248 lv2 = arg2.method_8320(lv).method_11614();
					if (this.method_13420(lv2) || lv2 == class_2246.field_10491) {
						if (bl) {
							this.method_13153(arg2, lv, class_2246.field_10382.method_9564());
						} else {
							this.method_13153(arg2, lv, class_2246.field_10124.method_9564());
							this.method_13422(arg2, lv);
						}
					}
				}
			}
		}
	}

	private void method_13422(class_1936 arg, class_2338 arg2) {
		if (arg.method_8320(arg2.method_10084()).method_11614() == class_2246.field_10477) {
			this.method_13153(arg, arg2.method_10084(), class_2246.field_10124.method_9564());
		}
	}

	private void method_13426(
		class_1936 arg, Random random, class_2338 arg2, int i, int j, int k, int l, int m, int n, boolean bl, int o, double d, boolean bl2, class_2680 arg3
	) {
		class_2338 lv = new class_2338(0, 0, 0);
		double e = bl ? this.method_13424(j, l, lv, n, this.method_13416(k, i, o), d) : this.method_13421(j, l, lv, m, random);
		if (e < 0.0) {
			class_2338 lv2 = arg2.method_10069(j, k, l);
			double f = bl ? -0.5 : (double)(-6 - random.nextInt(3));
			if (e > f && random.nextDouble() > 0.9) {
				return;
			}

			this.method_13425(lv2, arg, random, i - k, i, bl, bl2, arg3);
		}
	}

	private void method_13425(class_2338 arg, class_1936 arg2, Random random, int i, int j, boolean bl, boolean bl2, class_2680 arg3) {
		class_2680 lv = arg2.method_8320(arg);
		class_2248 lv2 = lv.method_11614();
		if (lv.method_11620() == class_3614.field_15959 || lv2 == class_2246.field_10491 || lv2 == class_2246.field_10295 || lv2 == class_2246.field_10382) {
			boolean bl3 = !bl || random.nextDouble() > 0.05;
			int k = bl ? 3 : 2;
			if (bl2 && lv2 != class_2246.field_10382 && (double)i <= (double)random.nextInt(Math.max(1, j / k)) + (double)j * 0.6 && bl3) {
				this.method_13153(arg2, arg, class_2246.field_10491.method_9564());
			} else {
				this.method_13153(arg2, arg, arg3);
			}
		}
	}

	private int method_13416(int i, int j, int k) {
		int l = k;
		if (i > 0 && j - i <= 3) {
			l = k - (4 - (j - i));
		}

		return l;
	}

	private double method_13421(int i, int j, class_2338 arg, int k, Random random) {
		float f = 10.0F * class_3532.method_15363(random.nextFloat(), 0.2F, 0.8F) / (float)k;
		return (double)f + Math.pow((double)(i - arg.method_10263()), 2.0) + Math.pow((double)(j - arg.method_10260()), 2.0) - Math.pow((double)k, 2.0);
	}

	private double method_13424(int i, int j, class_2338 arg, int k, int l, double d) {
		return Math.pow(((double)(i - arg.method_10263()) * Math.cos(d) - (double)(j - arg.method_10260()) * Math.sin(d)) / (double)k, 2.0)
			+ Math.pow(((double)(i - arg.method_10263()) * Math.sin(d) + (double)(j - arg.method_10260()) * Math.cos(d)) / (double)l, 2.0)
			- 1.0;
	}

	private int method_13419(Random random, int i, int j, int k) {
		float f = 3.5F - random.nextFloat();
		float g = (1.0F - (float)Math.pow((double)i, 2.0) / ((float)j * f)) * (float)k;
		if (j > 15 + random.nextInt(5)) {
			int l = i < 3 + random.nextInt(6) ? i / 2 : i;
			g = (1.0F - (float)l / ((float)j * f * 0.4F)) * (float)k;
		}

		return class_3532.method_15386(g / 2.0F);
	}

	private int method_13417(int i, int j, int k) {
		float f = 1.0F;
		float g = (1.0F - (float)Math.pow((double)i, 2.0) / ((float)j * 1.0F)) * (float)k;
		return class_3532.method_15386(g / 2.0F);
	}

	private int method_13427(Random random, int i, int j, int k) {
		float f = 1.0F + random.nextFloat() / 2.0F;
		float g = (1.0F - (float)i / ((float)j * f)) * (float)k;
		return class_3532.method_15386(g / 2.0F);
	}

	private boolean method_13420(class_2248 arg) {
		return arg == class_2246.field_10225 || arg == class_2246.field_10491 || arg == class_2246.field_10384;
	}

	private boolean method_13414(class_1922 arg, class_2338 arg2) {
		return arg.method_8320(arg2.method_10074()).method_11620() == class_3614.field_15959;
	}

	private void method_13418(class_1936 arg, class_2338 arg2, int i, int j, boolean bl, int k) {
		int l = bl ? k : i / 2;

		for (int m = -l; m <= l; m++) {
			for (int n = -l; n <= l; n++) {
				for (int o = 0; o <= j; o++) {
					class_2338 lv = arg2.method_10069(m, o, n);
					class_2248 lv2 = arg.method_8320(lv).method_11614();
					if (this.method_13420(lv2) || lv2 == class_2246.field_10477) {
						if (this.method_13414(arg, lv)) {
							this.method_13153(arg, lv, class_2246.field_10124.method_9564());
							this.method_13153(arg, lv.method_10084(), class_2246.field_10124.method_9564());
						} else if (this.method_13420(lv2)) {
							class_2248[] lvs = new class_2248[]{
								arg.method_8320(lv.method_10067()).method_11614(),
								arg.method_8320(lv.method_10078()).method_11614(),
								arg.method_8320(lv.method_10095()).method_11614(),
								arg.method_8320(lv.method_10072()).method_11614()
							};
							int p = 0;

							for (class_2248 lv3 : lvs) {
								if (!this.method_13420(lv3)) {
									p++;
								}
							}

							if (p >= 3) {
								this.method_13153(arg, lv, class_2246.field_10124.method_9564());
							}
						}
					}
				}
			}
		}
	}
}
