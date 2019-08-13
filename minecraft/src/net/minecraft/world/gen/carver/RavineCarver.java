package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class RavineCarver extends Carver<ProbabilityConfig> {
	private final float[] heightToHorizontalStretchFactor = new float[1024];

	public RavineCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
		super(function, 256);
	}

	public boolean method_12658(Random random, int i, int j, ProbabilityConfig probabilityConfig) {
		return random.nextFloat() <= probabilityConfig.probability;
	}

	public boolean method_12656(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig) {
		int n = (this.getBranchFactor() * 2 - 1) * 16;
		double d = (double)(j * 16 + random.nextInt(16));
		double e = (double)(random.nextInt(random.nextInt(40) + 8) + 20);
		double f = (double)(k * 16 + random.nextInt(16));
		float g = random.nextFloat() * (float) (Math.PI * 2);
		float h = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
		double o = 3.0;
		float p = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
		int q = n - random.nextInt(n / 4);
		int r = 0;
		this.carveRavine(chunk, random.nextLong(), i, l, m, d, e, f, p, g, h, 0, q, 3.0, bitSet);
		return true;
	}

	private void carveRavine(
		Chunk chunk, long l, int i, int j, int k, double d, double e, double f, float g, float h, float m, int n, int o, double p, BitSet bitSet
	) {
		Random random = new Random(l);
		float q = 1.0F;

		for (int r = 0; r < 256; r++) {
			if (r == 0 || random.nextInt(3) == 0) {
				q = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.heightToHorizontalStretchFactor[r] = q * q;
		}

		float s = 0.0F;
		float t = 0.0F;

		for (int u = n; u < o; u++) {
			double v = 1.5 + (double)(MathHelper.sin((float)u * (float) Math.PI / (float)o) * g);
			double w = v * p;
			v *= (double)random.nextFloat() * 0.25 + 0.75;
			w *= (double)random.nextFloat() * 0.25 + 0.75;
			float x = MathHelper.cos(m);
			float y = MathHelper.sin(m);
			d += (double)(MathHelper.cos(h) * x);
			e += (double)y;
			f += (double)(MathHelper.sin(h) * x);
			m *= 0.7F;
			m += t * 0.05F;
			h += s * 0.05F;
			t *= 0.8F;
			s *= 0.5F;
			t += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(j, k, d, f, u, o, g)) {
					return;
				}

				this.carveRegion(chunk, l, i, j, k, d, e, f, v, w, bitSet);
			}
		}
	}

	@Override
	protected boolean isPositionExcluded(double d, double e, double f, int i) {
		return (d * d + f * f) * (double)this.heightToHorizontalStretchFactor[i - 1] + e * e / 6.0 >= 1.0;
	}
}
