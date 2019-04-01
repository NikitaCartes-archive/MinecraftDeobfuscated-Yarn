package net.minecraft;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class class_1948 {
	private static final Logger field_9292 = LogManager.getLogger();

	public static void method_8663(class_1311 arg, class_1937 arg2, class_2818 arg3, class_2338 arg4) {
		class_2794<?> lv = arg2.method_8398().method_12129();
		int i = 0;
		class_2338 lv2 = method_8657(arg2, arg3);
		int j = lv2.method_10263();
		int k = lv2.method_10264();
		int l = lv2.method_10260();
		if (k >= 1) {
			class_2680 lv3 = arg3.method_8320(lv2);
			if (!lv3.method_11621(arg3, lv2)) {
				class_2338.class_2339 lv4 = new class_2338.class_2339();
				int m = 0;

				while (m < 3) {
					int n = j;
					int o = l;
					int p = 6;
					class_1959.class_1964 lv5 = null;
					class_1315 lv6 = null;
					int q = class_3532.method_15384(Math.random() * 4.0);
					int r = 0;
					int s = 0;

					while (true) {
						label119: {
							label94:
							if (s < q) {
								n += arg2.field_9229.nextInt(6) - arg2.field_9229.nextInt(6);
								o += arg2.field_9229.nextInt(6) - arg2.field_9229.nextInt(6);
								lv4.method_10103(n, k, o);
								float f = (float)n + 0.5F;
								float g = (float)o + 0.5F;
								class_1657 lv7 = arg2.method_18457((double)f, (double)g, -1.0);
								if (lv7 == null || lv7.method_5649((double)f, (double)k, (double)g) <= 576.0 || arg4.method_19769(new class_243((double)f, (double)k, (double)g), 24.0)
									)
								 {
									break label119;
								}

								class_1923 lv8 = new class_1923(lv4);
								if (!Objects.equals(lv8, arg3.method_12004())) {
									break label119;
								}

								if (lv5 == null) {
									lv5 = method_8664(lv, arg, arg2.field_9229, lv4);
									if (lv5 == null) {
										break label94;
									}

									q = lv5.field_9388 + arg2.field_9229.nextInt(1 + lv5.field_9387 - lv5.field_9388);
								}

								if (lv5.field_9389.method_5891() == class_1311.field_17715) {
									break label119;
								}

								class_1299<?> lv9 = lv5.field_9389;
								if (!lv9.method_5896() || !method_8659(lv, arg, lv5, lv4)) {
									break label119;
								}

								class_1317.class_1319 lv10 = class_1317.method_6159(lv9);
								if (lv10 == null || !method_8660(lv10, arg2, lv4, lv9) || !arg2.method_18026(lv9.method_17683((double)f, (double)k, (double)g))) {
									break label119;
								}

								class_1308 lv12;
								try {
									class_1297 lv11 = lv9.method_5883(arg2);
									if (!(lv11 instanceof class_1308)) {
										throw new IllegalStateException("Trying to spawn a non-mob: " + class_2378.field_11145.method_10221(lv9));
									}

									lv12 = (class_1308)lv11;
								} catch (Exception var29) {
									field_9292.warn("Failed to create mob", (Throwable)var29);
									return;
								}

								lv12.method_5808((double)f, (double)k, (double)g, arg2.field_9229.nextFloat() * 360.0F, 0.0F);
								if (lv7.method_5649((double)f, (double)k, (double)g) > 16384.0 && lv12.method_5974(lv7.method_5649((double)f, (double)k, (double)g))
									|| !lv12.method_5979(arg2, class_3730.field_16459)
									|| !lv12.method_5957(arg2)) {
									break label119;
								}

								lv6 = lv12.method_5943(arg2, arg2.method_8404(new class_2338(lv12)), class_3730.field_16459, lv6, null);
								i++;
								r++;
								arg2.method_8649(lv12);
								if (i >= lv12.method_5945()) {
									return;
								}

								if (!lv12.method_5969(r)) {
									break label119;
								}
							}

							m++;
							break;
						}

						s++;
					}
				}
			}
		}
	}

	@Nullable
	private static class_1959.class_1964 method_8664(class_2794<?> arg, class_1311 arg2, Random random, class_2338 arg3) {
		List<class_1959.class_1964> list = arg.method_12113(arg2, arg3);
		return list.isEmpty() ? null : class_3549.method_15446(random, list);
	}

	private static boolean method_8659(class_2794<?> arg, class_1311 arg2, class_1959.class_1964 arg3, class_2338 arg4) {
		List<class_1959.class_1964> list = arg.method_12113(arg2, arg4);
		return list.isEmpty() ? false : list.contains(arg3);
	}

	private static class_2338 method_8657(class_1937 arg, class_2818 arg2) {
		class_1923 lv = arg2.method_12004();
		int i = lv.method_8326() + arg.field_9229.nextInt(16);
		int j = lv.method_8328() + arg.field_9229.nextInt(16);
		int k = arg2.method_12005(class_2902.class_2903.field_13202, i, j) + 1;
		int l = arg.field_9229.nextInt(k + 1);
		return new class_2338(i, l, j);
	}

	public static boolean method_8662(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		if (class_2248.method_9614(arg3.method_11628(arg, arg2))) {
			return false;
		} else if (arg3.method_11634()) {
			return false;
		} else {
			return !arg4.method_15769() ? false : !arg3.method_11602(class_3481.field_15463);
		}
	}

	public static boolean method_8660(class_1317.class_1319 arg, class_1941 arg2, class_2338 arg3, @Nullable class_1299<?> arg4) {
		if (arg4 != null && arg2.method_8621().method_11952(arg3)) {
			class_2680 lv = arg2.method_8320(arg3);
			class_3610 lv2 = arg2.method_8316(arg3);
			class_2338 lv3 = arg3.method_10084();
			class_2338 lv4 = arg3.method_10074();
			switch (arg) {
				case field_6318:
					return lv2.method_15767(class_3486.field_15517)
						&& arg2.method_8316(lv4).method_15767(class_3486.field_15517)
						&& !arg2.method_8320(lv3).method_11621(arg2, lv3);
				case field_6317:
				default:
					class_2680 lv5 = arg2.method_8320(lv4);
					return !lv5.method_11611(arg2, lv4, arg4)
						? false
						: method_8662(arg2, arg3, lv, lv2) && method_8662(arg2, lv3, arg2.method_8320(lv3), arg2.method_8316(lv3));
			}
		} else {
			return false;
		}
	}

	public static void method_8661(class_1936 arg, class_1959 arg2, int i, int j, Random random) {
		List<class_1959.class_1964> list = arg2.method_8700(class_1311.field_6294);
		if (!list.isEmpty()) {
			int k = i << 4;
			int l = j << 4;

			while (random.nextFloat() < arg2.method_8690()) {
				class_1959.class_1964 lv = class_3549.method_15446(random, list);
				int m = lv.field_9388 + random.nextInt(1 + lv.field_9387 - lv.field_9388);
				class_1315 lv2 = null;
				int n = k + random.nextInt(16);
				int o = l + random.nextInt(16);
				int p = n;
				int q = o;

				for (int r = 0; r < m; r++) {
					boolean bl = false;

					for (int s = 0; !bl && s < 4; s++) {
						class_2338 lv3 = method_8658(arg, lv.field_9389, n, o);
						if (lv.field_9389.method_5896() && method_8660(class_1317.class_1319.field_6317, arg, lv3, lv.field_9389)) {
							float f = lv.field_9389.method_17685();
							double d = class_3532.method_15350((double)n, (double)k + (double)f, (double)k + 16.0 - (double)f);
							double e = class_3532.method_15350((double)o, (double)l + (double)f, (double)l + 16.0 - (double)f);
							if (!arg.method_18026(lv.field_9389.method_17683(d, (double)lv3.method_10264(), e))) {
								continue;
							}

							class_1297 lv4;
							try {
								lv4 = lv.field_9389.method_5883(arg.method_8410());
							} catch (Exception var26) {
								field_9292.warn("Failed to create mob", (Throwable)var26);
								continue;
							}

							lv4.method_5808(d, (double)lv3.method_10264(), e, random.nextFloat() * 360.0F, 0.0F);
							if (lv4 instanceof class_1308) {
								class_1308 lv5 = (class_1308)lv4;
								if (lv5.method_5979(arg, class_3730.field_16472) && lv5.method_5957(arg)) {
									lv2 = lv5.method_5943(arg, arg.method_8404(new class_2338(lv5)), class_3730.field_16472, lv2, null);
									arg.method_8649(lv5);
									bl = true;
								}
							}
						}

						n += random.nextInt(5) - random.nextInt(5);

						for (o += random.nextInt(5) - random.nextInt(5); n < k || n >= k + 16 || o < l || o >= l + 16; o = q + random.nextInt(5) - random.nextInt(5)) {
							n = p + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}

	private static class_2338 method_8658(class_1941 arg, @Nullable class_1299<?> arg2, int i, int j) {
		class_2338 lv = new class_2338(i, arg.method_8589(class_1317.method_6160(arg2), i, j), j);
		class_2338 lv2 = lv.method_10074();
		return arg.method_8320(lv2).method_11609(arg, lv2, class_10.field_50) ? lv2 : lv;
	}
}
