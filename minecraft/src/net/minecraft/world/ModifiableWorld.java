package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
	boolean setBlockState(BlockPos blockPos, BlockState blockState, int i);

	boolean clearBlockState(BlockPos blockPos, boolean bl);

	boolean breakBlock(BlockPos blockPos, boolean bl);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
