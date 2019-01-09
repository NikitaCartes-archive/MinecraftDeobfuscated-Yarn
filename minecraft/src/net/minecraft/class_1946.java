package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;

public class class_1946 {
	private static final class_2423 field_9288 = (class_2423)class_2246.field_10316;
	private final class_3218 field_9286;
	private final Random field_9289;
	private final Long2ObjectMap<class_1946.class_1947> field_9287 = new Long2ObjectOpenHashMap<>(4096);

	public class_1946(class_3218 arg) {
		this.field_9286 = arg;
		this.field_9289 = new Random(arg.method_8412());
	}

	public void method_8655(class_1297 arg, float f) {
		if (this.field_9286.field_9247.method_12460() != class_2874.field_13078) {
			if (!this.method_8653(arg, f)) {
				this.method_8654(arg);
				this.method_8653(arg, f);
			}
		} else {
			int i = class_3532.method_15357(arg.field_5987);
			int j = class_3532.method_15357(arg.field_6010) - 1;
			int k = class_3532.method_15357(arg.field_6035);
			int l = 1;
			int m = 0;

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					for (int p = -1; p < 3; p++) {
						int q = i + o * 1 + n * 0;
						int r = j + p;
						int s = k + o * 0 - n * 1;
						boolean bl = p < 0;
						this.field_9286.method_8501(new class_2338(q, r, s), bl ? class_2246.field_10540.method_9564() : class_2246.field_10124.method_9564());
					}
				}
			}

