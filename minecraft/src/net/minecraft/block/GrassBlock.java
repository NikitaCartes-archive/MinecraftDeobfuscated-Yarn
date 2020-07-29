package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowerFeature;

public class GrassBlock extends SpreadableBlock implements Fertilizable {
	public GrassBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		BlockState blockState = Blocks.GRASS.getDefaultState();

		label48:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos2 = blockPos;

			for (int j = 0; j < i / 16; j++) {
				blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
					continue label48;
				}
			}

			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
				((Fertilizable)blockState.getBlock()).grow(world, random, blockPos2, blockState2);
			}

			if (blockState2.isAir()) {
				BlockState blockState3;
				if (random.nextInt(8) == 0) {
					List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).getGenerationSettings().getFlowerFeatures();
					if (list.isEmpty()) {
						continue;
					}

					ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)list.get(0);
					FlowerFeature flowerFeature = (FlowerFeature)configuredFeature.feature;
					blockState3 = flowerFeature.getFlowerState(random, blockPos2, configuredFeature.getConfig());
				} else {
					blockState3 = blockState;
				}

				if (blockState3.canPlaceAt(world, blockPos2)) {
					world.setBlockState(blockPos2, blockState3, 3);
				}
			}
		}
	}
}
