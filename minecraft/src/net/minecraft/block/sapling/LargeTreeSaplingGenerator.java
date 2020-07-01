package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class LargeTreeSaplingGenerator extends SaplingGenerator {
	@Override
	public boolean generate(ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (canGenerateLargeTree(blockState, serverWorld, blockPos, i, j)) {
					return this.generateLargeTree(serverWorld, chunkGenerator, blockPos, blockState, random, i, j);
				}
			}
		}

		return super.generate(serverWorld, chunkGenerator, blockPos, blockState, random);
	}

	@Nullable
	protected abstract ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random random);

	public boolean generateLargeTree(ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, Random random, int i, int j) {
		ConfiguredFeature<TreeFeatureConfig, ?> configuredFeature = this.createLargeTreeFeature(random);
		if (configuredFeature == null) {
			return false;
		} else {
			configuredFeature.config.ignoreFluidCheck();
			BlockState blockState2 = Blocks.AIR.getDefaultState();
			serverWorld.setBlockState(blockPos.add(i, 0, j), blockState2, 4);
			serverWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState2, 4);
			serverWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState2, 4);
			serverWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState2, 4);
			if (configuredFeature.generate(serverWorld, chunkGenerator, random, blockPos.add(i, 0, j))) {
				return true;
			} else {
				serverWorld.setBlockState(blockPos.add(i, 0, j), blockState, 4);
				serverWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
				serverWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
				serverWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
				return false;
			}
		}
	}

	public static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
		Block block = state.getBlock();
		return block == world.getBlockState(pos.add(x, 0, z)).getBlock()
			&& block == world.getBlockState(pos.add(x + 1, 0, z)).getBlock()
			&& block == world.getBlockState(pos.add(x, 0, z + 1)).getBlock()
			&& block == world.getBlockState(pos.add(x + 1, 0, z + 1)).getBlock();
	}
}
