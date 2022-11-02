package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public abstract class SaplingGenerator {
	@Nullable
	protected abstract RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees);

	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		RegistryKey<ConfiguredFeature<?, ?>> registryKey = this.getTreeFeature(random, this.areFlowersNearby(world, pos));
		if (registryKey == null) {
			return false;
		} else {
			RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = (RegistryEntry<ConfiguredFeature<?, ?>>)world.getRegistryManager()
				.get(Registry.CONFIGURED_FEATURE_KEY)
				.getEntry(registryKey)
				.orElse(null);
			if (registryEntry == null) {
				return false;
			} else {
				ConfiguredFeature<?, ?> configuredFeature = registryEntry.value();
				BlockState blockState = world.getFluidState(pos).getBlockState();
				world.setBlockState(pos, blockState, Block.NO_REDRAW);
				if (configuredFeature.generate(world, chunkGenerator, random, pos)) {
					if (world.getBlockState(pos) == blockState) {
						world.updateListeners(pos, state, blockState, Block.NOTIFY_LISTENERS);
					}

					return true;
				} else {
					world.setBlockState(pos, state, Block.NO_REDRAW);
					return false;
				}
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
