package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public abstract class class_2939<C extends class_2920> {
	public static final class_2939<class_3133> field_13304 = method_12704("cave", new class_2925(class_3133::method_13674, 256));
	public static final class_2939<class_3133> field_13297 = method_12704("hell_cave", new class_2934(class_3133::method_13674));
	public static final class_2939<class_3133> field_13295 = method_12704("canyon", new class_2918(class_3133::method_13674));
	public static final class_2939<class_3133> field_13303 = method_12704("underwater_canyon", new class_2938(class_3133::method_13674));
	public static final class_2939<class_3133> field_13300 = method_12704("underwater_cave", new class_2936(class_3133::method_13674));
	protected static final class_2680 field_13301 = class_2246.field_10124.method_9564();
	protected static final class_2680 field_13294 = class_2246.field_10543.method_9564();
	protected static final class_3610 field_13305 = class_3612.field_15910.method_15785();
	protected static final class_3610 field_13296 = class_3612.field_15908.method_15785();
	protected Set<class_2248> field_13302 = ImmutableSet.of(
		class_2246.field_10340,
		class_2246.field_10474,
		class_2246.field_10508,
		class_2246.field_10115,
		class_2246.field_10566,
		class_2246.field_10253,
		class_2246.field_10520,
		class_2246.field_10219,
		class_2246.field_10415,
		class_2246.field_10611,
		class_2246.field_10184,
		class_2246.field_10015,
		class_2246.field_10325,
		class_2246.field_10143,
		class_2246.field_10014,
		class_2246.field_10444,
		class_2246.field_10349,
		class_2246.field_10590,
		class_2246.field_10235,
		class_2246.field_10570,
		class_2246.field_10409,
		class_2246.field_10123,
		class_2246.field_10526,
		class_2246.field_10328,
		class_2246.field_10626,
		class_2246.field_9979,
		class_2246.field_10344,
		class_2246.field_10402,
		class_2246.field_10477,
		class_2246.field_10225
	);
	protected Set<class_3611> field_13298 = ImmutableSet.of(class_3612.field_15910);
	private final Function<Dynamic<?>, ? extends C> field_13299;
	protected final int field_16653;

	private static <C extends class_2920, F extends class_2939<C>> F method_12704(String string, F arg) {
		return class_2378.method_10226(class_2378.field_11157, string, arg);
	}

	public class_2939(Function<Dynamic<?>, ? extends C> function, int i) {
		this.field_13299 = function;
		this.field_16653 = i;
	}

	public int method_12710() {
		return 4;
	}

	protected boolean method_16580(class_2791 arg, long l, int i, int j, int k, double d, double e, double f, double g, double h, BitSet bitSet) {
		Random random = new Random(l + (long)j + (long)k);
		double m = (double)(j * 16 + 8);
		double n = (double)(k * 16 + 8);
		if (!(d < m - 16.0 - g * 2.0) && !(f < n - 16.0 - g * 2.0) && !(d > m + 16.0 + g * 2.0) && !(f > n + 16.0 + g * 2.0)) {
			int o = Math.max(class_3532.method_15357(d - g) - j * 16 - 1, 0);
			int p = Math.min(class_3532.method_15357(d + g) - j * 16 + 1, 16);
			int q = Math.max(class_3532.method_15357(e - h) - 1, 1);
			int r = Math.min(class_3532.method_15357(e + h) + 1, this.field_16653 - 8);
			int s = Math.max(class_3532.method_15357(f - g) - k * 16 - 1, 0);
			int t = Math.min(class_3532.method_15357(f + g) - k * 16 + 1, 16);
			if (this.method_12711(arg, j, k, o, p, q, r, s, t)) {
				return false;
			} else {
				boolean bl = false;
				class_2338.class_2339 lv = new class_2338.class_2339();
				class_2338.class_2339 lv2 = new class_2338.class_2339();
				class_2338.class_2339 lv3 = new class_2338.class_2339();

				for (int u = o; u < p; u++) {
					int v = u + j * 16;
					double w = ((double)v + 0.5 - d) / g;

					for (int x = s; x < t; x++) {
						int y = x + k * 16;
						double z = ((double)y + 0.5 - f) / g;
						if (!(w * w + z * z >= 1.0)) {
							AtomicBoolean atomicBoolean = new AtomicBoolean(false);

							for (int aa = r; aa > q; aa--) {
								double ab = ((double)aa - 0.5 - e) / h;
								if (!this.method_16582(w, ab, z, aa)) {
									bl |= this.method_16581(arg, bitSet, random, lv, lv2, lv3, i, j, k, v, y, u, aa, x, atomicBoolean);
								}
							}
						}
					}
				}

				return bl;
			}
		} else {
			return false;
		}
	}

	protected boolean method_16581(
		class_2791 arg,
		BitSet bitSet,
		Random random,
		class_2338.class_2339 arg2,
		class_2338.class_2339 arg3,
		class_2338.class_2339 arg4,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		int o,
		int p,
		AtomicBoolean atomicBoolean
	) {
		int q = n | p << 4 | o << 8;
		if (bitSet.get(q)) {
			return false;
		} else {
			bitSet.set(q);
			arg2.method_10103(l, o, m);
			class_2680 lv = arg.method_8320(arg2);
			class_2680 lv2 = arg.method_8320(arg3.method_10101(arg2).method_10098(class_2350.field_11036));
			if (lv.method_11614() == class_2246.field_10219 || lv.method_11614() == class_2246.field_10402) {
				atomicBoolean.set(true);
			}

			if (!this.method_12703(lv, lv2)) {
				return false;
			} else {
				if (o < 11) {
					arg.method_12010(arg2, field_13296.method_15759(), false);
				} else {
					arg.method_12010(arg2, field_13294, false);
					if (atomicBoolean.get()) {
						arg4.method_10101(arg2).method_10098(class_2350.field_11033);
						if (arg.method_8320(arg4).method_11614() == class_2246.field_10566) {
							arg.method_12010(arg4, arg.method_16552(arg2).method_8722().method_15337(), false);
						}
					}
				}

				return true;
			}
		}
	}

	public abstract boolean method_12702(class_2791 arg, Random random, int i, int j, int k, int l, int m, BitSet bitSet, C arg2);

	public abstract boolean method_12705(Random random, int i, int j, C arg);

	protected boolean method_12709(class_2680 arg) {
		return this.field_13302.contains(arg.method_11614());
	}

	protected boolean method_12703(class_2680 arg, class_2680 arg2) {
		class_2248 lv = arg.method_11614();
		return this.method_12709(arg) || (lv == class_2246.field_10102 || lv == class_2246.field_10255) && !arg2.method_11618().method_15767(class_3486.field_15517);
	}

	protected boolean method_12711(class_2791 arg, int i, int j, int k, int l, int m, int n, int o, int p) {
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int q = k; q < l; q++) {
			for (int r = o; r < p; r++) {
				for (int s = m - 1; s <= n + 1; s++) {
					if (this.field_13298.contains(arg.method_8316(lv.method_10103(q + i * 16, s, r + j * 16)).method_15772())) {
						return true;
					}

					if (s != n + 1 && !this.method_12706(k, l, o, p, q, r)) {
						s = n;
					}
				}
			}
		}

		return false;
	}

	private boolean method_12706(int i, int j, int k, int l, int m, int n) {
		return m == i || m == j - 1 || n == k || n == l - 1;
	}

	protected boolean method_12707(int i, int j, double d, double e, int k, int l, float f) {
		double g = (double)(i * 16 + 8);
		double h = (double)(j * 16 + 8);
		double m = d - g;
		double n = e - h;
		double o = (double)(l - k);
		double p = (double)(f + 2.0F + 16.0F);
		return m * m + n * n - o * o <= p * p;
	}

	protected abstract boolean method_16582(double d, double e, double f, int i);
}