			arg.method_5808((double)i, (double)j, (double)k, arg.field_6031, 0.0F);
			arg.field_5967 = 0.0;
			arg.field_5984 = 0.0;
			arg.field_6006 = 0.0;
		}
	}

	public boolean method_8653(class_1297 arg, float f) {
		int i = 128;
		double d = -1.0;
		int j = class_3532.method_15357(arg.field_5987);
		int k = class_3532.method_15357(arg.field_6035);
		boolean bl = true;
		class_2338 lv = class_2338.field_10980;
		long l = class_1923.method_8331(j, k);
		if (this.field_9287.containsKey(l)) {
			class_1946.class_1947 lv2 = this.field_9287.get(l);
			d = 0.0;
			lv = lv2;
			lv2.field_9290 = this.field_9286.method_8510();
			bl = false;
		} else {
			class_2338 lv3 = new class_2338(arg);

			for (int m = -128; m <= 128; m++) {
				for (int n = -128; n <= 128; n++) {
					class_2338 lv4 = lv3.method_10069(m, this.field_9286.method_8456() - 1 - lv3.method_10264(), n);

					while (lv4.method_10264() >= 0) {
						class_2338 lv5 = lv4.method_10074();
						if (this.field_9286.method_8320(lv4).method_11614() == field_9288) {
							for (lv5 = lv4.method_10074(); this.field_9286.method_8320(lv5).method_11614() == field_9288; lv5 = lv5.method_10074()) {
								lv4 = lv5;
							}

							double e = lv4.method_10262(lv3);
							if (d < 0.0 || e < d) {
								d = e;
								lv = lv4;
							}
						}

						lv4 = lv5;
					}
				}
			}
		}

		if (d >= 0.0) {
			if (bl) {
				this.field_9287.put(l, new class_1946.class_1947(lv, this.field_9286.method_8510()));
			}

			double g = (double)lv.method_10263() + 0.5;
			double h = (double)lv.method_10260() + 0.5;
			class_2700.class_2702 lv6 = field_9288.method_10350(this.field_9286, lv);
			boolean bl2 = lv6.method_11719().method_10170().method_10171() == class_2350.class_2352.field_11060;
			double o = lv6.method_11719().method_10166() == class_2350.class_2351.field_11048
				? (double)lv6.method_11715().method_10260()
				: (double)lv6.method_11715().method_10263();
			double p = (double)(lv6.method_11715().method_10264() + 1) - arg.method_5656().field_1351 * (double)lv6.method_11720();
			if (bl2) {
				o++;
			}

			if (lv6.method_11719().method_10166() == class_2350.class_2351.field_11048) {
				h = o + (1.0 - arg.method_5656().field_1352) * (double)lv6.method_11718() * (double)lv6.method_11719().method_10170().method_10171().method_10181();
			} else {
				g = o + (1.0 - arg.method_5656().field_1352) * (double)lv6.method_11718() * (double)lv6.method_11719().method_10170().method_10171().method_10181();
			}

			float q = 0.0F;
			float r = 0.0F;
			float s = 0.0F;
			float t = 0.0F;
			if (lv6.method_11719().method_10153() == arg.method_5843()) {
				q = 1.0F;
				r = 1.0F;
			} else if (lv6.method_11719().method_10153() == arg.method_5843().method_10153()) {
				q = -1.0F;
				r = -1.0F;
			} else if (lv6.method_11719().method_10153() == arg.method_5843().method_10170()) {
				s = 1.0F;
				t = -1.0F;
			} else {
				s = -1.0F;
				t = 1.0F;
			}

			double u = arg.field_5967;
			double v = arg.field_6006;
			arg.field_5967 = u * (double)q + v * (double)t;
			arg.field_6006 = u * (double)s + v * (double)r;
			arg.field_6031 = f - (float)(arg.method_5843().method_10153().method_10161() * 90) + (float)(lv6.method_11719().method_10161() * 90);
			if (arg instanceof class_3222) {
				((class_3222)arg).field_13987.method_14363(g, p, h, arg.field_6031, arg.field_5965);
				((class_3222)arg).field_13987.method_14372();
			} else {
				arg.method_5808(g, p, h, arg.field_6031, arg.field_5965);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean method_8654(class_1297 arg) {
		int i = 16;
		double d = -1.0;
		int j = class_3532.method_15357(arg.field_5987);
		int k = class_3532.method_15357(arg.field_6010);
		int l = class_3532.method_15357(arg.field_6035);
		int m = j;
		int n = k;
		int o = l;
		int p = 0;
		int q = this.field_9289.nextInt(4);
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int r = j - 16; r <= j + 16; r++) {
			double e = (double)r + 0.5 - arg.field_5987;

			for (int s = l - 16; s <= l + 16; s++) {
				double f = (double)s + 0.5 - arg.field_6035;

				label279:
				for (int t = this.field_9286.method_8456() - 1; t >= 0; t--) {
					if (this.field_9286.method_8623(lv.method_10103(r, t, s))) {
						while (t > 0 && this.field_9286.method_8623(lv.method_10103(r, t - 1, s))) {
							t--;
						}

						for (int u = q; u < q + 4; u++) {
							int v = u % 2;
							int w = 1 - v;
							if (u % 4 >= 2) {
								v = -v;
								w = -w;
							}

							for (int x = 0; x < 3; x++) {
								for (int y = 0; y < 4; y++) {
									for (int z = -1; z < 4; z++) {
										int aa = r + (y - 1) * v + x * w;
										int ab = t + z;
										int ac = s + (y - 1) * w - x * v;
										lv.method_10103(aa, ab, ac);
										if (z < 0 && !this.field_9286.method_8320(lv).method_11620().method_15799() || z >= 0 && !this.field_9286.method_8623(lv)) {
											continue label279;
										}
									}
								}
							}

							double g = (double)t + 0.5 - arg.field_6010;
							double h = e * e + g * g + f * f;
							if (d < 0.0 || h < d) {
								d = h;
								m = r;
								n = t;
								o = s;
								p = u % 4;
							}
						}
					}
				}
			}
		}

		if (d < 0.0) {
			for (int r = j - 16; r <= j + 16; r++) {
				double e = (double)r + 0.5 - arg.field_5987;

				for (int s = l - 16; s <= l + 16; s++) {
					double f = (double)s + 0.5 - arg.field_6035;

					label216:
					for (int tx = this.field_9286.method_8456() - 1; tx >= 0; tx--) {
						if (this.field_9286.method_8623(lv.method_10103(r, tx, s))) {
							while (tx > 0 && this.field_9286.method_8623(lv.method_10103(r, tx - 1, s))) {
								tx--;
							}

							for (int u = q; u < q + 2; u++) {
								int vx = u % 2;
								int wx = 1 - vx;

								for (int x = 0; x < 4; x++) {
									for (int y = -1; y < 4; y++) {
										int zx = r + (x - 1) * vx;
										int aa = tx + y;
										int ab = s + (x - 1) * wx;
										lv.method_10103(zx, aa, ab);
										if (y < 0 && !this.field_9286.method_8320(lv).method_11620().method_15799() || y >= 0 && !this.field_9286.method_8623(lv)) {
											continue label216;
										}
									}
								}

								double g = (double)tx + 0.5 - arg.field_6010;
								double h = e * e + g * g + f * f;
								if (d < 0.0 || h < d) {
									d = h;
									m = r;
									n = tx;
									o = s;
									p = u % 2;
								}
							}
						}
					}
				}
			}
		}

		int ad = m;
		int ae = n;
		int s = o;
		int af = p % 2;
		int ag = 1 - af;
		if (p % 4 >= 2) {
			af = -af;
			ag = -ag;
		}

		if (d < 0.0) {
			n = class_3532.method_15340(n, 70, this.field_9286.method_8456() - 10);
			ae = n;

			for (int txx = -1; txx <= 1; txx++) {
				for (int u = 1; u < 3; u++) {
					for (int vx = -1; vx < 3; vx++) {
						int wx = ad + (u - 1) * af + txx * ag;
						int x = ae + vx;
						int yx = s + (u - 1) * ag - txx * af;
						boolean bl = vx < 0;
						lv.method_10103(wx, x, yx);
						this.field_9286.method_8501(lv, bl ? class_2246.field_10540.method_9564() : class_2246.field_10124.method_9564());
					}
				}
			}
		}

		for (int txx = -1; txx < 3; txx++) {
			for (int u = -1; u < 4; u++) {
				if (txx == -1 || txx == 2 || u == -1 || u == 3) {
					lv.method_10103(ad + txx * af, ae + u, s + txx * ag);
					this.field_9286.method_8652(lv, class_2246.field_10540.method_9564(), 3);
				}
			}
		}

		class_2680 lv2 = field_9288.method_9564()
			.method_11657(class_2423.field_11310, af == 0 ? class_2350.class_2351.field_11051 : class_2350.class_2351.field_11048);

		for (int ux = 0; ux < 2; ux++) {
			for (int vx = 0; vx < 3; vx++) {
				lv.method_10103(ad + ux * af, ae + vx, s + ux * ag);
				this.field_9286.method_8652(lv, lv2, 18);
			}
		}

		return true;
	}

	public void method_8656(long l) {
		if (l % 100L == 0L) {
			long m = l - 300L;
			ObjectIterator<class_1946.class_1947> objectIterator = this.field_9287.values().iterator();

			while (objectIterator.hasNext()) {
				class_1946.class_1947 lv = (class_1946.class_1947)objectIterator.next();
				if (lv == null || lv.field_9290 < m) {
					objectIterator.remove();
				}
			}
		}
	}

	public class class_1947 extends class_2338 {
		public long field_9290;

		public class_1947(class_2338 arg2, long l) {
			super(arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
			this.field_9290 = l;
		}
	}
}
