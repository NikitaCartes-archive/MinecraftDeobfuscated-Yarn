package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadBushBlock extends PlantBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected DeadBushBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		Block block = floor.getBlock();
		return block == Blocks.field_10102
			|| block == Blocks.field_10534
			|| block == Blocks.field_10415
			|| block == Blocks.field_10611
			|| block == Blocks.field_10184
			|| block == Blocks.field_10015
			|| block == Blocks.field_10325
			|| block == Blocks.field_10143
			|| block == Blocks.field_10014
			|| block == Blocks.field_10444
			|| block == Blocks.field_10349
			|| block == Blocks.field_10590
			|| block == Blocks.field_10235
			|| block == Blocks.field_10570
			|| block == Blocks.field_10409
			|| block == Blocks.field_10123
			|| block == Blocks.field_10526
			|| block == Blocks.field_10328
			|| block == Blocks.field_10626
			|| block == Blocks.field_10566
			|| block == Blocks.field_10253
			|| block == Blocks.field_10520;
	}
}
