package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AzaleaBlock extends PlantBlock {
	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	protected AzaleaBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.CLAY) || super.canPlantOnTop(floor, world, pos);
	}
}
