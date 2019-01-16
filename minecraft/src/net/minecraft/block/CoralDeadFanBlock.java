package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CoralDeadFanBlock extends CoralParentBlock {
	private static final VoxelShape field_9932 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected CoralDeadFanBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_9932;
	}
}
