package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class AzaleaBlock extends PlantBlock {
	protected AzaleaBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.CLAY) || super.canPlantOnTop(floor, world, pos);
	}
}
