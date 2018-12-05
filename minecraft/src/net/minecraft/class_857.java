package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_857 {
	public float[][] field_4497 = new float[6][4];
	public float[] field_4498 = new float[16];
	public float[] field_4500 = new float[16];
	public float[] field_4499 = new float[16];

	private double method_3701(float[] fs, double d, double e, double f) {
		return (double)fs[0] * d + (double)fs[1] * e + (double)fs[2] * f + (double)fs[3];
	}

	public boolean method_3702(double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < 6; j++) {
			float[] fs = this.field_4497[j];
			if (!(this.method_3701(fs, d, e, f) > 0.0)
				&& !(this.method_3701(fs, g, e, f) > 0.0)
				&& !(this.method_3701(fs, d, h, f) > 0.0)
				&& !(this.method_3701(fs, g, h, f) > 0.0)
				&& !(this.method_3701(fs, d, e, i) > 0.0)
				&& !(this.method_3701(fs, g, e, i) > 0.0)
				&& !(this.method_3701(fs, d, h, i) > 0.0)
				&& !(this.method_3701(fs, g, h, i) > 0.0)) {
				return false;
			}
		}

		return true;
	}
}
