package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public final class OreVeinSampler {
	private static final float field_36620 = 0.4F;
	private static final int field_36621 = 20;
	private static final double field_36622 = 0.2;
	private static final float field_36623 = 0.7F;
	private static final float field_36624 = 0.1F;
	private static final float field_36625 = 0.3F;
	private static final float field_36626 = 0.6F;
	private static final float RAW_ORE_BLOCK_CHANCE = 0.02F;
	private static final float field_36628 = -0.3F;

	private OreVeinSampler() {
	}

	public static ChunkNoiseSampler.BlockStateSampler create(
		DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, RandomDeriver randomDeriver
	) {
		BlockState blockState = null;
		return pos -> {
			double d = veinToggle.sample(pos);
			int i = pos.blockY();
			OreVeinSampler.VeinType veinType = d > 0.0 ? OreVeinSampler.VeinType.COPPER : OreVeinSampler.VeinType.IRON;
			double e = Math.abs(d);
			int j = veinType.maxY - i;
			int k = i - veinType.minY;
			if (k >= 0 && j >= 0) {
				int l = Math.min(j, k);
				double f = MathHelper.clampedLerpFromProgress((double)l, 0.0, 20.0, -0.2, 0.0);
				if (e + f < 0.4F) {
					return blockState;
				} else {
					AbstractRandom abstractRandom = randomDeriver.createRandom(pos.blockX(), i, pos.blockZ());
					if (abstractRandom.nextFloat() > 0.7F) {
						return blockState;
					} else if (veinRidged.sample(pos) >= 0.0) {
						return blockState;
					} else {
						double g = MathHelper.clampedLerpFromProgress(e, 0.4F, 0.6F, 0.1F, 0.3F);
						if ((double)abstractRandom.nextFloat() < g && veinGap.sample(pos) > -0.3F) {
							return abstractRandom.nextFloat() < 0.02F ? veinType.rawOreBlock : veinType.ore;
						} else {
							return veinType.stone;
						}
					}
				}
			} else {
				return blockState;
			}
		};
	}

	protected static enum VeinType {
		COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
		IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

		final BlockState ore;
		final BlockState rawOreBlock;
		final BlockState stone;
		public final int minY;
		public final int maxY;

		private VeinType(BlockState ore, BlockState rawOreBlock, BlockState stone, int minY, int maxY) {
			this.ore = ore;
			this.rawOreBlock = rawOreBlock;
			this.stone = stone;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
