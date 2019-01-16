package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CoralDeadBlock extends CoralParentBlock {
	protected static final VoxelShape shape = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

	protected CoralDeadBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return shape;
	}
}
