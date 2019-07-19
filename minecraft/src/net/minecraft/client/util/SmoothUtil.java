package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmoothUtil {
	private double field_15760;
	private double field_15759;
	private double field_15758;

	public double smooth(double original, double smoother) {
		this.field_15760 += original;
		double d = this.field_15760 - this.field_15759;
		double e = MathHelper.lerp(0.5, this.field_15758, d);
		double f = Math.signum(d);
		if (f * d > f * this.field_15758) {
			d = e;
		}

		this.field_15758 = e;
		this.field_15759 += d * smoother;
		return d * smoother;
	}

	public void clear() {
		this.field_15760 = 0.0;
		this.field_15759 = 0.0;
		this.field_15758 = 0.0;
	}
}
