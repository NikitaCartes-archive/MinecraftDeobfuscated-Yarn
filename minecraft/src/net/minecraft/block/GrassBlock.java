package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.class_4624;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;

public class GrassBlock extends SpreadableBlock implements Fertilizable {
	public GrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return blockView.getBlockState(blockPos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = Blocks.GRASS.getDefaultState();

		label48:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos3 = blockPos2;

			for (int j = 0; j < i / 16; j++) {
				blockPos3 = blockPos3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (serverWorld.getBlockState(blockPos3.method_10074()).getBlock() != this || serverWorld.getBlockState(blockPos3).method_21743(serverWorld, blockPos3)) {
					continue label48;
				}
			}

			BlockState blockState3 = serverWorld.getBlockState(blockPos3);
			if (blockState3.getBlock() == blockState2.getBlock() && random.nextInt(10) == 0) {
				((Fertilizable)blockState2.getBlock()).grow(serverWorld, random, blockPos3, blockState3);
			}

			if (blockState3.isAir()) {
				BlockState blockState4;
				if (random.nextInt(8) == 0) {
					List<ConfiguredFeature<?, ?>> list = serverWorld.getBiome(blockPos3).getFlowerFeatures();
					if (list.isEmpty()) {
						continue;
					}

					ConfiguredFeature<?, ?> configuredFeature = ((DecoratedFeatureConfig)((ConfiguredFeature)list.get(0)).config).feature;
					blockState4 = ((class_4624)configuredFeature.feature).getFlowerToPlace(random, blockPos3, configuredFeature.config);
				} else {
					blockState4 = blockState2;
				}

				if (blockState4.canPlaceAt(serverWorld, blockPos3)) {
					serverWorld.setBlockState(blockPos3, blockState4, 3);
				}
			}
		}
	}
}
