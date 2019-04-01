package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3540 {
	private double field_15760;
	private double field_15759;
	private double field_15758;

	public double method_15429(double d, double e) {
		this.field_15760 += d;
		double f = this.field_15760 - this.field_15759;
		double g = class_3532.method_16436(0.5, this.field_15758, f);
		double h = Math.signum(f);
		if (h * f > h * this.field_15758) {
			f = g;
		}

		this.field_15758 = g;
		this.field_15759 += f * e;
		return f * e;
	}

	public void method_15428() {
		this.field_15760 = 0.0;
		this.field_15759 = 0.0;
		this.field_15758 = 0.0;
	}
}
