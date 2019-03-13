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
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class LargeTreeSaplingGenerator extends SaplingGenerator {
	@Override
	public boolean method_11431(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		for (int i = 0; i >= -1; i--) {
			for (int j = 0; j >= -1; j--) {
				if (method_11442(blockState, iWorld, blockPos, i, j)) {
					return this.method_11444(iWorld, blockPos, blockState, random, i, j);
				}
			}
		}

		return super.method_11431(iWorld, blockPos, blockState, random);
	}

	@Nullable
	protected abstract AbstractTreeFeature<DefaultFeatureConfig> method_11443(Random random);

	public boolean method_11444(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random, int i, int j) {
		AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature = this.method_11443(random);
		if (abstractTreeFeature == null) {
			return false;
		} else {
			BlockState blockState2 = Blocks.field_10124.method_9564();
			iWorld.method_8652(blockPos.add(i, 0, j), blockState2, 4);
			iWorld.method_8652(blockPos.add(i + 1, 0, j), blockState2, 4);
			iWorld.method_8652(blockPos.add(i, 0, j + 1), blockState2, 4);
			iWorld.method_8652(blockPos.add(i + 1, 0, j + 1), blockState2, 4);
			if (abstractTreeFeature.method_13151(
				iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)iWorld.method_8398().getChunkGenerator(), random, blockPos.add(i, 0, j), FeatureConfig.field_13603
			)) {
				return true;
			} else {
				iWorld.method_8652(blockPos.add(i, 0, j), blockState, 4);
				iWorld.method_8652(blockPos.add(i + 1, 0, j), blockState, 4);
				iWorld.method_8652(blockPos.add(i, 0, j + 1), blockState, 4);
				iWorld.method_8652(blockPos.add(i + 1, 0, j + 1), blockState, 4);
				return false;
			}
		}
	}

	public static boolean method_11442(BlockState blockState, BlockView blockView, BlockPos blockPos, int i, int j) {
		Block block = blockState.getBlock();
		return block == blockView.method_8320(blockPos.add(i, 0, j)).getBlock()
			&& block == blockView.method_8320(blockPos.add(i + 1, 0, j)).getBlock()
			&& block == blockView.method_8320(blockPos.add(i, 0, j + 1)).getBlock()
			&& block == blockView.method_8320(blockPos.add(i + 1, 0, j + 1)).getBlock();
	}
}
