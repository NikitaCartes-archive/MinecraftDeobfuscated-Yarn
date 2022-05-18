package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DeltaFeature extends Feature<DeltaFeatureConfig> {
	private static final ImmutableList<Block> CANNOT_REPLACE_BLOCKS = ImmutableList.of(
		Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER
	);
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final double field_31501 = 0.9;

	public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DeltaFeatureConfig> context) {
		boolean bl = false;
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		DeltaFeatureConfig deltaFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();
		boolean bl2 = random.nextDouble() < 0.9;
		int i = bl2 ? deltaFeatureConfig.getRimSize().get(random) : 0;
		int j = bl2 ? deltaFeatureConfig.getRimSize().get(random) : 0;
		boolean bl3 = bl2 && i != 0 && j != 0;
		int k = deltaFeatureConfig.getSize().get(random);
		int l = deltaFeatureConfig.getSize().get(random);
		int m = Math.max(k, l);

		for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, k, 0, l)) {
			if (blockPos2.getManhattanDistance(blockPos) > m) {
				break;
			}

			if (canPlace(structureWorldAccess, blockPos2, deltaFeatureConfig)) {
				if (bl3) {
					bl = true;
					this.setBlockState(structureWorldAccess, blockPos2, deltaFeatureConfig.getRim());
				}

				BlockPos blockPos3 = blockPos2.add(i, 0, j);
				if (canPlace(structureWorldAccess, blockPos3, deltaFeatureConfig)) {
					bl = true;
					this.setBlockState(structureWorldAccess, blockPos3, deltaFeatureConfig.getContents());
				}
			}
		}

		return bl;
	}

	private static boolean canPlace(WorldAccess world, BlockPos pos, DeltaFeatureConfig config) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.isOf(config.getContents().getBlock())) {
			return false;
		} else if (CANNOT_REPLACE_BLOCKS.contains(blockState.getBlock())) {
			return false;
		} else {
			for (Direction direction : DIRECTIONS) {
				boolean bl = world.getBlockState(pos.offset(direction)).isAir();
				if (bl && direction != Direction.UP || !bl && direction == Direction.UP) {
					return false;
				}
			}

			return true;
		}
	}
}
