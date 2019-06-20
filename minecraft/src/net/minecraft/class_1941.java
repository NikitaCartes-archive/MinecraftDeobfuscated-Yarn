package net.minecraft;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
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

	@Deprecated
	boolean method_8393(int i, int j);

	class_2338 method_8598(class_2902.class_2903 arg, class_2338 arg2);

	int method_8589(class_2902.class_2903 arg, int i, int j);

	default float method_8610(class_2338 arg) {
		return this.method_8597().method_12456()[this.method_8602(arg)];
	}

	int method_8594();

	class_2784 method_8621();

	boolean method_8611(@Nullable class_1297 arg, class_265 arg2);

	default int method_8596(class_2338 arg, class_2350 arg2) {
		return this.method_8320(arg).method_11577(this, arg, arg2);
	}

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

	default class_2806 method_20311() {
		return class_2806.field_12798;
	}

	default boolean method_8628(class_2680 arg, class_2338 arg2, class_3726 arg3) {
		class_265 lv = arg.method_16337(this, arg2, arg3);
		return lv.method_1110() || this.method_8611(null, lv.method_1096((double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260()));
	}

	default boolean method_8606(class_1297 arg) {
		return this.method_8611(arg, class_259.method_1078(arg.method_5829()));
	}

	default boolean method_18026(class_238 arg) {
		return this.method_8590(null, arg, Collections.emptySet());
	}

	default boolean method_17892(class_1297 arg) {
		return this.method_8590(arg, arg.method_5829(), Collections.emptySet());
	}

	default boolean method_8587(class_1297 arg, class_238 arg2) {
		return this.method_8590(arg, arg2, Collections.emptySet());
	}

	default boolean method_8590(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set) {
		return this.method_8600(arg, arg2, set).allMatch(class_265::method_1110);
	}

	default Stream<class_265> method_20743(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set) {
		return Stream.empty();
	}

	default Stream<class_265> method_8600(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set) {
		return Streams.concat(this.method_20812(arg, arg2), this.method_20743(arg, arg2, set));
	}

	default Stream<class_265> method_20812(@Nullable class_1297 arg, class_238 arg2) {
		int i = class_3532.method_15357(arg2.field_1323 - 1.0E-7) - 1;
		int j = class_3532.method_15357(arg2.field_1320 + 1.0E-7) + 1;
		int k = class_3532.method_15357(arg2.field_1322 - 1.0E-7) - 1;
		int l = class_3532.method_15357(arg2.field_1325 + 1.0E-7) + 1;
		int m = class_3532.method_15357(arg2.field_1321 - 1.0E-7) - 1;
		int n = class_3532.method_15357(arg2.field_1324 + 1.0E-7) + 1;
		final class_3726 lv = arg == null ? class_3726.method_16194() : class_3726.method_16195(arg);
		final class_3980 lv2 = new class_3980(i, k, m, j, l, n);
		final class_2338.class_2339 lv3 = new class_2338.class_2339();
		final class_265 lv4 = class_259.method_1078(arg2);
		return StreamSupport.stream(new AbstractSpliterator<class_265>(Long.MAX_VALUE, 1280) {
			boolean field_19296 = arg == null;

			public boolean tryAdvance(Consumer<? super class_265> consumer) {
				if (!this.field_19296) {
					this.field_19296 = true;
					class_265 lv = class_1941.this.method_8621().method_17903();
					boolean bl = class_259.method_1074(lv, class_259.method_1078(arg.method_5829().method_1011(1.0E-7)), class_247.field_16896);
					boolean bl2 = class_259.method_1074(lv, class_259.method_1078(arg.method_5829().method_1014(1.0E-7)), class_247.field_16896);
					if (!bl && bl2) {
						consumer.accept(lv);
						return true;
					}
				}

				while (lv2.method_17963()) {
					int i = lv2.method_18671();
					int j = lv2.method_18672();
					int k = lv2.method_18673();
					int l = lv2.method_20789();
					if (l != 3) {
						int m = i >> 4;
						int n = k >> 4;
						class_2791 lv2 = class_1941.this.method_8402(m, n, class_1941.this.method_20311(), false);
						if (lv2 != null) {
							lv3.method_10103(i, j, k);
							class_2680 lv3 = lv2.method_8320(lv3);
							if ((l != 1 || lv3.method_17900()) && (l != 2 || lv3.method_11614() == class_2246.field_10008)) {
								class_265 lv4 = lv3.method_16337(class_1941.this, lv3, lv);
								class_265 lv5 = lv4.method_1096((double)i, (double)j, (double)k);
								if (class_259.method_1074(lv4, lv5, class_247.field_16896)) {
									consumer.accept(lv5);
									return true;
								}
							}
						}
					}
				}

				return false;
			}
		}, false);
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
		return arg.method_10263() >= -30000000 && arg.method_10260() >= -30000000 && arg.method_10263() < 30000000 && arg.method_10260() < 30000000
			? this.method_8624(arg, i)
			: 15;
	}

	@Deprecated
	default boolean method_8591(class_2338 arg) {
		return this.method_8393(arg.method_10263() >> 4, arg.method_10260() >> 4);
	}

	@Deprecated
	default boolean method_8617(class_2338 arg, class_2338 arg2) {
		return this.method_8627(arg.method_10263(), arg.method_10264(), arg.method_10260(), arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
	}

	@Deprecated
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
