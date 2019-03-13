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
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return blockView.method_8320(blockPos.up()).isAir();
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = Blocks.field_10479.method_9564();

		label48:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos3 = blockPos2;

			for (int j = 0; j < i / 16; j++) {
				blockPos3 = blockPos3.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (world.method_8320(blockPos3.down()).getBlock() != this || world.method_8320(blockPos3).method_11603(world, blockPos3)) {
					continue label48;
				}
			}

			BlockState blockState3 = world.method_8320(blockPos3);
			if (blockState3.getBlock() == blockState2.getBlock() && random.nextInt(10) == 0) {
				((Fertilizable)blockState2.getBlock()).method_9652(world, random, blockPos3, blockState3);
			}

			if (blockState3.isAir()) {
				BlockState blockState4;
				if (random.nextInt(8) == 0) {
					List<ConfiguredFeature<?>> list = world.method_8310(blockPos3).getFlowerFeatures();
					if (list.isEmpty()) {
						continue;
					}

					blockState4 = ((FlowerFeature)((DecoratedFeatureConfig)((ConfiguredFeature)list.get(0)).field_13375).feature.field_13376).method_13175(random, blockPos3);
				} else {
					blockState4 = blockState2;
				}

				if (blockState4.method_11591(world, blockPos3)) {
					world.method_8652(blockPos3, blockState4, 3);
				}
			}
		}
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.MIPPED_CUTOUT;
	}
}
