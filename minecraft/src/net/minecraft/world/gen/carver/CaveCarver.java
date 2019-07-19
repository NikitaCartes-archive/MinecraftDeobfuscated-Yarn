package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class CaveCarver extends Carver<ProbabilityConfig> {
	public CaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer, int heightLimit) {
		super(configDeserializer, heightLimit);
	}

	public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig probabilityConfig) {
		return random.nextFloat() <= probabilityConfig.probability;
	}

	public boolean carve(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig) {
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
				this.carveCave(chunk, random.nextLong(), i, l, m, d, e, f, h, 0.5, bitSet);
				q += random.nextInt(4);
			}

			for (int r = 0; r < q; r++) {
				float s = random.nextFloat() * (float) (Math.PI * 2);
				float h = (random.nextFloat() - 0.5F) / 4.0F;
				float t = this.getTunnelSystemWidth(random);
				int u = n - random.nextInt(n / 4);
				int v = 0;
				this.carveTunnels(chunk, random.nextLong(), i, l, m, d, e, f, t, s, h, 0, u, this.getTunnelSystemHeightWidthRatio(), bitSet);
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
		Chunk chunk, long seed, int seaLevel, int mainChunkX, int mainChunkZ, double x, double y, double z, float width, double heightWidthRatio, BitSet mask
	) {
		double d = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * width);
		double e = d * heightWidthRatio;
		this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x + 1.0, y, z, d, e, mask);
	}

	protected void carveTunnels(
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
		int currentBranch,
		int height,
		double heightWidthRatio,
		BitSet mask
	) {
		Random random = new Random(seed);
		int i = random.nextInt(height / 2) + height / 4;
		boolean bl = random.nextInt(6) == 0;
		float f = 0.0F;
		float g = 0.0F;

		for (int j = currentBranch; j < height; j++) {
			double d = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)j / (float)height) * baseWidth);
			double e = d * heightWidthRatio;
			float h = MathHelper.cos(yAngle);
			x += (double)(MathHelper.cos(xzAngle) * h);
			y += (double)MathHelper.sin(yAngle);
			z += (double)(MathHelper.sin(xzAngle) * h);
			yAngle *= bl ? 0.92F : 0.7F;
			yAngle += g * 0.1F;
			xzAngle += f * 0.1F;
			g *= 0.9F;
			f *= 0.75F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (j == i && baseWidth > 1.0F) {
				this.carveTunnels(
					chunk,
					random.nextLong(),
					seaLevel,
					mainChunkX,
					mainChunkZ,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					xzAngle - (float) (Math.PI / 2),
					yAngle / 3.0F,
					j,
					height,
					1.0,
					mask
				);
				this.carveTunnels(
					chunk,
					random.nextLong(),
					seaLevel,
					mainChunkX,
					mainChunkZ,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					xzAngle + (float) (Math.PI / 2),
					yAngle / 3.0F,
					j,
					height,
					1.0,
					mask
				);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, j, height, baseWidth)) {
					return;
				}

				this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, mask);
			}
		}
	}

	@Override
	protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
		return scaledRelativeY <= -0.7 || scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
	}
}
