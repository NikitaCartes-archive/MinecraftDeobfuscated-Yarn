package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
	/**
	 * Runs this action on the given block entity. The world, block position, and block state are passed
	 * as context.
	 */
	void tick(World world, BlockPos pos, BlockState state, T blockEntity);
}
