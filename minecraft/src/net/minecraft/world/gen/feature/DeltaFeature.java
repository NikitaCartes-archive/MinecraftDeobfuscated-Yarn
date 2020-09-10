package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DeltaFeature extends Feature<DeltaFeatureConfig> {
	private static final ImmutableList<Block> field_24133 = ImmutableList.of(
		Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER
	);
	private static final Direction[] DIRECTIONS = Direction.values();

	public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig
	) {
		boolean bl = false;
		boolean bl2 = random.nextDouble() < 0.9;
		int i = bl2 ? deltaFeatureConfig.getRimSize().getValue(random) : 0;
		int j = bl2 ? deltaFeatureConfig.getRimSize().getValue(random) : 0;
		boolean bl3 = bl2 && i != 0 && j != 0;
		int k = deltaFeatureConfig.getSize().getValue(random);
		int l = deltaFeatureConfig.getSize().getValue(random);
		int m = Math.max(k, l);

		for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, k, 0, l)) {
			if (blockPos2.getManhattanDistance(blockPos) > m) {
				break;
			}

			if (method_27103(structureWorldAccess, blockPos2, deltaFeatureConfig)) {
				if (bl3) {
					bl = true;
					this.setBlockState(structureWorldAccess, blockPos2, deltaFeatureConfig.getRim());
				}

				BlockPos blockPos3 = blockPos2.add(i, 0, j);
				if (method_27103(structureWorldAccess, blockPos3, deltaFeatureConfig)) {
					bl = true;
					this.setBlockState(structureWorldAccess, blockPos3, deltaFeatureConfig.getContents());
				}
			}
		}

		return bl;
	}

	private static boolean method_27103(WorldAccess worldAccess, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (blockState.isOf(deltaFeatureConfig.getContents().getBlock())) {
			return false;
		} else if (field_24133.contains(blockState.getBlock())) {
			return false;
		} else {
			for (Direction direction : DIRECTIONS) {
				boolean bl = worldAccess.getBlockState(blockPos.offset(direction)).isAir();
				if (bl && direction != Direction.UP || !bl && direction == Direction.UP) {
					return false;
				}
			}

			return true;
		}
	}
}
