package net.minecraft;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface class_1941 extends class_1920 {
	default boolean method_8623(class_2338 arg) {
		return this.method_8320(arg).method_11588();
	}

	default boolean method_8626(class_2338 arg) {
		if (arg.method_10264() >= this.method_8615()) {
			return this.method_8311(arg);
		} else {
			class_2338 lv = new class_2338(arg.method_10263(), this.method_8615(), arg.method_10260());
			if (!this.method_8311(lv)) {
				return false;
			} else {
				for (class_2338 var4 = lv.method_10074(); var4.method_10264() > arg.method_10264(); var4 = var4.method_10074()) {
					class_2680 lv2 = this.method_8320(var4);
					if (lv2.method_11581(this, var4) > 0 && !lv2.method_11620().method_15797()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	int method_8624(class_2338 arg, int i);

	@Nullable
	class_2791 method_8402(int i, int j, class_2806 arg, boolean bl);

	boolean method_8393(int i, int j);

	class_2338 method_8598(class_2902.class_2903 arg, class_2338 arg2);

	int method_8589(class_2902.class_2903 arg, int i, int j);

	default float method_8610(class_2338 arg) {
		return this.method_8597().method_12456()[this.method_8602(arg)];
	}

	@Nullable
	default class_1657 method_8614(class_1297 arg, double d) {
		return this.method_8620(arg.field_5987, arg.field_6010, arg.field_6035, d, false);
	}

	@Nullable
	default class_1657 method_8613(class_1297 arg, double d) {
		return this.method_8620(arg.field_5987, arg.field_6010, arg.field_6035, d, true);
	}

	@Nullable
	default class_1657 method_8620(double d, double e, double f, double g, boolean bl) {
		Predicate<class_1297> predicate = bl ? class_1301.field_6156 : class_1301.field_6155;
		return this.method_8604(d, e, f, g, predicate);
	}

	@Nullable
	class_1657 method_8604(double d, double e, double f, double g, Predicate<class_1297> predicate);

	int method_8594();

	class_2784 method_8621();

	boolean method_8611(@Nullable class_1297 arg, class_265 arg2);

	int method_8596(class_2338 arg, class_2350 arg2);

	boolean method_8608();

	int method_8615();

	default class_2791 method_16955(class_2338 arg) {
		return this.method_8392(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	default class_2791 method_8392(int i, int j) {
		return this.method_8402(i, j, class_2806.field_12803, true);
	}

	default class_2791 method_16956(int i, int j, class_2806 arg) {
		return this.method_8402(i, j, arg, true);
	}

	default boolean method_8628(class_2680 arg, class_2338 arg2) {
		class_265 lv = arg.method_11628(this, arg2);
		return lv.method_1110() || this.method_8611(null, lv.method_1096((double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260()));
	}

	default boolean method_8606(@Nullable class_1297 arg, class_238 arg2) {
		return this.method_8611(arg, class_259.method_1078(arg2));
	}

	default Stream<class_265> method_8601(@Nullable class_1297 arg, class_265 arg2, class_265 arg3, boolean bl) {
		int i = class_3532.method_15357(arg2.method_1091(class_2350.class_2351.field_11048)) - 1;
		int j = class_3532.method_15384(arg2.method_1105(class_2350.class_2351.field_11048)) + 1;
		int k = class_3532.method_15357(arg2.method_1091(class_2350.class_2351.field_11052)) - 1;
		int l = class_3532.method_15384(arg2.method_1105(class_2350.class_2351.field_11052)) + 1;
		int m = class_3532.method_15357(arg2.method_1091(class_2350.class_2351.field_11051)) - 1;
		int n = class_3532.method_15384(arg2.method_1105(class_2350.class_2351.field_11051)) + 1;
		class_2784 lv = this.method_8621();
		boolean bl2 = lv.method_11976() < (double)i && (double)j < lv.method_11963() && lv.method_11958() < (double)m && (double)n < lv.method_11977();
		class_251 lv2 = new class_244(j - i, l - k, n - m);
		Predicate<class_265> predicate = arg2x -> !arg2x.method_1110() && class_259.method_1074(arg2, arg2x, class_247.field_16896);
		class_3726 lv3 = class_3726.method_16195(arg);
		AtomicReference<class_1923> atomicReference = new AtomicReference(new class_1923(i >> 4, m >> 4));
		AtomicReference<class_2791> atomicReference2 = new AtomicReference(this.method_8402(i >> 4, m >> 4, class_2806.field_12798, false));
		Stream<class_265> stream = StreamSupport.stream(class_2338.class_2339.method_10068(i, k, m, j - 1, l - 1, n - 1).spliterator(), false).map(arg5 -> {
			int o = arg5.method_10263();
			int p = arg5.method_10264();
			int q = arg5.method_10260();
			if (class_1937.method_8476(p)) {
				return class_259.method_1073();
			} else {
				boolean bl3 = o == i || o == j - 1;
				boolean bl4 = p == k || p == l - 1;
				boolean bl5 = q == m || q == n - 1;
				class_1923 lvx = (class_1923)atomicReference.get();
				int r = o >> 4;
				int s = q >> 4;
				class_2791 lv2x;
				if (lvx.field_9181 == r && lvx.field_9180 == s) {
					lv2x = (class_2791)atomicReference2.get();
				} else {
					lv2x = this.method_8402(r, s, class_2806.field_12798, false);
					atomicReference.set(new class_1923(r, s));
					atomicReference2.set(lv2x);
				}

				if ((!bl3 || !bl4) && (!bl4 || !bl5) && (!bl5 || !bl3) && lv2x != null) {
					class_265 lv3x;
					if (bl && !bl2 && !lv.method_11952(arg5)) {
						lv3x = class_259.method_1077();
					} else {
						lv3x = lv2x.method_8320(arg5).method_16337(this, arg5, lv3);
					}

					class_265 lv4 = arg3.method_1096((double)(-o), (double)(-p), (double)(-q));
					if (class_259.method_1074(lv4, lv3x, class_247.field_16896)) {
						return class_259.method_1073();
					} else if (lv3x == class_259.method_1077()) {
						lv2.method_1049(o - i, p - k, q - m, true, true);
						return class_259.method_1073();
					} else {
						return lv3x.method_1096((double)o, (double)p, (double)q);
					}
				} else {
					return class_259.method_1073();
				}
			}
		}).filter(predicate);
		return Stream.concat(stream, Stream.generate(() -> new class_264(lv2, i, k, m)).limit(1L).filter(predicate));
	}

	default Stream<class_265> method_8609(@Nullable class_1297 arg, class_238 arg2, double d, double e, double f) {
		return this.method_8618(arg, arg2, Collections.emptySet(), d, e, f);
	}

	default Stream<class_265> method_8618(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set, double d, double e, double f) {
		double g = 1.0E-7;
		class_265 lv = class_259.method_1078(arg2);
		class_265 lv2 = class_259.method_1078(arg2.method_989(d > 0.0 ? -1.0E-7 : 1.0E-7, e > 0.0 ? -1.0E-7 : 1.0E-7, f > 0.0 ? -1.0E-7 : 1.0E-7));
		class_265 lv3 = class_259.method_1082(class_259.method_1078(arg2.method_1012(d, e, f).method_1014(1.0E-7)), lv2, class_247.field_16886);
		return this.method_8600(arg, lv3, lv, set);
	}

	default Stream<class_265> method_8607(@Nullable class_1297 arg, class_238 arg2) {
		return this.method_8600(arg, class_259.method_1078(arg2), class_259.method_1073(), Collections.emptySet());
	}

	default Stream<class_265> method_8600(@Nullable class_1297 arg, class_265 arg2, class_265 arg3, Set<class_1297> set) {
		boolean bl = arg != null && arg.method_5686();
		boolean bl2 = arg != null && this.method_8625(arg);
		if (arg != null && bl == bl2) {
			arg.method_5789(!bl2);
		}

		return this.method_8601(arg, arg2, arg3, bl2);
	}

	default boolean method_8625(class_1297 arg) {
		class_2784 lv = this.method_8621();
		double d = lv.method_11976();
		double e = lv.method_11958();
		double f = lv.method_11963();
		double g = lv.method_11977();
		if (arg.method_5686()) {
			d++;
			e++;
			f--;
			g--;
		} else {
			d--;
			e--;
			f++;
			g++;
		}

		return arg.field_5987 > d && arg.field_5987 < f && arg.field_6035 > e && arg.field_6035 < g;
	}

	default boolean method_8590(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set) {
		return this.method_8600(arg, class_259.method_1078(arg2), class_259.method_1073(), set).allMatch(class_265::method_1110);
	}

	default boolean method_8587(@Nullable class_1297 arg, class_238 arg2) {
		return this.method_8590(arg, arg2, Collections.emptySet());
	}

	default boolean method_8585(class_2338 arg) {
		return this.method_8316(arg).method_15767(class_3486.field_15517);
	}

	default boolean method_8599(class_238 arg) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15384(arg.field_1320);
		int k = class_3532.method_15357(arg.field_1322);
		int l = class_3532.method_15384(arg.field_1325);
		int m = class_3532.method_15357(arg.field_1321);
		int n = class_3532.method_15384(arg.field_1324);

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						class_2680 lv2 = this.method_8320(lv.method_10113(o, p, q));
						if (!lv2.method_11618().method_15769()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	default int method_8602(class_2338 arg) {
		return this.method_8603(arg, this.method_8594());
	}

	default int method_8603(class_2338 arg, int i) {
		if (arg.method_10263() < -30000000 || arg.method_10260() < -30000000 || arg.method_10263() >= 30000000 || arg.method_10260() >= 30000000) {
			return 15;
		} else if (this.method_8320(arg).method_11593(this, arg)) {
			int j = this.method_8624(arg.method_10084(), i);
			int k = this.method_8624(arg.method_10078(), i);
			int l = this.method_8624(arg.method_10067(), i);
			int m = this.method_8624(arg.method_10072(), i);
			int n = this.method_8624(arg.method_10095(), i);
			if (k > j) {
				j = k;
			}

			if (l > j) {
				j = l;
			}

			if (m > j) {
				j = m;
			}

			if (n > j) {
				j = n;
			}

			return j;
		} else {
			return this.method_8624(arg, i);
		}
	}

	default boolean method_8591(class_2338 arg) {
		return this.method_8393(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	default boolean method_8617(class_2338 arg, class_2338 arg2) {
		return this.method_8627(arg.method_10263(), arg.method_10264(), arg.method_10260(), arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
	}

	default boolean method_8627(int i, int j, int k, int l, int m, int n) {
		if (m >= 0 && j < 256) {
			i >>= 4;
			k >>= 4;
			l >>= 4;
			n >>= 4;

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (!this.method_8393(o, p)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	class_2869 method_8597();
}
