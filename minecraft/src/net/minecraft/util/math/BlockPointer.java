package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface BlockPointer extends WorldPositionPointer {
	@Override
	double getX();

	@Override
	double getY();

	@Override
	double getZ();

	BlockPos getBlockPos();

	BlockState getBlockState();

	<T extends BlockEntity> T getBlockEntity();
}
