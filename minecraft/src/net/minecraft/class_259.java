package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_259 {
	private static final class_265 field_1384 = new class_245(
		new class_244(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0})
	);
	private static final class_265 field_1385 = class_156.method_656(() -> {
		class_251 lv = new class_244(1, 1, 1);
		lv.method_1049(0, 0, 0, true, true);
		return new class_249(lv);
	});

	public static class_265 method_1073() {
		return field_1384;
	}

	public static class_265 method_1077() {
		return field_1385;
	}

	public static class_265 method_1081(double d, double e, double f, double g, double h, double i) {
		return method_1078(new class_238(d, e, f, g, h, i));
	}

	public static class_265 method_1078(class_238 arg) {
		int i = method_1086(arg.field_1323, arg.field_1320);
		int j = method_1086(arg.field_1322, arg.field_1325);
		int k = method_1086(arg.field_1321, arg.field_1324);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return arg.method_1008(0.5, 0.5, 0.5) ? method_1077() : method_1073();
			} else {
				int l = 1 << i;
				int m = 1 << j;
				int n = 1 << k;
				int o = (int)Math.round(arg.field_1323 * (double)l);
				int p = (int)Math.round(arg.field_1320 * (double)l);
				int q = (int)Math.round(arg.field_1322 * (double)m);
				int r = (int)Math.round(arg.field_1325 * (double)m);
				int s = (int)Math.round(arg.field_1321 * (double)n);
				int t = (int)Math.round(arg.field_1324 * (double)n);
				class_244 lv = new class_244(l, m, n, o, q, s, p, r, t);

				for (long u = (long)o; u < (long)p; u++) {
					for (long v = (long)q; v < (long)r; v++) {
						for (long w = (long)s; w < (long)t; w++) {
							lv.method_1049((int)u, (int)v, (int)w, false, true);
						}
					}
				}

				return new class_249(lv);
			}
		} else {
			return new class_245(
				field_1385.field_1401,
				new double[]{arg.field_1323, arg.field_1320},
				new double[]{arg.field_1322, arg.field_1325},
				new double[]{arg.field_1321, arg.field_1324}
			);
		}
	}

	private static int method_1086(double d, double e) {
		if (!(d < -1.0E-7) && !(e > 1.0000001)) {
			for (int i = 0; i <= 3; i++) {
				double f = d * (double)(1 << i);
				double g = e * (double)(1 << i);
				boolean bl = Math.abs(f - Math.floor(f)) < 1.0E-7;
				boolean bl2 = Math.abs(g - Math.floor(g)) < 1.0E-7;
				if (bl && bl2) {
					return i;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	protected static long method_1079(int i, int j) {
		return (long)i * (long)(j / IntMath.gcd(i, j));
	}

	public static class_265 method_1084(class_265 arg, class_265 arg2) {
		return method_1072(arg, arg2, class_247.field_1366);
	}

	public static class_265 method_1072(class_265 arg, class_265 arg2, class_247 arg3) {
		return method_1082(arg, arg2, arg3).method_1097();
	}

	public static class_265 method_1082(class_265 arg, class_265 arg2, class_247 arg3) {
		if (arg3.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (arg == arg2) {
			return arg3.apply(true, true) ? arg : method_1073();
		} else {
			boolean bl = arg3.apply(true, false);
			boolean bl2 = arg3.apply(false, true);
			if (arg.method_1110()) {
				return bl2 ? arg2 : method_1073();
			} else if (arg2.method_1110()) {
				return bl ? arg : method_1073();
			} else {
				class_255 lv = method_1069(1, arg.method_1109(class_2350.class_2351.field_11048), arg2.method_1109(class_2350.class_2351.field_11048), bl, bl2);
				class_255 lv2 = method_1069(
					lv.method_1066().size() - 1, arg.method_1109(class_2350.class_2351.field_11052), arg2.method_1109(class_2350.class_2351.field_11052), bl, bl2
				);
				class_255 lv3 = method_1069(
					(lv.method_1066().size() - 1) * (lv2.method_1066().size() - 1),
					arg.method_1109(class_2350.class_2351.field_11051),
					arg2.method_1109(class_2350.class_2351.field_11051),
					bl,
					bl2
				);
				class_244 lv4 = class_244.method_1040(arg.field_1401, arg2.field_1401, lv, lv2, lv3, arg3);
				return (class_265)(lv instanceof class_248 && lv2 instanceof class_248 && lv3 instanceof class_248
					? new class_249(lv4)
					: new class_245(lv4, lv.method_1066(), lv2.method_1066(), lv3.method_1066()));
			}
		}
	}

	public static boolean method_1074(class_265 arg, class_265 arg2, class_247 arg3) {
		if (arg3.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (arg == arg2) {
			return arg3.apply(true, true);
		} else if (arg.method_1110()) {
			return arg3.apply(false, !arg2.method_1110());
		} else if (arg2.method_1110()) {
			return arg3.apply(!arg.method_1110(), false);
		} else {
			boolean bl = arg3.apply(true, false);
			boolean bl2 = arg3.apply(false, true);

			for (class_2350.class_2351 lv : class_2335.field_10961) {
				if (arg.method_1105(lv) < arg2.method_1091(lv) - 1.0E-7) {
					return bl || bl2;
				}

				if (arg2.method_1105(lv) < arg.method_1091(lv) - 1.0E-7) {
					return bl || bl2;
				}
			}

			class_255 lv2 = method_1069(1, arg.method_1109(class_2350.class_2351.field_11048), arg2.method_1109(class_2350.class_2351.field_11048), bl, bl2);
			class_255 lv3 = method_1069(
				lv2.method_1066().size() - 1, arg.method_1109(class_2350.class_2351.field_11052), arg2.method_1109(class_2350.class_2351.field_11052), bl, bl2
			);
			class_255 lv4 = method_1069(
				(lv2.method_1066().size() - 1) * (lv3.method_1066().size() - 1),
				arg.method_1109(class_2350.class_2351.field_11051),
				arg2.method_1109(class_2350.class_2351.field_11051),
				bl,
				bl2
			);
			return method_1071(lv2, lv3, lv4, arg.field_1401, arg2.field_1401, arg3);
		}
	}

	private static boolean method_1071(class_255 arg, class_255 arg2, class_255 arg3, class_251 arg4, class_251 arg5, class_247 arg6) {
		return !arg.method_1065(
			(i, j, k) -> arg2.method_1065((kx, l, m) -> arg3.method_1065((mx, n, o) -> !arg6.apply(arg4.method_1044(i, kx, mx), arg5.method_1044(j, l, n))))
		);
	}

	public static double method_1085(class_2350.class_2351 arg, class_238 arg2, Stream<class_265> stream, double d) {
		Iterator<class_265> iterator = stream.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((class_265)iterator.next()).method_1108(arg, arg2, d);
		}

		return d;
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_1083(class_265 arg, class_265 arg2, class_2350 arg3) {
		if (arg == method_1077() && arg2 == method_1077()) {
			return true;
		} else if (arg2.method_1110()) {
			return false;
		} else {
			class_2350.class_2351 lv = arg3.method_10166();
			class_2350.class_2352 lv2 = arg3.method_10171();
			class_265 lv3 = lv2 == class_2350.class_2352.field_11056 ? arg : arg2;
			class_265 lv4 = lv2 == class_2350.class_2352.field_11056 ? arg2 : arg;
			class_247 lv5 = lv2 == class_2350.class_2352.field_11056 ? class_247.field_16886 : class_247.field_16893;
			return DoubleMath.fuzzyEquals(lv3.method_1105(lv), 1.0, 1.0E-7)
				&& DoubleMath.fuzzyEquals(lv4.method_1091(lv), 0.0, 1.0E-7)
				&& !method_1074(new class_263(lv3, lv, lv3.field_1401.method_1051(lv) - 1), new class_263(lv4, lv, 0), lv5);
		}
	}

	public static class_265 method_16344(class_265 arg, class_2350 arg2) {
		class_2350.class_2351 lv = arg2.method_10166();
		boolean bl;
		int i;
		if (arg2.method_10171() == class_2350.class_2352.field_11056) {
			bl = DoubleMath.fuzzyEquals(arg.method_1105(lv), 1.0, 1.0E-7);
			i = arg.field_1401.method_1051(lv) - 1;
		} else {
			bl = DoubleMath.fuzzyEquals(arg.method_1091(lv), 0.0, 1.0E-7);
			i = 0;
		}

		return (class_265)(!bl ? method_1073() : new class_263(arg, lv, i));
	}

	public static boolean method_1080(class_265 arg, class_265 arg2, class_2350 arg3) {
		if (arg != method_1077() && arg2 != method_1077()) {
			class_2350.class_2351 lv = arg3.method_10166();
			class_2350.class_2352 lv2 = arg3.method_10171();
			class_265 lv3 = lv2 == class_2350.class_2352.field_11056 ? arg : arg2;
			class_265 lv4 = lv2 == class_2350.class_2352.field_11056 ? arg2 : arg;
			if (!DoubleMath.fuzzyEquals(lv3.method_1105(lv), 1.0, 1.0E-7)) {
				lv3 = method_1073();
			}

			if (!DoubleMath.fuzzyEquals(lv4.method_1091(lv), 0.0, 1.0E-7)) {
				lv4 = method_1073();
			}

			return !method_1074(
				method_1077(),
				method_1082(new class_263(lv3, lv, lv3.field_1401.method_1051(lv) - 1), new class_263(lv4, lv, 0), class_247.field_1366),
				class_247.field_16886
			);
		} else {
			return true;
		}
	}

	@VisibleForTesting
	protected static class_255 method_1069(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int j = doubleList.size() - 1;
		int k = doubleList2.size() - 1;
		if (doubleList instanceof class_246 && doubleList2 instanceof class_246) {
			long l = method_1079(j, k);
			if ((long)i * l <= 256L) {
				return new class_248(j, k);
			}
		}

		if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
			return new class_257(doubleList, doubleList2, false);
		} else if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
			return new class_257(doubleList2, doubleList, true);
		} else if (j != k || !Objects.equals(doubleList, doubleList2)) {
			return new class_254(doubleList, doubleList2, bl, bl2);
		} else if (doubleList instanceof class_250) {
			return (class_255)doubleList;
		} else {
			return (class_255)(doubleList2 instanceof class_250 ? (class_255)doubleList2 : new class_250(doubleList));
		}
	}

	public interface class_260 {
		void consume(double d, double e, double f, double g, double h, double i);
	}
}
