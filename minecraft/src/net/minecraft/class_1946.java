package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1946 {
	private static final Logger field_19277 = LogManager.getLogger();
	private static final class_2423 field_9288 = (class_2423)class_2246.field_10316;
	private final class_3218 field_9286;
	private final Random field_9289;
	private final Map<class_2265, class_1946.class_1947> field_19278 = Maps.<class_2265, class_1946.class_1947>newHashMapWithExpectedSize(4096);
	private final Object2LongMap<class_2265> field_9287 = new Object2LongOpenHashMap<>();

	public class_1946(class_3218 arg) {
		this.field_9286 = arg;
		this.field_9289 = new Random(arg.method_8412());
	}

	public boolean method_8653(class_1297 arg, float f) {
		class_243 lv = arg.method_5656();
		class_2350 lv2 = arg.method_5843();
		class_2700.class_4297 lv3 = this.method_18475(new class_2338(arg), arg.method_18798(), lv2, lv.field_1352, lv.field_1351, arg instanceof class_1657);
		if (lv3 == null) {
			return false;
		} else {
			class_243 lv4 = lv3.field_19281;
			class_243 lv5 = lv3.field_19282;
			arg.method_18799(lv5);
			arg.field_6031 = f + (float)lv3.field_19283;
			if (arg instanceof class_3222) {
				((class_3222)arg).field_13987.method_14363(lv4.field_1352, lv4.field_1351, lv4.field_1350, arg.field_6031, arg.field_5965);
				((class_3222)arg).field_13987.method_14372();
			} else {
				arg.method_5808(lv4.field_1352, lv4.field_1351, lv4.field_1350, arg.field_6031, arg.field_5965);
			}

			return true;
		}
	}

	@Nullable
	public class_2700.class_4297 method_18475(class_2338 arg, class_243 arg2, class_2350 arg3, double d, double e, boolean bl) {
		int i = 128;
		boolean bl2 = true;
		class_2338 lv = null;
		class_2265 lv2 = new class_2265(arg);
		if (!bl && this.field_9287.containsKey(lv2)) {
			return null;
		} else {
			class_1946.class_1947 lv3 = (class_1946.class_1947)this.field_19278.get(lv2);
			if (lv3 != null) {
				lv = lv3.field_19279;
				lv3.field_9290 = this.field_9286.method_8510();
				bl2 = false;
			} else {
				double f = Double.MAX_VALUE;

				for (int j = -128; j <= 128; j++) {
					for (int k = -128; k <= 128; k++) {
						class_2338 lv4 = arg.method_10069(j, this.field_9286.method_8456() - 1 - arg.method_10264(), k);

						while (lv4.method_10264() >= 0) {
							class_2338 lv5 = lv4.method_10074();
							if (this.field_9286.method_8320(lv4).method_11614() == field_9288) {
								for (lv5 = lv4.method_10074(); this.field_9286.method_8320(lv5).method_11614() == field_9288; lv5 = lv5.method_10074()) {
									lv4 = lv5;
								}

								double g = lv4.method_10262(arg);
								if (f < 0.0 || g < f) {
									f = g;
									lv = lv4;
								}
							}

							lv4 = lv5;
						}
					}
				}
			}

			if (lv == null) {
				long l = this.field_9286.method_8510() + 300L;
				this.field_9287.put(lv2, l);
				return null;
			} else {
				if (bl2) {
					this.field_19278.put(lv2, new class_1946.class_1947(lv, this.field_9286.method_8510()));
					field_19277.debug("Adding nether portal ticket for {}:{}", this.field_9286.method_8597()::method_12460, () -> lv2);
					this.field_9286.method_14178().method_17297(class_3230.field_19280, new class_1923(lv), 3, lv2);
				}

				class_2700.class_2702 lv6 = field_9288.method_10350(this.field_9286, lv);
				return lv6.method_18478(arg3, lv, e, arg2, d);
			}
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

	public void method_20464(long l) {
		if (l % 100L == 0L) {
			this.method_8656(l);
			this.method_20467(l);
		}
	}

	private void method_8656(long l) {
		LongIterator longIterator = this.field_9287.values().iterator();

		while (longIterator.hasNext()) {
			long m = longIterator.nextLong();
			if (m <= l) {
				longIterator.remove();
			}
		}
	}

	private void method_20467(long l) {
		long m = l - 300L;
		Iterator<Entry<class_2265, class_1946.class_1947>> iterator = this.field_19278.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<class_2265, class_1946.class_1947> entry = (Entry<class_2265, class_1946.class_1947>)iterator.next();
			class_1946.class_1947 lv = (class_1946.class_1947)entry.getValue();
			if (lv.field_9290 < m) {
				class_2265 lv2 = (class_2265)entry.getKey();
				field_19277.debug("Removing nether portal ticket for {}:{}", this.field_9286.method_8597()::method_12460, () -> lv2);
				this.field_9286.method_14178().method_17300(class_3230.field_19280, new class_1923(lv.field_19279), 3, lv2);
				iterator.remove();
			}
		}
	}

	static class class_1947 {
		public final class_2338 field_19279;
		public long field_9290;

		public class_1947(class_2338 arg, long l) {
			this.field_19279 = arg;
			this.field_9290 = l;
		}
	}
}
