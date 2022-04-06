package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class RavineCarver extends Carver<RavineCarverConfig> {
	public RavineCarver(Codec<RavineCarverConfig> codec) {
		super(codec);
	}

	public boolean shouldCarve(RavineCarverConfig ravineCarverConfig, AbstractRandom abstractRandom) {
		return abstractRandom.nextFloat() <= ravineCarverConfig.probability;
	}

	public boolean carve(
		CarverContext carverContext,
		RavineCarverConfig ravineCarverConfig,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> function,
		AbstractRandom abstractRandom,
		AquiferSampler aquiferSampler,
		ChunkPos chunkPos,
		CarvingMask carvingMask
	) {
		int i = (this.getBranchFactor() * 2 - 1) * 16;
		double d = (double)chunkPos.getOffsetX(abstractRandom.nextInt(16));
		int j = ravineCarverConfig.y.get(abstractRandom, carverContext);
		double e = (double)chunkPos.getOffsetZ(abstractRandom.nextInt(16));
		float f = abstractRandom.nextFloat() * (float) (Math.PI * 2);
		float g = ravineCarverConfig.verticalRotation.get(abstractRandom);
		double h = (double)ravineCarverConfig.yScale.get(abstractRandom);
		float k = ravineCarverConfig.shape.thickness.get(abstractRandom);
		int l = (int)((float)i * ravineCarverConfig.shape.distanceFactor.get(abstractRandom));
		int m = 0;
		this.carveRavine(
			carverContext, ravineCarverConfig, chunk, function, abstractRandom.nextLong(), aquiferSampler, d, (double)j, e, k, f, g, 0, l, h, carvingMask
		);
		return true;
	}

	private void carveRavine(
		CarverContext context,
		RavineCarverConfig config,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> posToBiome,
		long seed,
		AquiferSampler aquiferSampler,
		double x,
		double y,
		double z,
		float width,
		float yaw,
		float pitch,
		int branchStartIndex,
		int branchCount,
		double yawPitchRatio,
		CarvingMask mask
	) {
		AbstractRandom abstractRandom = AbstractRandom.createAtomic(seed);
		float[] fs = this.createHorizontalStretchFactors(context, config, abstractRandom);
		float f = 0.0F;
		float g = 0.0F;

		for (int i = branchStartIndex; i < branchCount; i++) {
			double d = 1.5 + (double)(MathHelper.sin((float)i * (float) Math.PI / (float)branchCount) * width);
			double e = d * yawPitchRatio;
			d *= (double)config.shape.horizontalRadiusFactor.get(abstractRandom);
			e = this.getVerticalScale(config, abstractRandom, e, (float)branchCount, (float)i);
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
			g += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 2.0F;
			f += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 4.0F;
			if (abstractRandom.nextInt(4) != 0) {
				if (!canCarveBranch(chunk.getPos(), x, z, i, branchCount, width)) {
					return;
				}

				this.carveRegion(
					context,
					config,
					chunk,
					posToBiome,
					aquiferSampler,
					x,
					y,
					z,
					d,
					e,
					mask,
					(contextx, scaledRelativeX, scaledRelativeY, scaledRelativeZ, yx) -> this.isPositionExcluded(
							contextx, fs, scaledRelativeX, scaledRelativeY, scaledRelativeZ, yx
						)
				);
			}
		}
	}

	private float[] createHorizontalStretchFactors(CarverContext context, RavineCarverConfig config, AbstractRandom random) {
		int i = context.getHeight();
		float[] fs = new float[i];
		float f = 1.0F;

		for (int j = 0; j < i; j++) {
			if (j == 0 || random.nextInt(config.shape.widthSmoothness) == 0) {
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			fs[j] = f * f;
		}

		return fs;
	}

	private double getVerticalScale(RavineCarverConfig config, AbstractRandom random, double pitch, float branchCount, float branchIndex) {
		float f = 1.0F - MathHelper.abs(0.5F - branchIndex / branchCount) * 2.0F;
		float g = config.shape.verticalRadiusDefaultFactor + config.shape.verticalRadiusCenterFactor * f;
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
