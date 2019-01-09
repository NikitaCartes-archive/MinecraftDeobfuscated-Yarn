package net.minecraft;

import java.util.Random;

public final class class_3756 {
	private final byte[] field_16590;
	public final double field_16591;
	public final double field_16589;
	public final double field_16588;

	public class_3756(Random random) {
		this.field_16591 = random.nextDouble() * 256.0;
		this.field_16589 = random.nextDouble() * 256.0;
		this.field_16588 = random.nextDouble() * 256.0;
		this.field_16590 = new byte[256];

		for (int i = 0; i < 256; i++) {
			this.field_16590[i] = (byte)i;
		}

		for (int i = 0; i < 256; i++) {
			int j = random.nextInt(256 - i);
			byte b = this.field_16590[i];
			this.field_16590[i] = this.field_16590[i + j];
			this.field_16590[i + j] = b;
		}
	}

	public double method_16447(double d, double e, double f, double g, double h) {
		double i = d + this.field_16591;
		double j = e + this.field_16589;
		double k = f + this.field_16588;
		int l = class_3532.method_15357(i);
		int m = class_3532.method_15357(j);
		int n = class_3532.method_15357(k);
		double o = i - (double)l;
		double p = j - (double)m;
		double q = k - (double)n;
		double r = class_3532.method_16435(o);
		double s = class_3532.method_16435(p);
		double t = class_3532.method_16435(q);
		double v;
		if (g != 0.0) {
			double u = Math.min(h, p);
			v = (double)class_3532.method_15357(u / g) * g;
		} else {
			v = 0.0;
		}

		return this.method_16450(l, m, n, o, p - v, q, r, s, t);
	}

	private static double method_16448(int i, double d, double e, double f) {
		int j = i & 15;
		return class_3541.method_15431(class_3541.field_15766[j], d, e, f);
	}

	private int method_16449(int i) {
		return this.field_16590[i & 0xFF] & 0xFF;
	}

	public double method_16450(int i, int j, int k, double d, double e, double f, double g, double h, double l) {
		int m = this.method_16449(i) + j;
		int n = this.method_16449(m) + k;
		int o = this.method_16449(m + 1) + k;
		int p = this.method_16449(i + 1) + j;
		int q = this.method_16449(p) + k;
		int r = this.method_16449(p + 1) + k;
		double s = method_16448(this.method_16449(n), d, e, f);
		double t = method_16448(this.method_16449(q), d - 1.0, e, f);
		double u = method_16448(this.method_16449(o), d, e - 1.0, f);
		double v = method_16448(this.method_16449(r), d - 1.0, e - 1.0, f);
		double w = method_16448(this.method_16449(n + 1), d, e, f - 1.0);
		double x = method_16448(this.method_16449(q + 1), d - 1.0, e, f - 1.0);
		double y = method_16448(this.method_16449(o + 1), d, e - 1.0, f - 1.0);
		double z = method_16448(this.method_16449(r + 1), d - 1.0, e - 1.0, f - 1.0);
		return class_3532.method_16438(g, h, l, s, t, u, v, w, x, y, z);
	}
}
