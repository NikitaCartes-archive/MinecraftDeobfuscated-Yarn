package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public interface BlockPointer extends Position {
	@Override
	double getX();

	@Override
	double getY();

	@Override
	double getZ();

	BlockPos getPos();

	BlockState getBlockState();

	<T extends BlockEntity> T getBlockEntity();

	ServerWorld getWorld();
}
