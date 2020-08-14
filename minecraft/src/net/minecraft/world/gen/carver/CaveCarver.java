package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class CaveCarver extends Carver<ProbabilityConfig> {
	public CaveCarver(Codec<ProbabilityConfig> codec, int i) {
		super(codec, i);
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
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		double x,
		double y,
		double z,
		float yaw,
		double yawPitchRatio,
		BitSet carvingMask
	) {
		double d = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * yaw);
		double e = d * yawPitchRatio;
		this.carveRegion(chunk, posToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x + 1.0, y, z, d, e, carvingMask);
	}

	protected void carveTunnels(
		Chunk chunk,
		Function<BlockPos, Biome> postToBiome,
		long seed,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		double x,
		double y,
		double z,
		float width,
		float yaw,
		float pitch,
		int branchStartIndex,
		int branchCount,
		double yawPitchRatio,
		BitSet carvingMask
	) {
		Random random = new Random(seed);
		int i = random.nextInt(branchCount / 2) + branchCount / 4;
		boolean bl = random.nextInt(6) == 0;
		float f = 0.0F;
		float g = 0.0F;

		for (int j = branchStartIndex; j < branchCount; j++) {
			double d = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)j / (float)branchCount) * width);
			double e = d * yawPitchRatio;
			float h = MathHelper.cos(pitch);
			x += (double)(MathHelper.cos(yaw) * h);
			y += (double)MathHelper.sin(pitch);
			z += (double)(MathHelper.sin(yaw) * h);
			pitch *= bl ? 0.92F : 0.7F;
			pitch += g * 0.1F;
			yaw += f * 0.1F;
			g *= 0.9F;
			f *= 0.75F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (j == i && width > 1.0F) {
				this.carveTunnels(
					chunk,
					postToBiome,
					random.nextLong(),
					seaLevel,
					mainChunkX,
					mainChunkZ,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					yaw - (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					carvingMask
				);
				this.carveTunnels(
					chunk,
					postToBiome,
					random.nextLong(),
					seaLevel,
					mainChunkX,
					mainChunkZ,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					yaw + (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					carvingMask
				);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, j, branchCount, width)) {
					return;
				}

				this.carveRegion(chunk, postToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, carvingMask);
			}
		}
	}

	@Override
	protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
		return scaledRelativeY <= -0.7 || scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
	}
}
