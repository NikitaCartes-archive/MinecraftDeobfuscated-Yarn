package net.minecraft;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_238 {
	public final double field_1323;
	public final double field_1322;
	public final double field_1321;
	public final double field_1320;
	public final double field_1325;
	public final double field_1324;

	public class_238(double d, double e, double f, double g, double h, double i) {
		this.field_1323 = Math.min(d, g);
		this.field_1322 = Math.min(e, h);
		this.field_1321 = Math.min(f, i);
		this.field_1320 = Math.max(d, g);
		this.field_1325 = Math.max(e, h);
		this.field_1324 = Math.max(f, i);
	}

	public class_238(class_2338 arg) {
		this(
			(double)arg.method_10263(),
			(double)arg.method_10264(),
			(double)arg.method_10260(),
			(double)(arg.method_10263() + 1),
			(double)(arg.method_10264() + 1),
			(double)(arg.method_10260() + 1)
		);
	}

	public class_238(class_2338 arg, class_2338 arg2) {
		this(
			(double)arg.method_10263(),
			(double)arg.method_10264(),
			(double)arg.method_10260(),
			(double)arg2.method_10263(),
			(double)arg2.method_10264(),
			(double)arg2.method_10260()
		);
	}

	public class_238(class_243 arg, class_243 arg2) {
		this(arg.field_1352, arg.field_1351, arg.field_1350, arg2.field_1352, arg2.field_1351, arg2.field_1350);
	}

	public static class_238 method_19316(class_3341 arg) {
		return new class_238(
			(double)arg.field_14381,
			(double)arg.field_14380,
			(double)arg.field_14379,
			(double)(arg.field_14378 + 1),
			(double)(arg.field_14377 + 1),
			(double)(arg.field_14376 + 1)
		);
	}

	public double method_1001(class_2350.class_2351 arg) {
		return arg.method_10172(this.field_1323, this.field_1322, this.field_1321);
	}

	public double method_990(class_2350.class_2351 arg) {
		return arg.method_10172(this.field_1320, this.field_1325, this.field_1324);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_238)) {
			return false;
		} else {
			class_238 lv = (class_238)object;
			if (Double.compare(lv.field_1323, this.field_1323) != 0) {
				return false;
			} else if (Double.compare(lv.field_1322, this.field_1322) != 0) {
				return false;
			} else if (Double.compare(lv.field_1321, this.field_1321) != 0) {
				return false;
			} else if (Double.compare(lv.field_1320, this.field_1320) != 0) {
				return false;
			} else {
				return Double.compare(lv.field_1325, this.field_1325) != 0 ? false : Double.compare(lv.field_1324, this.field_1324) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.field_1323);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1322);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1321);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1320);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1325);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1324);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public class_238 method_1002(double d, double e, double f) {
		double g = this.field_1323;
		double h = this.field_1322;
		double i = this.field_1321;
		double j = this.field_1320;
		double k = this.field_1325;
		double l = this.field_1324;
		if (d < 0.0) {
			g -= d;
		} else if (d > 0.0) {
			j -= d;
		}

		if (e < 0.0) {
			h -= e;
		} else if (e > 0.0) {
			k -= e;
		}

		if (f < 0.0) {
			i -= f;
		} else if (f > 0.0) {
			l -= f;
		}

		return new class_238(g, h, i, j, k, l);
	}

	public class_238 method_18804(class_243 arg) {
		return this.method_1012(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public class_238 method_1012(double d, double e, double f) {
		double g = this.field_1323;
		double h = this.field_1322;
		double i = this.field_1321;
		double j = this.field_1320;
		double k = this.field_1325;
		double l = this.field_1324;
		if (d < 0.0) {
			g += d;
		} else if (d > 0.0) {
			j += d;
		}

		if (e < 0.0) {
			h += e;
		} else if (e > 0.0) {
			k += e;
		}

		if (f < 0.0) {
			i += f;
		} else if (f > 0.0) {
			l += f;
		}

		return new class_238(g, h, i, j, k, l);
	}

	public class_238 method_1009(double d, double e, double f) {
		double g = this.field_1323 - d;
		double h = this.field_1322 - e;
		double i = this.field_1321 - f;
		double j = this.field_1320 + d;
		double k = this.field_1325 + e;
		double l = this.field_1324 + f;
		return new class_238(g, h, i, j, k, l);
	}

	public class_238 method_1014(double d) {
		return this.method_1009(d, d, d);
	}

	public class_238 method_999(class_238 arg) {
		double d = Math.max(this.field_1323, arg.field_1323);
		double e = Math.max(this.field_1322, arg.field_1322);
		double f = Math.max(this.field_1321, arg.field_1321);
		double g = Math.min(this.field_1320, arg.field_1320);
		double h = Math.min(this.field_1325, arg.field_1325);
		double i = Math.min(this.field_1324, arg.field_1324);
		return new class_238(d, e, f, g, h, i);
	}

	public class_238 method_991(class_238 arg) {
		double d = Math.min(this.field_1323, arg.field_1323);
		double e = Math.min(this.field_1322, arg.field_1322);
		double f = Math.min(this.field_1321, arg.field_1321);
		double g = Math.max(this.field_1320, arg.field_1320);
		double h = Math.max(this.field_1325, arg.field_1325);
		double i = Math.max(this.field_1324, arg.field_1324);
		return new class_238(d, e, f, g, h, i);
	}

	public class_238 method_989(double d, double e, double f) {
		return new class_238(this.field_1323 + d, this.field_1322 + e, this.field_1321 + f, this.field_1320 + d, this.field_1325 + e, this.field_1324 + f);
	}

	public class_238 method_996(class_2338 arg) {
		return new class_238(
			this.field_1323 + (double)arg.method_10263(),
			this.field_1322 + (double)arg.method_10264(),
			this.field_1321 + (double)arg.method_10260(),
			this.field_1320 + (double)arg.method_10263(),
			this.field_1325 + (double)arg.method_10264(),
			this.field_1324 + (double)arg.method_10260()
		);
	}

	public class_238 method_997(class_243 arg) {
		return this.method_989(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public boolean method_994(class_238 arg) {
		return this.method_1003(arg.field_1323, arg.field_1322, arg.field_1321, arg.field_1320, arg.field_1325, arg.field_1324);
	}

	public boolean method_1003(double d, double e, double f, double g, double h, double i) {
		return this.field_1323 < g && this.field_1320 > d && this.field_1322 < h && this.field_1325 > e && this.field_1321 < i && this.field_1324 > f;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_993(class_243 arg, class_243 arg2) {
		return this.method_1003(
			Math.min(arg.field_1352, arg2.field_1352),
			Math.min(arg.field_1351, arg2.field_1351),
			Math.min(arg.field_1350, arg2.field_1350),
			Math.max(arg.field_1352, arg2.field_1352),
			Math.max(arg.field_1351, arg2.field_1351),
			Math.max(arg.field_1350, arg2.field_1350)
		);
	}

	public boolean method_1006(class_243 arg) {
		return this.method_1008(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public boolean method_1008(double d, double e, double f) {
		return d >= this.field_1323 && d < this.field_1320 && e >= this.field_1322 && e < this.field_1325 && f >= this.field_1321 && f < this.field_1324;
	}

	public double method_995() {
		double d = this.method_17939();
		double e = this.method_17940();
		double f = this.method_17941();
		return (d + e + f) / 3.0;
	}

	public double method_17939() {
		return this.field_1320 - this.field_1323;
	}

	public double method_17940() {
		return this.field_1325 - this.field_1322;
	}

	public double method_17941() {
		return this.field_1324 - this.field_1321;
	}

	public class_238 method_1011(double d) {
		return this.method_1014(-d);
	}

	public Optional<class_243> method_992(class_243 arg, class_243 arg2) {
		double[] ds = new double[]{1.0};
		double d = arg2.field_1352 - arg.field_1352;
		double e = arg2.field_1351 - arg.field_1351;
		double f = arg2.field_1350 - arg.field_1350;
		class_2350 lv = method_1007(this, arg, ds, null, d, e, f);
		if (lv == null) {
			return Optional.empty();
		} else {
			double g = ds[0];
			return Optional.of(arg.method_1031(g * d, g * e, g * f));
		}
	}

	@Nullable
	public static class_3965 method_1010(Iterable<class_238> iterable, class_243 arg, class_243 arg2, class_2338 arg3) {
		double[] ds = new double[]{1.0};
		class_2350 lv = null;
		double d = arg2.field_1352 - arg.field_1352;
		double e = arg2.field_1351 - arg.field_1351;
		double f = arg2.field_1350 - arg.field_1350;

		for (class_238 lv2 : iterable) {
			lv = method_1007(lv2.method_996(arg3), arg, ds, lv, d, e, f);
		}

		if (lv == null) {
			return null;
		} else {
			double g = ds[0];
			return new class_3965(arg.method_1031(g * d, g * e, g * f), lv, arg3, false);
		}
	}

	@Nullable
	private static class_2350 method_1007(class_238 arg, class_243 arg2, double[] ds, @Nullable class_2350 arg3, double d, double e, double f) {
		if (d > 1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				d,
				e,
				f,
				arg.field_1323,
				arg.field_1322,
				arg.field_1325,
				arg.field_1321,
				arg.field_1324,
				class_2350.field_11039,
				arg2.field_1352,
				arg2.field_1351,
				arg2.field_1350
			);
		} else if (d < -1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				d,
				e,
				f,
				arg.field_1320,
				arg.field_1322,
				arg.field_1325,
				arg.field_1321,
				arg.field_1324,
				class_2350.field_11034,
				arg2.field_1352,
				arg2.field_1351,
				arg2.field_1350
			);
		}

		if (e > 1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				e,
				f,
				d,
				arg.field_1322,
				arg.field_1321,
				arg.field_1324,
				arg.field_1323,
				arg.field_1320,
				class_2350.field_11033,
				arg2.field_1351,
				arg2.field_1350,
				arg2.field_1352
			);
		} else if (e < -1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				e,
				f,
				d,
				arg.field_1325,
				arg.field_1321,
				arg.field_1324,
				arg.field_1323,
				arg.field_1320,
				class_2350.field_11036,
				arg2.field_1351,
				arg2.field_1350,
				arg2.field_1352
			);
		}

		if (f > 1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				f,
				d,
				e,
				arg.field_1321,
				arg.field_1323,
				arg.field_1320,
				arg.field_1322,
				arg.field_1325,
				class_2350.field_11043,
				arg2.field_1350,
				arg2.field_1352,
				arg2.field_1351
			);
		} else if (f < -1.0E-7) {
			arg3 = method_998(
				ds,
				arg3,
				f,
				d,
				e,
				arg.field_1324,
				arg.field_1323,
				arg.field_1320,
				arg.field_1322,
				arg.field_1325,
				class_2350.field_11035,
				arg2.field_1350,
				arg2.field_1352,
				arg2.field_1351
			);
		}

		return arg3;
	}

	@Nullable
	private static class_2350 method_998(
		double[] ds,
		@Nullable class_2350 arg,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		double j,
		double k,
		class_2350 arg2,
		double l,
		double m,
		double n
	) {
		double o = (g - l) / d;
		double p = m + o * e;
		double q = n + o * f;
		if (0.0 < o && o < ds[0] && h - 1.0E-7 < p && p < i + 1.0E-7 && j - 1.0E-7 < q && q < k + 1.0E-7) {
			ds[0] = o;
			return arg2;
		} else {
			return arg;
		}
	}

	public String toString() {
		return "box["
			+ this.field_1323
			+ ", "
			+ this.field_1322
			+ ", "
			+ this.field_1321
			+ "] -> ["
			+ this.field_1320
			+ ", "
			+ this.field_1325
			+ ", "
			+ this.field_1324
			+ "]";
	}

	@Environment(EnvType.CLIENT)
	public boolean method_1013() {
		return Double.isNaN(this.field_1323)
			|| Double.isNaN(this.field_1322)
			|| Double.isNaN(this.field_1321)
			|| Double.isNaN(this.field_1320)
			|| Double.isNaN(this.field_1325)
			|| Double.isNaN(this.field_1324);
	}

	public class_243 method_1005() {
		return new class_243(
			class_3532.method_16436(0.5, this.field_1323, this.field_1320),
			class_3532.method_16436(0.5, this.field_1322, this.field_1325),
			class_3532.method_16436(0.5, this.field_1321, this.field_1324)
		);
	}
}
