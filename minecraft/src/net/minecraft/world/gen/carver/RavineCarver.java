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

	public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig probabilityConfig) {
		return random.nextFloat() <= probabilityConfig.probability;
	}

	public boolean carve(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig) {
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
		Chunk chunk,
		long seed,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		double x,
		double y,
		double z,
		float baseWidth,
		float xzAngle,
		float yAngle,
		int branch,
		int branchCount,
		double heightWidthRatio,
		BitSet mask
	) {
		Random random = new Random(seed);
		float f = 1.0F;

		for (int i = 0; i < 256; i++) {
			if (i == 0 || random.nextInt(3) == 0) {
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.heightToHorizontalStretchFactor[i] = f * f;
		}

		float g = 0.0F;
		float h = 0.0F;

		for (int j = branch; j < branchCount; j++) {
			double d = 1.5 + (double)(MathHelper.sin((float)j * (float) Math.PI / (float)branchCount) * baseWidth);
			double e = d * heightWidthRatio;
			d *= (double)random.nextFloat() * 0.25 + 0.75;
			e *= (double)random.nextFloat() * 0.25 + 0.75;
			float k = MathHelper.cos(yAngle);
			float l = MathHelper.sin(yAngle);
			x += (double)(MathHelper.cos(xzAngle) * k);
			y += (double)l;
			z += (double)(MathHelper.sin(xzAngle) * k);
			yAngle *= 0.7F;
			yAngle += h * 0.05F;
			xzAngle += g * 0.05F;
			h *= 0.8F;
			g *= 0.5F;
			h += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, j, branchCount, baseWidth)) {
					return;
				}

				this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, mask);
			}
		}
	}

	@Override
	protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
		return (scaledRelativeX * scaledRelativeX + scaledRelativeZ * scaledRelativeZ) * (double)this.heightToHorizontalStretchFactor[y - 1]
				+ scaledRelativeY * scaledRelativeY / 6.0
			>= 1.0;
	}
}
