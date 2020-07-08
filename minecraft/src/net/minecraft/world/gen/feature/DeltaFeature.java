package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DeltaFeature extends Feature<DeltaFeatureConfig> {
	private static final ImmutableList<Block> field_24133 = ImmutableList.of(
		Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER
	);
	private static final Direction[] field_23883 = Direction.values();

	public DeltaFeature(Codec<DeltaFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig
	) {
		boolean bl = false;
		boolean bl2 = random.nextDouble() < 0.9;
		int i = bl2 ? deltaFeatureConfig.method_30403().method_30321(random) : 0;
		int j = bl2 ? deltaFeatureConfig.method_30403().method_30321(random) : 0;
		boolean bl3 = bl2 && i != 0 && j != 0;
		int k = deltaFeatureConfig.method_30402().method_30321(random);
		int l = deltaFeatureConfig.method_30402().method_30321(random);
		int m = Math.max(k, l);

		for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, k, 0, l)) {
			if (blockPos2.getManhattanDistance(blockPos) > m) {
				break;
			}

			if (method_27103(serverWorldAccess, blockPos2, deltaFeatureConfig)) {
				if (bl3) {
					bl = true;
					this.setBlockState(serverWorldAccess, blockPos2, deltaFeatureConfig.method_30400());
				}

				BlockPos blockPos3 = blockPos2.add(i, 0, j);
				if (method_27103(serverWorldAccess, blockPos3, deltaFeatureConfig)) {
					bl = true;
					this.setBlockState(serverWorldAccess, blockPos3, deltaFeatureConfig.method_30397());
				}
			}
		}

		return bl;
	}

	private static boolean method_27103(WorldAccess worldAccess, BlockPos blockPos, DeltaFeatureConfig deltaFeatureConfig) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (blockState.isOf(deltaFeatureConfig.method_30397().getBlock())) {
			return false;
		} else if (field_24133.contains(blockState.getBlock())) {
			return false;
		} else {
			for (Direction direction : field_23883) {
				boolean bl = worldAccess.getBlockState(blockPos.offset(direction)).isAir();
				if (bl && direction != Direction.UP || !bl && direction == Direction.UP) {
					return false;
				}
			}

			return true;
		}
	}
}
