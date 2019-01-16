package net.minecraft.util.math;

import net.minecraft.class_2364;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface BlockPointer extends class_2364 {
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
