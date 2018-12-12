package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract AbstractTreeFeature<DefaultFeatureConfig> method_11430(Random random);

	public boolean generate(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature = this.method_11430(random);
		if (abstractTreeFeature == null) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 4);
			if (abstractTreeFeature.method_13151(
				iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)iWorld.getChunkManager().getChunkGenerator(), random, blockPos, FeatureConfig.field_13603
			)) {
				return true;
			} else {
				iWorld.setBlockState(blockPos, blockState, 4);
				return false;
			}
		}
	}
}
