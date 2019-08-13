package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmoothUtil {
	private double field_15760;
	private double field_15759;
	private double field_15758;

	public double smooth(double d, double e) {
		this.field_15760 += d;
		double f = this.field_15760 - this.field_15759;
		double g = MathHelper.lerp(0.5, this.field_15758, f);
		double h = Math.signum(f);
		if (h * f > h * this.field_15758) {
			f = g;
		}

		this.field_15758 = g;
		this.field_15759 += f * e;
		return f * e;
	}

	public void clear() {
		this.field_15760 = 0.0;
		this.field_15759 = 0.0;
		this.field_15758 = 0.0;
	}
}
