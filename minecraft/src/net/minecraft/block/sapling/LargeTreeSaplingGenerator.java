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
	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (canGenerateLargeTree(state, world, pos, i, j)) {
					return this.generateLargeTree(world, chunkGenerator, pos, state, random, i, j);
				}
			}
		}

		return super.generate(world, chunkGenerator, pos, state, random);
	}

	@Nullable
	protected abstract ConfiguredFeature<TreeFeatureConfig, ?> getLargeTreeFeature(Random random);

	public boolean generateLargeTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random, int x, int z) {
		ConfiguredFeature<TreeFeatureConfig, ?> configuredFeature = this.getLargeTreeFeature(random);
		if (configuredFeature == null) {
			return false;
		} else {
			BlockState blockState = Blocks.AIR.getDefaultState();
			world.setBlockState(pos.add(x, 0, z), blockState, Block.NO_REDRAW);
			world.setBlockState(pos.add(x + 1, 0, z), blockState, Block.NO_REDRAW);
			world.setBlockState(pos.add(x, 0, z + 1), blockState, Block.NO_REDRAW);
			world.setBlockState(pos.add(x + 1, 0, z + 1), blockState, Block.NO_REDRAW);
			if (configuredFeature.generate(world, chunkGenerator, random, pos.add(x, 0, z))) {
				return true;
			} else {
				world.setBlockState(pos.add(x, 0, z), state, Block.NO_REDRAW);
				world.setBlockState(pos.add(x + 1, 0, z), state, Block.NO_REDRAW);
				world.setBlockState(pos.add(x, 0, z + 1), state, Block.NO_REDRAW);
				world.setBlockState(pos.add(x + 1, 0, z + 1), state, Block.NO_REDRAW);
				return false;
			}
		}
	}

	public static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
		Block block = state.getBlock();
		return world.getBlockState(pos.add(x, 0, z)).isOf(block)
			&& world.getBlockState(pos.add(x + 1, 0, z)).isOf(block)
			&& world.getBlockState(pos.add(x, 0, z + 1)).isOf(block)
			&& world.getBlockState(pos.add(x + 1, 0, z + 1)).isOf(block);
	}
}
