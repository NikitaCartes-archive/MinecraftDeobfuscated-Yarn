package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class CaveCarver extends Carver<CaveCarverConfig> {
	public CaveCarver(Codec<CaveCarverConfig> codec) {
		super(codec);
	}

	public boolean shouldCarve(CaveCarverConfig caveCarverConfig, AbstractRandom abstractRandom) {
		return abstractRandom.nextFloat() <= caveCarverConfig.probability;
	}

	public boolean carve(
		CarverContext carverContext,
		CaveCarverConfig caveCarverConfig,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> function,
		AbstractRandom abstractRandom,
		AquiferSampler aquiferSampler,
		ChunkPos chunkPos,
		CarvingMask carvingMask
	) {
		int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
		int j = abstractRandom.nextInt(abstractRandom.nextInt(abstractRandom.nextInt(this.getMaxCaveCount()) + 1) + 1);

		for (int k = 0; k < j; k++) {
			double d = (double)chunkPos.getOffsetX(abstractRandom.nextInt(16));
			double e = (double)caveCarverConfig.y.get(abstractRandom, carverContext);
			double f = (double)chunkPos.getOffsetZ(abstractRandom.nextInt(16));
			double g = (double)caveCarverConfig.horizontalRadiusMultiplier.get(abstractRandom);
			double h = (double)caveCarverConfig.verticalRadiusMultiplier.get(abstractRandom);
			double l = (double)caveCarverConfig.floorLevel.get(abstractRandom);
			Carver.SkipPredicate skipPredicate = (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> isPositionExcluded(
					scaledRelativeX, scaledRelativeY, scaledRelativeZ, l
				);
			int m = 1;
			if (abstractRandom.nextInt(4) == 0) {
				double n = (double)caveCarverConfig.yScale.get(abstractRandom);
				float o = 1.0F + abstractRandom.nextFloat() * 6.0F;
				this.carveCave(carverContext, caveCarverConfig, chunk, function, aquiferSampler, d, e, f, o, n, carvingMask, skipPredicate);
				m += abstractRandom.nextInt(4);
			}

			for (int p = 0; p < m; p++) {
				float q = abstractRandom.nextFloat() * (float) (Math.PI * 2);
				float o = (abstractRandom.nextFloat() - 0.5F) / 4.0F;
				float r = this.getTunnelSystemWidth(abstractRandom);
				int s = i - abstractRandom.nextInt(i / 4);
				int t = 0;
				this.carveTunnels(
					carverContext,
					caveCarverConfig,
					chunk,
					function,
					abstractRandom.nextLong(),
					aquiferSampler,
					d,
					e,
					f,
					g,
					h,
					r,
					q,
					o,
					0,
					s,
					this.getTunnelSystemHeightWidthRatio(),
					carvingMask,
					skipPredicate
				);
			}
		}

		return true;
	}

	protected int getMaxCaveCount() {
		return 15;
	}

	protected float getTunnelSystemWidth(AbstractRandom random) {
		float f = random.nextFloat() * 2.0F + random.nextFloat();
		if (random.nextInt(10) == 0) {
			f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
		}

		return f;
	}

	protected double getTunnelSystemHeightWidthRatio() {
		return 1.0;
	}

	protected void carveCave(
		CarverContext context,
		CaveCarverConfig config,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> posToBiome,
		AquiferSampler aquiferSampler,
		double d,
		double e,
		double f,
		float g,
		double h,
		CarvingMask mask,
		Carver.SkipPredicate skipPredicate
	) {
		double i = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * g);
		double j = i * h;
		this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, d + 1.0, e, f, i, j, mask, skipPredicate);
	}

	protected void carveTunnels(
		CarverContext context,
		CaveCarverConfig config,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> posToBiome,
		long seed,
		AquiferSampler aquiferSampler,
		double x,
		double y,
		double z,
		double horizontalScale,
		double verticalScale,
		float width,
		float yaw,
		float pitch,
		int branchStartIndex,
		int branchCount,
		double yawPitchRatio,
		CarvingMask mask,
		Carver.SkipPredicate skipPredicate
	) {
		AbstractRandom abstractRandom = AbstractRandom.createAtomic(seed);
		int i = abstractRandom.nextInt(branchCount / 2) + branchCount / 4;
		boolean bl = abstractRandom.nextInt(6) == 0;
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
			g += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 2.0F;
			f += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 4.0F;
			if (j == i && width > 1.0F) {
				this.carveTunnels(
					context,
					config,
					chunk,
					posToBiome,
					abstractRandom.nextLong(),
					aquiferSampler,
					x,
					y,
					z,
					horizontalScale,
					verticalScale,
					abstractRandom.nextFloat() * 0.5F + 0.5F,
					yaw - (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					mask,
					skipPredicate
				);
				this.carveTunnels(
					context,
					config,
					chunk,
					posToBiome,
					abstractRandom.nextLong(),
					aquiferSampler,
					x,
					y,
					z,
					horizontalScale,
					verticalScale,
					abstractRandom.nextFloat() * 0.5F + 0.5F,
					yaw + (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					mask,
					skipPredicate
				);
				return;
			}

			if (abstractRandom.nextInt(4) != 0) {
				if (!canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
					return;
				}

				this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, x, y, z, d * horizontalScale, e * verticalScale, mask, skipPredicate);
			}
		}
	}

	private static boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, double floorY) {
		return scaledRelativeY <= floorY ? true : scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
	}
}
