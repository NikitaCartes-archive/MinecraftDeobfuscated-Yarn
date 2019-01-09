package net.minecraft;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_243 {
	public static final class_243 field_1353 = new class_243(0.0, 0.0, 0.0);
	public final double field_1352;
	public final double field_1351;
	public final double field_1350;

	public class_243(double d, double e, double f) {
		this.field_1352 = d;
		this.field_1351 = e;
		this.field_1350 = f;
	}

	public class_243(class_2382 arg) {
		this((double)arg.method_10263(), (double)arg.method_10264(), (double)arg.method_10260());
	}

	public class_243 method_1035(class_243 arg) {
		return new class_243(arg.field_1352 - this.field_1352, arg.field_1351 - this.field_1351, arg.field_1350 - this.field_1350);
	}

	public class_243 method_1029() {
		double d = (double)class_3532.method_15368(this.field_1352 * this.field_1352 + this.field_1351 * this.field_1351 + this.field_1350 * this.field_1350);
		return d < 1.0E-4 ? field_1353 : new class_243(this.field_1352 / d, this.field_1351 / d, this.field_1350 / d);
	}

	public double method_1026(class_243 arg) {
		return this.field_1352 * arg.field_1352 + this.field_1351 * arg.field_1351 + this.field_1350 * arg.field_1350;
	}

	public class_243 method_1036(class_243 arg) {
		return new class_243(
			this.field_1351 * arg.field_1350 - this.field_1350 * arg.field_1351,
			this.field_1350 * arg.field_1352 - this.field_1352 * arg.field_1350,
			this.field_1352 * arg.field_1351 - this.field_1351 * arg.field_1352
		);
	}

	public class_243 method_1020(class_243 arg) {
		return this.method_1023(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public class_243 method_1023(double d, double e, double f) {
		return this.method_1031(-d, -e, -f);
	}

	public class_243 method_1019(class_243 arg) {
		return this.method_1031(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public class_243 method_1031(double d, double e, double f) {
		return new class_243(this.field_1352 + d, this.field_1351 + e, this.field_1350 + f);
	}

	public double method_1022(class_243 arg) {
		double d = arg.field_1352 - this.field_1352;
		double e = arg.field_1351 - this.field_1351;
		double f = arg.field_1350 - this.field_1350;
		return (double)class_3532.method_15368(d * d + e * e + f * f);
	}

	public double method_1025(class_243 arg) {
		double d = arg.field_1352 - this.field_1352;
		double e = arg.field_1351 - this.field_1351;
		double f = arg.field_1350 - this.field_1350;
		return d * d + e * e + f * f;
	}

	public double method_1028(double d, double e, double f) {
		double g = d - this.field_1352;
		double h = e - this.field_1351;
		double i = f - this.field_1350;
		return g * g + h * h + i * i;
	}

	public class_243 method_1021(double d) {
		return new class_243(this.field_1352 * d, this.field_1351 * d, this.field_1350 * d);
	}

	public double method_1033() {
		return (double)class_3532.method_15368(this.field_1352 * this.field_1352 + this.field_1351 * this.field_1351 + this.field_1350 * this.field_1350);
	}

	public double method_1027() {
		return this.field_1352 * this.field_1352 + this.field_1351 * this.field_1351 + this.field_1350 * this.field_1350;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_243)) {
			return false;
		} else {
			class_243 lv = (class_243)object;
			if (Double.compare(lv.field_1352, this.field_1352) != 0) {
				return false;
			} else {
				return Double.compare(lv.field_1351, this.field_1351) != 0 ? false : Double.compare(lv.field_1350, this.field_1350) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.field_1352);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1351);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.field_1350);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public String toString() {
		return "(" + this.field_1352 + ", " + this.field_1351 + ", " + this.field_1350 + ")";
	}

	public class_243 method_1037(float f) {
		float g = class_3532.method_15362(f);
		float h = class_3532.method_15374(f);
		double d = this.field_1352;
		double e = this.field_1351 * (double)g + this.field_1350 * (double)h;
		double i = this.field_1350 * (double)g - this.field_1351 * (double)h;
		return new class_243(d, e, i);
	}

	public class_243 method_1024(float f) {
		float g = class_3532.method_15362(f);
		float h = class_3532.method_15374(f);
		double d = this.field_1352 * (double)g + this.field_1350 * (double)h;
		double e = this.field_1351;
		double i = this.field_1350 * (double)g - this.field_1352 * (double)h;
		return new class_243(d, e, i);
	}

	@Environment(EnvType.CLIENT)
	public static class_243 method_1034(class_241 arg) {
		return method_1030(arg.field_1343, arg.field_1342);
	}

	@Environment(EnvType.CLIENT)
	public static class_243 method_1030(float f, float g) {
		float h = class_3532.method_15362(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = class_3532.method_15374(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -class_3532.method_15362(-f * (float) (Math.PI / 180.0));
		float k = class_3532.method_15374(-f * (float) (Math.PI / 180.0));
		return new class_243((double)(i * j), (double)k, (double)(h * j));
	}

	public class_243 method_1032(EnumSet<class_2350.class_2351> enumSet) {
		double d = enumSet.contains(class_2350.class_2351.field_11048) ? (double)class_3532.method_15357(this.field_1352) : this.field_1352;
		double e = enumSet.contains(class_2350.class_2351.field_11052) ? (double)class_3532.method_15357(this.field_1351) : this.field_1351;
		double f = enumSet.contains(class_2350.class_2351.field_11051) ? (double)class_3532.method_15357(this.field_1350) : this.field_1350;
		return new class_243(d, e, f);
	}
}
