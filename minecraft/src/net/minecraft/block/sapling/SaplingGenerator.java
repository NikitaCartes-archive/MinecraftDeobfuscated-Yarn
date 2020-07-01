package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl);

	public boolean generate(ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
		ConfiguredFeature<TreeFeatureConfig, ?> configuredFeature = this.createTreeFeature(random, this.method_24282(serverWorld, blockPos));
		if (configuredFeature == null) {
			return false;
		} else {
			serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
			configuredFeature.config.ignoreFluidCheck();
			if (configuredFeature.generate(serverWorld, chunkGenerator, random, blockPos)) {
				return true;
			} else {
				serverWorld.setBlockState(blockPos, blockState, 4);
				return false;
			}
		}
	}

	private boolean method_24282(WorldAccess worldAccess, BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.Mutable.iterate(blockPos.down().north(2).west(2), blockPos.up().south(2).east(2))) {
			if (worldAccess.getBlockState(blockPos2).isIn(BlockTags.FLOWERS)) {
				return true;
			}
		}

		return false;
	}
}
