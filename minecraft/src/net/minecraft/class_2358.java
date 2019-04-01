package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2358 extends class_2248 {
	public static final class_2758 field_11092 = class_2741.field_12498;
	public static final class_2746 field_11096 = class_2429.field_11332;
	public static final class_2746 field_11094 = class_2429.field_11335;
	public static final class_2746 field_11089 = class_2429.field_11331;
	public static final class_2746 field_11088 = class_2429.field_11328;
	public static final class_2746 field_11093 = class_2429.field_11327;
	private static final Map<class_2350, class_2746> field_11090 = (Map<class_2350, class_2746>)class_2429.field_11329
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != class_2350.field_11033)
		.collect(class_156.method_664());
	private final Object2IntMap<class_2248> field_11095 = new Object2IntOpenHashMap<>();
	private final Object2IntMap<class_2248> field_11091 = new Object2IntOpenHashMap<>();

	protected class_2358(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11092, Integer.valueOf(0))
				.method_11657(field_11096, Boolean.valueOf(false))
				.method_11657(field_11094, Boolean.valueOf(false))
				.method_11657(field_11089, Boolean.valueOf(false))
				.method_11657(field_11088, Boolean.valueOf(false))
				.method_11657(field_11093, Boolean.valueOf(false))
		);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_259.method_1073();
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return this.method_9558(arg, arg4, arg5)
			? this.method_10198(arg4, arg5).method_11657(field_11092, arg.method_11654(field_11092))
			: class_2246.field_10124.method_9564();
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_10198(arg.method_8045(), arg.method_8037());
	}

	public class_2680 method_10198(class_1922 arg, class_2338 arg2) {
		class_2338 lv = arg2.method_10074();
		class_2680 lv2 = arg.method_8320(lv);
		if (!this.method_10195(lv2) && !class_2248.method_20045(lv2, arg, lv, class_2350.field_11036)) {
			class_2680 lv3 = this.method_9564();

			for (class_2350 lv4 : class_2350.values()) {
				class_2746 lv5 = (class_2746)field_11090.get(lv4);
				if (lv5 != null) {
					lv3 = lv3.method_11657(lv5, Boolean.valueOf(this.method_10195(arg.method_8320(arg2.method_10093(lv4)))));
				}
			}

			return lv3;
		} else {
			return this.method_9564();
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return class_2248.method_20045(arg2.method_8320(lv), arg2, lv, class_2350.field_11036) || this.method_10193(arg2, arg3);
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 30;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.method_8450().method_8355("doFireTick")) {
			if (!arg.method_11591(arg2, arg3)) {
				arg2.method_8650(arg3, false);
			}

			class_2248 lv = arg2.method_8320(arg3.method_10074()).method_11614();
			boolean bl = arg2.field_9247 instanceof class_2880 && lv == class_2246.field_9987 || lv == class_2246.field_10515 || lv == class_2246.field_10092;
			int i = (Integer)arg.method_11654(field_11092);
			if (!bl && arg2.method_8419() && this.method_10192(arg2, arg3) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				arg2.method_8650(arg3, false);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					arg = arg.method_11657(field_11092, Integer.valueOf(j));
					arg2.method_8652(arg3, arg, 4);
				}

				if (!bl) {
					arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2) + random.nextInt(10));
					if (!this.method_10193(arg2, arg3)) {
						class_2338 lv2 = arg3.method_10074();
						if (!class_2248.method_20045(arg2.method_8320(lv2), arg2, lv2, class_2350.field_11036) || i > 3) {
							arg2.method_8650(arg3, false);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.method_10195(arg2.method_8320(arg3.method_10074()))) {
						arg2.method_8650(arg3, false);
						return;
					}
				}

				boolean bl2 = arg2.method_8480(arg3);
				int k = bl2 ? -50 : 0;
				this.method_10196(arg2, arg3.method_10078(), 300 + k, random, i);
				this.method_10196(arg2, arg3.method_10067(), 300 + k, random, i);
				this.method_10196(arg2, arg3.method_10074(), 250 + k, random, i);
				this.method_10196(arg2, arg3.method_10084(), 250 + k, random, i);
				this.method_10196(arg2, arg3.method_10095(), 300 + k, random, i);
				this.method_10196(arg2, arg3.method_10072(), 300 + k, random, i);
				class_2338.class_2339 lv3 = new class_2338.class_2339();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								lv3.method_10101(arg3).method_10100(l, n, m);
								int p = this.method_10194(arg2, lv3);
								if (p > 0) {
									int q = (p + 40 + arg2.method_8407().method_5461() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!arg2.method_8419() || !this.method_10192(arg2, lv3))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										arg2.method_8652(lv3, this.method_10198(arg2, lv3).method_11657(field_11092, Integer.valueOf(r)), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean method_10192(class_1937 arg, class_2338 arg2) {
		return arg.method_8520(arg2)
			|| arg.method_8520(arg2.method_10067())
			|| arg.method_8520(arg2.method_10078())
			|| arg.method_8520(arg2.method_10095())
			|| arg.method_8520(arg2.method_10072());
	}

	private int method_10190(class_2680 arg) {
		return arg.method_11570(class_2741.field_12508) && arg.method_11654(class_2741.field_12508) ? 0 : this.field_11091.getInt(arg.method_11614());
	}

	private int method_10191(class_2680 arg) {
		return arg.method_11570(class_2741.field_12508) && arg.method_11654(class_2741.field_12508) ? 0 : this.field_11095.getInt(arg.method_11614());
	}

	private void method_10196(class_1937 arg, class_2338 arg2, int i, Random random, int j) {
		int k = this.method_10190(arg.method_8320(arg2));
		if (random.nextInt(i) < k) {
			class_2680 lv = arg.method_8320(arg2);
			if (random.nextInt(j + 10) < 5 && !arg.method_8520(arg2)) {
				int l = Math.min(j + random.nextInt(5) / 4, 15);
				arg.method_8652(arg2, this.method_10198(arg, arg2).method_11657(field_11092, Integer.valueOf(l)), 3);
			} else {
				arg.method_8650(arg2, false);
			}

			class_2248 lv2 = lv.method_11614();
			if (lv2 instanceof class_2530) {
				class_2530.method_10738(arg, arg2);
			}
		}
	}

	private boolean method_10193(class_1922 arg, class_2338 arg2) {
		for (class_2350 lv : class_2350.values()) {
			if (this.method_10195(arg.method_8320(arg2.method_10093(lv)))) {
				return true;
			}
		}

		return false;
	}

	private int method_10194(class_1941 arg, class_2338 arg2) {
		if (!arg.method_8623(arg2)) {
			return 0;
		} else {
			int i = 0;

			for (class_2350 lv : class_2350.values()) {
				class_2680 lv2 = arg.method_8320(arg2.method_10093(lv));
				i = Math.max(this.method_10191(lv2), i);
			}

			return i;
		}
	}

	public boolean method_10195(class_2680 arg) {
		return this.method_10191(arg) > 0;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			if (arg2.field_9247.method_12460() != class_2874.field_13072 && arg2.field_9247.method_12460() != class_2874.field_13076
				|| !((class_2423)class_2246.field_10316).method_10352(arg2, arg3)) {
				if (!arg.method_11591(arg2, arg3)) {
					arg2.method_8650(arg3, false);
				} else {
					arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2) + arg2.field_9229.nextInt(10));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(24) == 0) {
			arg2.method_8486(
				(double)((float)arg3.method_10263() + 0.5F),
				(double)((float)arg3.method_10264() + 0.5F),
				(double)((float)arg3.method_10260() + 0.5F),
				class_3417.field_14993,
				class_3419.field_15245,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		if (!this.method_10195(lv2) && !class_2248.method_20045(lv2, arg2, lv, class_2350.field_11036)) {
			if (this.method_10195(arg2.method_8320(arg3.method_10067()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)arg3.method_10263() + random.nextDouble() * 0.1F;
					double e = (double)arg3.method_10264() + random.nextDouble();
					double f = (double)arg3.method_10260() + random.nextDouble();
					arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(arg2.method_8320(arg3.method_10078()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)(arg3.method_10263() + 1) - random.nextDouble() * 0.1F;
					double e = (double)arg3.method_10264() + random.nextDouble();
					double f = (double)arg3.method_10260() + random.nextDouble();
					arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(arg2.method_8320(arg3.method_10095()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)arg3.method_10263() + random.nextDouble();
					double e = (double)arg3.method_10264() + random.nextDouble();
					double f = (double)arg3.method_10260() + random.nextDouble() * 0.1F;
					arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(arg2.method_8320(arg3.method_10072()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)arg3.method_10263() + random.nextDouble();
					double e = (double)arg3.method_10264() + random.nextDouble();
					double f = (double)(arg3.method_10260() + 1) - random.nextDouble() * 0.1F;
					arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(arg2.method_8320(arg3.method_10084()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)arg3.method_10263() + random.nextDouble();
					double e = (double)(arg3.method_10264() + 1) - random.nextDouble() * 0.1F;
					double f = (double)arg3.method_10260() + random.nextDouble();
					arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)arg3.method_10263() + random.nextDouble();
				double e = (double)arg3.method_10264() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)arg3.method_10260() + random.nextDouble();
				arg2.method_8406(class_2398.field_11237, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11092, field_11096, field_11094, field_11089, field_11088, field_11093);
	}

	public void method_10189(class_2248 arg, int i, int j) {
		this.field_11095.put(arg, i);
		this.field_11091.put(arg, j);
	}

	public static void method_10199() {
		class_2358 lv = (class_2358)class_2246.field_10036;
		lv.method_10189(class_2246.field_10161, 5, 20);
		lv.method_10189(class_2246.field_9975, 5, 20);
		lv.method_10189(class_2246.field_10148, 5, 20);
		lv.method_10189(class_2246.field_10334, 5, 20);
		lv.method_10189(class_2246.field_10218, 5, 20);
		lv.method_10189(class_2246.field_10075, 5, 20);
		lv.method_10189(class_2246.field_10119, 5, 20);
		lv.method_10189(class_2246.field_10071, 5, 20);
		lv.method_10189(class_2246.field_10257, 5, 20);
		lv.method_10189(class_2246.field_10617, 5, 20);
		lv.method_10189(class_2246.field_10031, 5, 20);
		lv.method_10189(class_2246.field_10500, 5, 20);
		lv.method_10189(class_2246.field_10188, 5, 20);
		lv.method_10189(class_2246.field_10291, 5, 20);
		lv.method_10189(class_2246.field_10513, 5, 20);
		lv.method_10189(class_2246.field_10041, 5, 20);
		lv.method_10189(class_2246.field_10196, 5, 20);
		lv.method_10189(class_2246.field_10457, 5, 20);
		lv.method_10189(class_2246.field_10620, 5, 20);
		lv.method_10189(class_2246.field_10020, 5, 20);
		lv.method_10189(class_2246.field_10299, 5, 20);
		lv.method_10189(class_2246.field_10319, 5, 20);
		lv.method_10189(class_2246.field_10132, 5, 20);
		lv.method_10189(class_2246.field_10144, 5, 20);
		lv.method_10189(class_2246.field_10563, 5, 20);
		lv.method_10189(class_2246.field_10408, 5, 20);
		lv.method_10189(class_2246.field_10569, 5, 20);
		lv.method_10189(class_2246.field_10122, 5, 20);
		lv.method_10189(class_2246.field_10256, 5, 20);
		lv.method_10189(class_2246.field_10616, 5, 20);
		lv.method_10189(class_2246.field_10431, 5, 5);
		lv.method_10189(class_2246.field_10037, 5, 5);
		lv.method_10189(class_2246.field_10511, 5, 5);
		lv.method_10189(class_2246.field_10306, 5, 5);
		lv.method_10189(class_2246.field_10533, 5, 5);
		lv.method_10189(class_2246.field_10010, 5, 5);
		lv.method_10189(class_2246.field_10519, 5, 5);
		lv.method_10189(class_2246.field_10436, 5, 5);
		lv.method_10189(class_2246.field_10366, 5, 5);
		lv.method_10189(class_2246.field_10254, 5, 5);
		lv.method_10189(class_2246.field_10622, 5, 5);
		lv.method_10189(class_2246.field_10244, 5, 5);
		lv.method_10189(class_2246.field_10250, 5, 5);
		lv.method_10189(class_2246.field_10558, 5, 5);
		lv.method_10189(class_2246.field_10204, 5, 5);
		lv.method_10189(class_2246.field_10084, 5, 5);
		lv.method_10189(class_2246.field_10103, 5, 5);
		lv.method_10189(class_2246.field_10374, 5, 5);
		lv.method_10189(class_2246.field_10126, 5, 5);
		lv.method_10189(class_2246.field_10155, 5, 5);
		lv.method_10189(class_2246.field_10307, 5, 5);
		lv.method_10189(class_2246.field_10303, 5, 5);
		lv.method_10189(class_2246.field_9999, 5, 5);
		lv.method_10189(class_2246.field_10178, 5, 5);
		lv.method_10189(class_2246.field_10503, 30, 60);
		lv.method_10189(class_2246.field_9988, 30, 60);
		lv.method_10189(class_2246.field_10539, 30, 60);
		lv.method_10189(class_2246.field_10335, 30, 60);
		lv.method_10189(class_2246.field_10098, 30, 60);
		lv.method_10189(class_2246.field_10035, 30, 60);
		lv.method_10189(class_2246.field_10504, 30, 20);
		lv.method_10189(class_2246.field_10375, 15, 100);
		lv.method_10189(class_2246.field_10479, 60, 100);
		lv.method_10189(class_2246.field_10112, 60, 100);
		lv.method_10189(class_2246.field_10428, 60, 100);
		lv.method_10189(class_2246.field_10583, 60, 100);
		lv.method_10189(class_2246.field_10378, 60, 100);
		lv.method_10189(class_2246.field_10430, 60, 100);
		lv.method_10189(class_2246.field_10003, 60, 100);
		lv.method_10189(class_2246.field_10214, 60, 100);
		lv.method_10189(class_2246.field_10313, 60, 100);
		lv.method_10189(class_2246.field_10182, 60, 100);
		lv.method_10189(class_2246.field_10449, 60, 100);
		lv.method_10189(class_2246.field_10086, 60, 100);
		lv.method_10189(class_2246.field_10226, 60, 100);
		lv.method_10189(class_2246.field_10573, 60, 100);
		lv.method_10189(class_2246.field_10270, 60, 100);
		lv.method_10189(class_2246.field_10048, 60, 100);
		lv.method_10189(class_2246.field_10156, 60, 100);
		lv.method_10189(class_2246.field_10315, 60, 100);
		lv.method_10189(class_2246.field_10554, 60, 100);
		lv.method_10189(class_2246.field_9995, 60, 100);
		lv.method_10189(class_2246.field_10548, 60, 100);
		lv.method_10189(class_2246.field_10606, 60, 100);
		lv.method_10189(class_2246.field_10446, 30, 60);
		lv.method_10189(class_2246.field_10095, 30, 60);
		lv.method_10189(class_2246.field_10215, 30, 60);
		lv.method_10189(class_2246.field_10294, 30, 60);
		lv.method_10189(class_2246.field_10490, 30, 60);
		lv.method_10189(class_2246.field_10028, 30, 60);
		lv.method_10189(class_2246.field_10459, 30, 60);
		lv.method_10189(class_2246.field_10423, 30, 60);
		lv.method_10189(class_2246.field_10222, 30, 60);
		lv.method_10189(class_2246.field_10619, 30, 60);
		lv.method_10189(class_2246.field_10259, 30, 60);
		lv.method_10189(class_2246.field_10514, 30, 60);
		lv.method_10189(class_2246.field_10113, 30, 60);
		lv.method_10189(class_2246.field_10170, 30, 60);
		lv.method_10189(class_2246.field_10314, 30, 60);
		lv.method_10189(class_2246.field_10146, 30, 60);
		lv.method_10189(class_2246.field_10597, 15, 100);
		lv.method_10189(class_2246.field_10381, 5, 5);
		lv.method_10189(class_2246.field_10359, 60, 20);
		lv.method_10189(class_2246.field_10466, 60, 20);
		lv.method_10189(class_2246.field_9977, 60, 20);
		lv.method_10189(class_2246.field_10482, 60, 20);
		lv.method_10189(class_2246.field_10290, 60, 20);
		lv.method_10189(class_2246.field_10512, 60, 20);
		lv.method_10189(class_2246.field_10040, 60, 20);
		lv.method_10189(class_2246.field_10393, 60, 20);
		lv.method_10189(class_2246.field_10591, 60, 20);
		lv.method_10189(class_2246.field_10209, 60, 20);
		lv.method_10189(class_2246.field_10433, 60, 20);
		lv.method_10189(class_2246.field_10510, 60, 20);
		lv.method_10189(class_2246.field_10043, 60, 20);
		lv.method_10189(class_2246.field_10473, 60, 20);
		lv.method_10189(class_2246.field_10338, 60, 20);
		lv.method_10189(class_2246.field_10536, 60, 20);
		lv.method_10189(class_2246.field_10106, 60, 20);
		lv.method_10189(class_2246.field_10342, 30, 60);
		lv.method_10189(class_2246.field_10211, 60, 60);
		lv.method_10189(class_2246.field_16492, 60, 60);
		lv.method_10189(class_2246.field_16330, 30, 20);
		lv.method_10189(class_2246.field_17563, 5, 20);
		lv.method_10189(class_2246.field_16999, 60, 100);
	}
}
