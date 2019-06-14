package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadCoralFanBlock extends CoralParentBlock {
	private static final VoxelShape field_9932 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected DeadCoralFanBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_9932;
	}
}
