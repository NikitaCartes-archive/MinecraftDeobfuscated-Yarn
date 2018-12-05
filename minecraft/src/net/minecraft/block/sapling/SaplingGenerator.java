package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract TreeFeature<DefaultFeatureConfig> createTreeFeature(Random random);

	public boolean generate(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		TreeFeature<DefaultFeatureConfig> treeFeature = this.createTreeFeature(random);
		if (treeFeature == null) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 4);
			if (treeFeature.generate(
				iWorld, (ChunkGenerator<? extends ChunkGeneratorSettings>)iWorld.getChunkManager().getChunkGenerator(), random, blockPos, FeatureConfig.DEFAULT
			)) {
				return true;
			} else {
				iWorld.setBlockState(blockPos, blockState, 4);
				return false;
			}
		}
	}
}
