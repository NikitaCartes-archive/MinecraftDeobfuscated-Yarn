package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2700 {
	private final Predicate<class_2694>[][][] field_12358;
	private final int field_12357;
	private final int field_12356;
	private final int field_12355;

	public class_2700(Predicate<class_2694>[][][] predicates) {
		this.field_12358 = predicates;
		this.field_12357 = predicates.length;
		if (this.field_12357 > 0) {
			this.field_12356 = predicates[0].length;
			if (this.field_12356 > 0) {
				this.field_12355 = predicates[0][0].length;
			} else {
				this.field_12355 = 0;
			}
		} else {
			this.field_12356 = 0;
			this.field_12355 = 0;
		}
	}

	public int method_11712() {
		return this.field_12357;
	}

	public int method_11713() {
		return this.field_12356;
	}

	public int method_11710() {
		return this.field_12355;
	}

	@Nullable
	private class_2700.class_2702 method_11711(class_2338 arg, class_2350 arg2, class_2350 arg3, LoadingCache<class_2338, class_2694> loadingCache) {
		for (int i = 0; i < this.field_12355; i++) {
			for (int j = 0; j < this.field_12356; j++) {
				for (int k = 0; k < this.field_12357; k++) {
					if (!this.field_12358[k][j][i].test(loadingCache.getUnchecked(method_11707(arg, arg2, arg3, i, j, k)))) {
						return null;
					}
				}
			}
		}

		return new class_2700.class_2702(arg, arg2, arg3, loadingCache, this.field_12355, this.field_12356, this.field_12357);
	}

	@Nullable
	public class_2700.class_2702 method_11708(class_1941 arg, class_2338 arg2) {
		LoadingCache<class_2338, class_2694> loadingCache = method_11709(arg, false);
		int i = Math.max(Math.max(this.field_12355, this.field_12356), this.field_12357);

		for (class_2338 lv : class_2338.method_10097(arg2, arg2.method_10069(i - 1, i - 1, i - 1))) {
			for (class_2350 lv2 : class_2350.values()) {
				for (class_2350 lv3 : class_2350.values()) {
					if (lv3 != lv2 && lv3 != lv2.method_10153()) {
						class_2700.class_2702 lv4 = this.method_11711(lv, lv2, lv3, loadingCache);
						if (lv4 != null) {
							return lv4;
						}
					}
				}
			}
		}

		return null;
	}

	public static LoadingCache<class_2338, class_2694> method_11709(class_1941 arg, boolean bl) {
		return CacheBuilder.newBuilder().build(new class_2700.class_2701(arg, bl));
	}

	protected static class_2338 method_11707(class_2338 arg, class_2350 arg2, class_2350 arg3, int i, int j, int k) {
		if (arg2 != arg3 && arg2 != arg3.method_10153()) {
			class_2382 lv = new class_2382(arg2.method_10148(), arg2.method_10164(), arg2.method_10165());
			class_2382 lv2 = new class_2382(arg3.method_10148(), arg3.method_10164(), arg3.method_10165());
			class_2382 lv3 = lv.method_10259(lv2);
			return arg.method_10069(
				lv2.method_10263() * -j + lv3.method_10263() * i + lv.method_10263() * k,
				lv2.method_10264() * -j + lv3.method_10264() * i + lv.method_10264() * k,
				lv2.method_10260() * -j + lv3.method_10260() * i + lv.method_10260() * k
			);
		} else {
			throw new IllegalArgumentException("Invalid forwards & up combination");
		}
	}

	static class class_2701 extends CacheLoader<class_2338, class_2694> {
		private final class_1941 field_12359;
		private final boolean field_12360;

		public class_2701(class_1941 arg, boolean bl) {
			this.field_12359 = arg;
			this.field_12360 = bl;
		}

		public class_2694 method_11714(class_2338 arg) throws Exception {
			return new class_2694(this.field_12359, arg, this.field_12360);
		}
	}

	public static class class_2702 {
		private final class_2338 field_12367;
		private final class_2350 field_12365;
		private final class_2350 field_12364;
		private final LoadingCache<class_2338, class_2694> field_12366;
		private final int field_12363;
		private final int field_12362;
		private final int field_12361;

		public class_2702(class_2338 arg, class_2350 arg2, class_2350 arg3, LoadingCache<class_2338, class_2694> loadingCache, int i, int j, int k) {
			this.field_12367 = arg;
			this.field_12365 = arg2;
			this.field_12364 = arg3;
			this.field_12366 = loadingCache;
			this.field_12363 = i;
			this.field_12362 = j;
			this.field_12361 = k;
		}

		public class_2338 method_11715() {
			return this.field_12367;
		}

		public class_2350 method_11719() {
			return this.field_12365;
		}

		public class_2350 method_11716() {
			return this.field_12364;
		}

		public int method_11718() {
			return this.field_12363;
		}

		public int method_11720() {
			return this.field_12362;
		}

		public class_2694 method_11717(int i, int j, int k) {
			return this.field_12366.getUnchecked(class_2700.method_11707(this.field_12367, this.method_11719(), this.method_11716(), i, j, k));
		}

		public String toString() {
			return MoreObjects.toStringHelper(this).add("up", this.field_12364).add("forwards", this.field_12365).add("frontTopLeft", this.field_12367).toString();
		}

		public Pair<class_243, Pair<class_243, Integer>> method_18478(class_2350 arg, class_2338 arg2, double d, class_243 arg3, double e) {
			class_2350 lv = this.method_11719();
			class_2350 lv2 = lv.method_10170();
			double f = (double)(this.method_11715().method_10264() + 1) - d * (double)this.method_11720();
			double g;
			double h;
			if (lv2 == class_2350.field_11043) {
				g = (double)arg2.method_10263() + 0.5;
				h = (double)(this.method_11715().method_10260() + 1) - (1.0 - e) * (double)this.method_11718();
			} else if (lv2 == class_2350.field_11035) {
				g = (double)arg2.method_10263() + 0.5;
				h = (double)this.method_11715().method_10260() + (1.0 - e) * (double)this.method_11718();
			} else if (lv2 == class_2350.field_11039) {
				g = (double)(this.method_11715().method_10263() + 1) - (1.0 - e) * (double)this.method_11718();
				h = (double)arg2.method_10260() + 0.5;
			} else {
				g = (double)this.method_11715().method_10263() + (1.0 - e) * (double)this.method_11718();
				h = (double)arg2.method_10260() + 0.5;
			}

			double i;
			double j;
			if (lv.method_10153() == arg) {
				i = arg3.field_1352;
				j = arg3.field_1350;
			} else if (lv.method_10153() == arg.method_10153()) {
				i = -arg3.field_1352;
				j = -arg3.field_1350;
			} else if (lv.method_10153() == arg.method_10170()) {
				i = -arg3.field_1350;
				j = arg3.field_1352;
			} else {
				i = arg3.field_1350;
				j = -arg3.field_1352;
			}

			int k = (lv.method_10161() - arg.method_10153().method_10161()) * 90;
			return Pair.of(new class_243(g, f, h), Pair.of(new class_243(i, arg3.field_1351, j), k));
		}
	}
}
