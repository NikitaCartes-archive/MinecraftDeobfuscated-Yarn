package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class class_2925 extends class_2939<class_3133> {
	public class_2925(Function<Dynamic<?>, ? extends class_3133> function, int i) {
		super(function, i);
	}

	public boolean method_12676(Random random, int i, int j, class_3133 arg) {
		return random.nextFloat() <= arg.field_13738;
	}

	public boolean method_12673(class_2791 arg, Random random, int i, int j, int k, int l, int m, BitSet bitSet, class_3133 arg2) {
		int n = (this.method_12710() * 2 - 1) * 16;
		int o = random.nextInt(random.nextInt(random.nextInt(this.method_16577()) + 1) + 1);

		for (int p = 0; p < o; p++) {
			double d = (double)(j * 16 + random.nextInt(16));
			double e = (double)this.method_16579(random);
			double f = (double)(k * 16 + random.nextInt(16));
			int q = 1;
			if (random.nextInt(4) == 0) {
				double g = 0.5;
				float h = 1.0F + random.nextFloat() * 6.0F;
				this.method_12674(arg, random.nextLong(), i, l, m, d, e, f, h, 0.5, bitSet);
				q += random.nextInt(4);
			}

			for (int r = 0; r < q; r++) {
				float s = random.nextFloat() * (float) (Math.PI * 2);
				float h = (random.nextFloat() - 0.5F) / 4.0F;
				float t = this.method_16576(random);
				int u = n - random.nextInt(n / 4);
				int v = 0;
				this.method_12675(arg, random.nextLong(), i, l, m, d, e, f, t, s, h, 0, u, this.method_16578(), bitSet);
			}
		}

		return true;
	}

	protected int method_16577() {
		return 15;
	}

	protected float method_16576(Random random) {
		float f = random.nextFloat() * 2.0F + random.nextFloat();
		if (random.nextInt(10) == 0) {
			f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
		}

		return f;
	}

	protected double method_16578() {
		return 1.0;
	}

	protected int method_16579(Random random) {
		return random.nextInt(random.nextInt(120) + 8);
	}

	protected void method_12674(class_2791 arg, long l, int i, int j, int k, double d, double e, double f, float g, double h, BitSet bitSet) {
		double m = 1.5 + (double)(class_3532.method_15374((float) (Math.PI / 2)) * g);
		double n = m * h;
		this.method_16580(arg, l, i, j, k, d + 1.0, e, f, m, n, bitSet);
	}

	protected void method_12675(
		class_2791 arg, long l, int i, int j, int k, double d, double e, double f, float g, float h, float m, int n, int o, double p, BitSet bitSet
	) {
		Random random = new Random(l);
		int q = random.nextInt(o / 2) + o / 4;
		boolean bl = random.nextInt(6) == 0;
		float r = 0.0F;
		float s = 0.0F;

		for (int t = n; t < o; t++) {
			double u = 1.5 + (double)(class_3532.method_15374((float) Math.PI * (float)t / (float)o) * g);
			double v = u * p;
			float w = class_3532.method_15362(m);
			d += (double)(class_3532.method_15362(h) * w);
			e += (double)class_3532.method_15374(m);
			f += (double)(class_3532.method_15374(h) * w);
			m *= bl ? 0.92F : 0.7F;
			m += s * 0.1F;
			h += r * 0.1F;
			s *= 0.9F;
			r *= 0.75F;
			s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (t == q && g > 1.0F) {
				this.method_12675(arg, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5F + 0.5F, h - (float) (Math.PI / 2), m / 3.0F, t, o, 1.0, bitSet);
				this.method_12675(arg, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5F + 0.5F, h + (float) (Math.PI / 2), m / 3.0F, t, o, 1.0, bitSet);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!this.method_12707(j, k, d, f, t, o, g)) {
					return;
				}

				this.method_16580(arg, l, i, j, k, d, e, f, u, v, bitSet);
			}
		}
	}

	@Override
	protected boolean method_16582(double d, double e, double f, int i) {
		return e <= -0.7 || d * d + e * e + f * f >= 1.0;
	}
}
