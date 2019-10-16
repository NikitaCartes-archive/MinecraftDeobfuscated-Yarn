package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random);

	public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
		ConfiguredFeature<NormalTreeFeatureConfig, ?> configuredFeature = this.createTreeFeature(random);
		if (configuredFeature == null) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
			if (configuredFeature.generate(iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)chunkGenerator, random, blockPos)) {
				return true;
			} else {
				iWorld.setBlockState(blockPos, blockState, 4);
				return false;
			}
		}
	}
}
