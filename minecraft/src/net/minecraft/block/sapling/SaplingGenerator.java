package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees);

	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry = this.getTreeFeature(random, this.areFlowersNearby(world, pos));
		if (registryEntry == null) {
			return false;
		} else {
			ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)registryEntry.value();
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NO_REDRAW);
			if (configuredFeature.generate(world, chunkGenerator, random, pos)) {
				return true;
			} else {
				world.setBlockState(pos, state, Block.NO_REDRAW);
				return false;
			}
		}
	}

	private boolean areFlowersNearby(WorldAccess world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.Mutable.iterate(pos.down().north(2).west(2), pos.up().south(2).east(2))) {
			if (world.getBlockState(blockPos).isIn(BlockTags.FLOWERS)) {
				return true;
			}
		}

		return false;
	}
}
