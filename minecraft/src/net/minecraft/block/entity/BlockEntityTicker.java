package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A functional interface that ticks a block entity. This is usually implemented
 * as a static method in the block entity's class.
 * 
 * @see net.minecraft.block.BlockEntityProvider#getTicker
 */
@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
	/**
	 * Ticks the block entity.
	 */
	void tick(World world, BlockPos pos, BlockState state, T blockEntity);
}
