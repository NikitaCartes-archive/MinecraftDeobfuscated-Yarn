package net.minecraft;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;

public interface class_1922 {
	@Nullable
	class_2586 method_8321(class_2338 arg);

	class_2680 method_8320(class_2338 arg);

	class_3610 method_8316(class_2338 arg);

	default int method_8317(class_2338 arg) {
		return this.method_8320(arg).method_11630();
	}

	default int method_8315() {
		return 15;
	}

	default int method_8322() {
		return 256;
	}

	default class_3965 method_17742(class_3959 arg) {
		return method_17744(
			arg,
			(argx, arg2) -> {
				class_2680 lv = this.method_8320(arg2);
				class_3610 lv2 = this.method_8316(arg2);
				class_243 lv3 = argx.method_17750();
				class_243 lv4 = argx.method_17747();
				class_265 lv5 = argx.method_17748(lv, this, arg2);
				class_3965 lv6 = this.method_17745(lv3, lv4, arg2, lv5, lv);
				class_265 lv7 = argx.method_17749(lv2, this, arg2);
				class_3965 lv8 = lv7.method_1092(lv3, lv4, arg2);
				double d = lv6 == null ? Double.MAX_VALUE : argx.method_17750().method_1025(lv6.method_17784());
				double e = lv8 == null ? Double.MAX_VALUE : argx.method_17750().method_1025(lv8.method_17784());
				return d <= e ? lv6 : lv8;
			},
			argx -> {
				class_243 lv = argx.method_17750().method_1020(argx.method_17747());
				return class_3965.method_17778(
					argx.method_17747(), class_2350.method_10142(lv.field_1352, lv.field_1351, lv.field_1350), new class_2338(argx.method_17747())
				);
			}
		);
	}

	@Nullable
	default class_3965 method_17745(class_243 arg, class_243 arg2, class_2338 arg3, class_265 arg4, class_2680 arg5) {
		class_3965 lv = arg4.method_1092(arg, arg2, arg3);
		if (lv != null) {
			class_3965 lv2 = arg5.method_11607(this, arg3).method_1092(arg, arg2, arg3);
			if (lv2 != null && lv2.method_17784().method_1020(arg).method_1027() < lv.method_17784().method_1020(arg).method_1027()) {
				return lv.method_17779(lv2.method_17780());
			}
		}

		return lv;
	}

	static <T> T method_17744(class_3959 arg, BiFunction<class_3959, class_2338, T> biFunction, Function<class_3959, T> function) {
		class_243 lv = arg.method_17750();
		class_243 lv2 = arg.method_17747();
		if (lv.equals(lv2)) {
			return (T)function.apply(arg);
		} else {
			double d = class_3532.method_16436(-1.0E-7, lv2.field_1352, lv.field_1352);
			double e = class_3532.method_16436(-1.0E-7, lv2.field_1351, lv.field_1351);
			double f = class_3532.method_16436(-1.0E-7, lv2.field_1350, lv.field_1350);
			double g = class_3532.method_16436(-1.0E-7, lv.field_1352, lv2.field_1352);
			double h = class_3532.method_16436(-1.0E-7, lv.field_1351, lv2.field_1351);
			double i = class_3532.method_16436(-1.0E-7, lv.field_1350, lv2.field_1350);
			int j = class_3532.method_15357(g);
			int k = class_3532.method_15357(h);
			int l = class_3532.method_15357(i);
			class_2338.class_2339 lv3 = new class_2338.class_2339(j, k, l);
			T object = (T)biFunction.apply(arg, lv3);
			if (object != null) {
				return object;
			} else {
				double m = d - g;
				double n = e - h;
				double o = f - i;
				int p = class_3532.method_17822(m);
				int q = class_3532.method_17822(n);
				int r = class_3532.method_17822(o);
				double s = p == 0 ? Double.MAX_VALUE : (double)p / m;
				double t = q == 0 ? Double.MAX_VALUE : (double)q / n;
				double u = r == 0 ? Double.MAX_VALUE : (double)r / o;
				double v = s * (p > 0 ? 1.0 - class_3532.method_15385(g) : class_3532.method_15385(g));
				double w = t * (q > 0 ? 1.0 - class_3532.method_15385(h) : class_3532.method_15385(h));
				double x = u * (r > 0 ? 1.0 - class_3532.method_15385(i) : class_3532.method_15385(i));

				while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
					if (v < w) {
						if (v < x) {
							j += p;
							v += s;
						} else {
							l += r;
							x += u;
						}
					} else if (w < x) {
						k += q;
						w += t;
					} else {
						l += r;
						x += u;
					}

					T object2 = (T)biFunction.apply(arg, lv3.method_10103(j, k, l));
					if (object2 != null) {
						return object2;
					}
				}

				return (T)function.apply(arg);
			}
		}
	}
}
