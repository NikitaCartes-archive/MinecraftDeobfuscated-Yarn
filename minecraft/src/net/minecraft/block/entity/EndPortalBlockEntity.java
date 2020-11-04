package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity {
	protected EndPortalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public EndPortalBlockEntity(BlockPos blockPos, BlockState blockState) {
		this(BlockEntityType.END_PORTAL, blockPos, blockState);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldDrawSide(Direction direction) {
		return direction == Direction.UP;
	}
}
