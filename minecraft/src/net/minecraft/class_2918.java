package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class class_2918 extends class_2939<class_3133> {
	private final float[] field_13275 = new float[1024];

	public class_2918(Function<Dynamic<?>, ? extends class_3133> function) {
		super(function, 256);
	}

	public boolean method_12658(Random random, int i, int j, class_3133 arg) {
		return random.nextFloat() <= arg.field_13738;
	}

	public boolean method_12656(class_2791 arg, Random random, int i, int j, int k, int l, int m, BitSet bitSet, class_3133 arg2) {
		int n = (this.method_12710() * 2 - 1) * 16;
		double d = (double)(j * 16 + random.nextInt(16));
		double e = (double)(random.nextInt(random.nextInt(40) + 8) + 20);
		double f = (double)(k * 16 + random.nextInt(16));
		float g = random.nextFloat() * (float) (Math.PI * 2);
		float h = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
		double o = 3.0;
		float p = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
		int q = n - random.nextInt(n / 4);
		int r = 0;
		this.method_12657(arg, random.nextLong(), i, l, m, d, e, f, p, g, h, 0, q, 3.0, bitSet);
		return true;
	}

	private void method_12657(
		class_2791 arg, long l, int i, int j, int k, double d, double e, double f, float g, float h, float m, int n, int o, double p, BitSet bitSet
	) {
		Random random = new Random(l);
		float q = 1.0F;

		for (int r = 0; r < 256; r++) {
			if (r == 0 || random.nextInt(3) == 0) {
				q = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.field_13275[r] = q * q;
		}

		float s = 0.0F;
		float t = 0.0F;

		for (int u = n; u < o; u++) {
			double v = 1.5 + (double)(class_3532.method_15374((float)u * (float) Math.PI / (float)o) * g);
			double w = v * p;
			v *= (double)random.nextFloat() * 0.25 + 0.75;
			w *= (double)random.nextFloat() * 0.25 + 0.75;
			float x = class_3532.method_15362(m);
			float y = class_3532.method_15374(m);
			d += (double)(class_3532.method_15362(h) * x);
			e += (double)y;
			f += (double)(class_3532.method_15374(h) * x);
			m *= 0.7F;
			m += t * 0.05F;
			h += s * 0.05F;
			t *= 0.8F;
			s *= 0.5F;
			t += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!this.method_12707(j, k, d, f, u, o, g)) {
					return;
				}

				this.method_16580(arg, l, i, j, k, d, e, f, v, w, bitSet);
			}
		}
	}

	@Override
	protected boolean method_16582(double d, double e, double f, int i) {
		return (d * d + f * f) * (double)this.field_13275[i - 1] + e * e / 6.0 >= 1.0;
	}
}
