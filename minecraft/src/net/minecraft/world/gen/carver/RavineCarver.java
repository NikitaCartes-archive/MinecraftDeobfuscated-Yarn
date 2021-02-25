package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RavineCarver extends Carver<RavineCarverConfig> {
	private static final Logger LOGGER = LogManager.getLogger();

	public RavineCarver(Codec<RavineCarverConfig> codec) {
		super(codec);
	}

	public boolean shouldCarve(RavineCarverConfig ravineCarverConfig, Random random) {
		return random.nextFloat() <= ravineCarverConfig.probability;
	}

	public boolean carve(
		CarverContext carverContext,
		RavineCarverConfig ravineCarverConfig,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		Random random,
		int i,
		ChunkPos chunkPos,
		BitSet bitSet
	) {
		int j = (this.getBranchFactor() * 2 - 1) * 16;
		double d = (double)chunkPos.getOffsetX(random.nextInt(16));
		int k = this.getY(carverContext, ravineCarverConfig, random);
		double e = (double)chunkPos.getOffsetZ(random.nextInt(16));
		float f = random.nextFloat() * (float) (Math.PI * 2);
		float g = ravineCarverConfig.getVerticalRotation().get(random);
		double h = (double)ravineCarverConfig.getYScale().getValue(random);
		float l = ravineCarverConfig.getThickness().get(random);
		int m = (int)((float)j * ravineCarverConfig.getDistanceFactor().get(random));
		int n = 0;
		this.carveRavine(carverContext, ravineCarverConfig, chunk, function, random.nextLong(), i, d, (double)k, e, l, f, g, 0, m, h, bitSet);
		return true;
	}

	private void carveRavine(
		CarverContext context,
		RavineCarverConfig config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		int seaLevel,
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
		float[] fs = this.createHorizontalStretchFactors(context, config, random);
		float f = 0.0F;
		float g = 0.0F;

		for (int i = branchStartIndex; i < branchCount; i++) {
			double d = 1.5 + (double)(MathHelper.sin((float)i * (float) Math.PI / (float)branchCount) * width);
			double e = d * yawPitchRatio;
			d *= (double)config.getHorizontalRadiusFactor().get(random);
			e = this.getVerticalScale(config, random, e, (float)branchCount, (float)i);
			float h = MathHelper.cos(pitch);
			float j = MathHelper.sin(pitch);
			x += (double)(MathHelper.cos(yaw) * h);
			y += (double)j;
			z += (double)(MathHelper.sin(yaw) * h);
			pitch *= 0.7F;
			pitch += g * 0.05F;
			yaw += f * 0.05F;
			g *= 0.8F;
			f *= 0.5F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!canCarveBranch(chunk.getPos(), x, z, i, branchCount, width)) {
					return;
				}

				this.carveRegion(
					context,
					config,
					chunk,
					posToBiome,
					seed,
					seaLevel,
					x,
					y,
					z,
					d,
					e,
					carvingMask,
					(contextx, dx, ex, fx, yx) -> this.isPositionExcluded(contextx, fs, dx, ex, fx, yx)
				);
			}
		}
	}

	private int getY(CarverContext context, RavineCarverConfig config, Random random) {
		int i = config.getBottom().getY(context);
		int j = config.getTop().getY(context);
		if (i >= j) {
			LOGGER.warn("Empty carver: {} [{}-{}]", this, i, j);
			return i;
		} else {
			return MathHelper.nextBetween(random, i, j);
		}
	}

	private float[] createHorizontalStretchFactors(CarverContext context, RavineCarverConfig config, Random random) {
		int i = context.getMaxY();
		float[] fs = new float[i];
		float f = 1.0F;

		for (int j = 0; j < i; j++) {
			if (j == 0 || random.nextInt(config.getWidthSmoothness()) == 0) {
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			fs[j] = f * f;
		}

		return fs;
	}

	private double getVerticalScale(RavineCarverConfig config, Random random, double pitch, float branchCount, float branchIndex) {
		float f = 1.0F - MathHelper.abs(0.5F - branchIndex / branchCount) * 2.0F;
		float g = config.getVerticalRadiusDefaultFactor() + config.getVerticalRadiusCenterFactor() * f;
		return (double)g * pitch * (double)MathHelper.nextBetween(random, 0.75F, 1.0F);
	}

	private boolean isPositionExcluded(
		CarverContext context, float[] horizontalStretchFactors, double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y
	) {
		int i = y - context.getMinY();
		return (scaledRelativeX * scaledRelativeX + scaledRelativeZ * scaledRelativeZ) * (double)horizontalStretchFactors[i - 1]
				+ scaledRelativeY * scaledRelativeY / 6.0
			>= 1.0;
	}
}
