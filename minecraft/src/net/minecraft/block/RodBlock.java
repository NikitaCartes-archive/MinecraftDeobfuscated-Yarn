package net.minecraft.block;

import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class RodBlock extends FacingBlock {
	protected static final float field_31233 = 6.0F;
	protected static final float field_31234 = 10.0F;
	protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	protected RodBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (((Direction)state.get(FACING)).getAxis()) {
			case X:
			default:
				return X_SHAPE;
			case Z:
				return Z_SHAPE;
			case Y:
				return Y_SHAPE;
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(FACING, mirror.apply(state.get(FACING)));
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
