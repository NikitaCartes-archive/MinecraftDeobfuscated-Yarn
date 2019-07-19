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
	protected abstract AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random);

	public boolean generate(IWorld world, BlockPos pos, BlockState state, Random random) {
		AbstractTreeFeature<DefaultFeatureConfig> abstractTreeFeature = this.createTreeFeature(random);
		if (abstractTreeFeature == null) {
			return false;
		} else {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
			if (abstractTreeFeature.generate(
				world, (ChunkGenerator<? extends ChunkGeneratorConfig>)world.getChunkManager().getChunkGenerator(), random, pos, FeatureConfig.DEFAULT
			)) {
				return true;
			} else {
				world.setBlockState(pos, state, 4);
				return false;
			}
		}
	}
}
