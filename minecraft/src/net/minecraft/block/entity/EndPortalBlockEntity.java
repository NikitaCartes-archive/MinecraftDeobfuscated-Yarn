package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity {
	protected EndPortalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public EndPortalBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntityType.END_PORTAL, pos, state);
	}

	public boolean shouldDrawSide(Direction direction) {
		return direction.getAxis() == Direction.Axis.Y;
	}
}
