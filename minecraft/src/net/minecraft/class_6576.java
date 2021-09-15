package net.minecraft;

import java.lang.runtime.ObjectMethods;

public final class class_6576 extends Record {
	private final double comp_77;
	private final double comp_78;
	private final double comp_79;

	public class_6576(double d, double e, double f) {
		this.comp_77 = d;
		this.comp_78 = e;
		this.comp_79 = f;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",class_6576,"offset;factor;peaks",class_6576::comp_77,class_6576::comp_78,class_6576::comp_79>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",class_6576,"offset;factor;peaks",class_6576::comp_77,class_6576::comp_78,class_6576::comp_79>(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",class_6576,"offset;factor;peaks",class_6576::comp_77,class_6576::comp_78,class_6576::comp_79>(this, object);
	}

	public double comp_77() {
		return this.comp_77;
	}

	public double comp_78() {
		return this.comp_78;
	}

	public double comp_79() {
		return this.comp_79;
	}
}
