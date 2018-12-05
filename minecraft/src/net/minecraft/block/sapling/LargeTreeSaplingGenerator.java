package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public abstract class LargeTreeSaplingGenerator extends SaplingGenerator {
	@Override
	public boolean generate(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (method_11442(blockState, iWorld, blockPos, i, j)) {
					return this.method_11444(iWorld, blockPos, blockState, random, i, j);
				}
			}
		}

		return super.generate(iWorld, blockPos, blockState, random);
	}

	@Nullable
	protected abstract TreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random);

	public boolean method_11444(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random, int i, int j) {
		TreeFeature<DefaultFeatureConfig> treeFeature = this.createLargeTreeFeature(random);
		if (treeFeature == null) {
			return false;
		} else {
			BlockState blockState2 = Blocks.field_10124.getDefaultState();
			iWorld.setBlockState(blockPos.add(i, 0, j), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState2, 4);
			iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState2, 4);
			if (treeFeature.generate(
				iWorld,
				(ChunkGenerator<? extends ChunkGeneratorSettings>)iWorld.getChunkManager().getChunkGenerator(),
				random,
				blockPos.add(i, 0, j),
				FeatureConfig.DEFAULT
			)) {
				return true;
			} else {
				iWorld.setBlockState(blockPos.add(i, 0, j), blockState, 4);
				iWorld.setBlockState(blockPos.add(i + 1, 0, j), blockState, 4);
				iWorld.setBlockState(blockPos.add(i, 0, j + 1), blockState, 4);
				iWorld.setBlockState(blockPos.add(i + 1, 0, j + 1), blockState, 4);
				return false;
			}
		}
	}

	public static boolean method_11442(BlockState blockState, BlockView blockView, BlockPos blockPos, int i, int j) {
		Block block = blockState.getBlock();
		return block == blockView.getBlockState(blockPos.add(i, 0, j)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i + 1, 0, j)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i, 0, j + 1)).getBlock()
			&& block == blockView.getBlockState(blockPos.add(i + 1, 0, j + 1)).getBlock();
	}
}
