package net.minecraft.block;

import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

public class GrassBlock extends SpreadableBlock implements Fertilizable {
	public GrassBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, AbstractRandom random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, AbstractRandom random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		BlockState blockState = Blocks.GRASS.getDefaultState();

		label46:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos2 = blockPos;

			for (int j = 0; j < i / 16; j++) {
				blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
					continue label46;
				}
			}

			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
				((Fertilizable)blockState.getBlock()).grow(world, random, blockPos2, blockState2);
			}

			if (blockState2.isAir()) {
				RegistryEntry<PlacedFeature> registryEntry;
				if (random.nextInt(8) == 0) {
					List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).value().getGenerationSettings().getFlowerFeatures();
					if (list.isEmpty()) {
						continue;
					}

					registryEntry = ((RandomPatchFeatureConfig)((ConfiguredFeature)list.get(0)).config()).feature();
				} else {
					registryEntry = VegetationPlacedFeatures.GRASS_BONEMEAL;
				}

				registryEntry.value().generateUnregistered(world, world.getChunkManager().getChunkGenerator(), random, blockPos2);
			}
		}
	}
}
