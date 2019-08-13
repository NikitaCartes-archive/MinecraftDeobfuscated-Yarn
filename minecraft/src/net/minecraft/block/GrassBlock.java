package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;

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
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = Blocks.field_10479.getDefaultState();

		label48:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos3 = blockPos2;

			for (int j = 0; j < i / 16; j++) {
				blockPos3 = blockPos3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (world.getBlockState(blockPos3.down()).getBlock() != this || world.getBlockState(blockPos3).method_21743(world, blockPos3)) {
					continue label48;
				}
			}

			BlockState blockState3 = world.getBlockState(blockPos3);
			if (blockState3.getBlock() == blockState2.getBlock() && random.nextInt(10) == 0) {
				((Fertilizable)blockState2.getBlock()).grow(world, random, blockPos3, blockState3);
			}

			if (blockState3.isAir()) {
				BlockState blockState4;
				if (random.nextInt(8) == 0) {
					List<ConfiguredFeature<?>> list = world.getBiome(blockPos3).getFlowerFeatures();
					if (list.isEmpty()) {
						continue;
					}

					blockState4 = ((FlowerFeature)((DecoratedFeatureConfig)((ConfiguredFeature)list.get(0)).config).feature.feature).getFlowerToPlace(random, blockPos3);
				} else {
					blockState4 = blockState2;
				}

				if (blockState4.canPlaceAt(world, blockPos3)) {
					world.setBlockState(blockPos3, blockState4, 3);
				}
			}
		}
	}

	@Override
	public boolean isOpaque(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
}
