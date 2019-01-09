package net.minecraft;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class class_2382 implements Comparable<class_2382> {
	public static final class_2382 field_11176 = new class_2382(0, 0, 0);
	private final int field_11175;
	private final int field_11174;
	private final int field_11173;

	public class_2382(int i, int j, int k) {
		this.field_11175 = i;
		this.field_11174 = j;
		this.field_11173 = k;
	}

	public class_2382(double d, double e, double f) {
		this(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2382)) {
			return false;
		} else {
			class_2382 lv = (class_2382)object;
			if (this.method_10263() != lv.method_10263()) {
				return false;
			} else {
				return this.method_10264() != lv.method_10264() ? false : this.method_10260() == lv.method_10260();
			}
		}
	}

	public int hashCode() {
		return (this.method_10264() + this.method_10260() * 31) * 31 + this.method_10263();
	}

	public int method_10265(class_2382 arg) {
		if (this.method_10264() == arg.method_10264()) {
			return this.method_10260() == arg.method_10260() ? this.method_10263() - arg.method_10263() : this.method_10260() - arg.method_10260();
		} else {
			return this.method_10264() - arg.method_10264();
		}
	}

	public int method_10263() {
		return this.field_11175;
	}

	public int method_10264() {
		return this.field_11174;
	}

	public int method_10260() {
		return this.field_11173;
	}

	public class_2382 method_10259(class_2382 arg) {
		return new class_2382(
			this.method_10264() * arg.method_10260() - this.method_10260() * arg.method_10264(),
			this.method_10260() * arg.method_10263() - this.method_10263() * arg.method_10260(),
			this.method_10263() * arg.method_10264() - this.method_10264() * arg.method_10263()
		);
	}

	public double method_10266(int i, int j, int k) {
		double d = (double)(this.method_10263() - i);
		double e = (double)(this.method_10264() - j);
		double f = (double)(this.method_10260() - k);
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double method_10267(class_2382 arg) {
		return this.method_10266(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public double method_10261(double d, double e, double f) {
		double g = (double)this.method_10263() - d;
		double h = (double)this.method_10264() - e;
		double i = (double)this.method_10260() - f;
		return g * g + h * h + i * i;
	}

	public double method_10268(double d, double e, double f) {
		double g = (double)this.method_10263() + 0.5 - d;
		double h = (double)this.method_10264() + 0.5 - e;
		double i = (double)this.method_10260() + 0.5 - f;
		return g * g + h * h + i * i;
	}

	public double method_10262(class_2382 arg) {
		return this.method_10261((double)arg.method_10263(), (double)arg.method_10264(), (double)arg.method_10260());
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.method_10263()).add("y", this.method_10264()).add("z", this.method_10260()).toString();
	}
}
