package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class CaveCarver extends Carver<ProbabilityConfig> {
	public CaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer, int heightLimit) {
		super(configDeserializer, heightLimit);
	}

	public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig probabilityConfig) {
		return random.nextFloat() <= probabilityConfig.probability;
	}

	public boolean carve(
		Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig
	) {
		int n = (this.getBranchFactor() * 2 - 1) * 16;
		int o = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);

		for (int p = 0; p < o; p++) {
			double d = (double)(j * 16 + random.nextInt(16));
			double e = (double)this.getCaveY(random);
			double f = (double)(k * 16 + random.nextInt(16));
			int q = 1;
			if (random.nextInt(4) == 0) {
				double g = 0.5;
				float h = 1.0F + random.nextFloat() * 6.0F;
				this.carveCave(chunk, function, random.nextLong(), i, l, m, d, e, f, h, 0.5, bitSet);
				q += random.nextInt(4);
			}

			for (int r = 0; r < q; r++) {
				float s = random.nextFloat() * (float) (Math.PI * 2);
				float h = (random.nextFloat() - 0.5F) / 4.0F;
				float t = this.getTunnelSystemWidth(random);
				int u = n - random.nextInt(n / 4);
				int v = 0;
				this.carveTunnels(chunk, function, random.nextLong(), i, l, m, d, e, f, t, s, h, 0, u, this.getTunnelSystemHeightWidthRatio(), bitSet);
			}
		}

		return true;
	}

	protected int getMaxCaveCount() {
		return 15;
	}

	protected float getTunnelSystemWidth(Random random) {
		float f = random.nextFloat() * 2.0F + random.nextFloat();
		if (random.nextInt(10) == 0) {
			f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
		}

		return f;
	}

	protected double getTunnelSystemHeightWidthRatio() {
		return 1.0;
	}

	protected int getCaveY(Random random) {
		return random.nextInt(random.nextInt(120) + 8);
	}

	protected void carveCave(
		Chunk chunk, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, float g, double h, BitSet bitSet
	) {
		double m = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * g);
		double n = m * h;
		this.carveRegion(chunk, function, l, i, j, k, d + 1.0, e, f, m, n, bitSet);
	}

	protected void carveTunnels(
		Chunk chunk,
		Function<BlockPos, Biome> function,
		long l,
		int i,
		int j,
		int k,
		double d,
		double e,
		double f,
		float g,
		float h,
		float m,
		int n,
		int o,
		double p,
		BitSet bitSet
	) {
		Random random = new Random(l);
		int q = random.nextInt(o / 2) + o / 4;
		boolean bl = random.nextInt(6) == 0;
		float r = 0.0F;
		float s = 0.0F;

		for (int t = n; t < o; t++) {
			double u = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)t / (float)o) * g);
			double v = u * p;
			float w = MathHelper.cos(m);
			d += (double)(MathHelper.cos(h) * w);
			e += (double)MathHelper.sin(m);
			f += (double)(MathHelper.sin(h) * w);
			m *= bl ? 0.92F : 0.7F;
			m += s * 0.1F;
			h += r * 0.1F;
			s *= 0.9F;
			r *= 0.75F;
			s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (t == q && g > 1.0F) {
				this.carveTunnels(
					chunk, function, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5F + 0.5F, h - (float) (Math.PI / 2), m / 3.0F, t, o, 1.0, bitSet
				);
				this.carveTunnels(
					chunk, function, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5F + 0.5F, h + (float) (Math.PI / 2), m / 3.0F, t, o, 1.0, bitSet
				);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(j, k, d, f, t, o, g)) {
					return;
				}

				this.carveRegion(chunk, function, l, i, j, k, d, e, f, u, v, bitSet);
			}
		}
	}

	@Override
	protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
		return scaledRelativeY <= -0.7 || scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
	}
}
