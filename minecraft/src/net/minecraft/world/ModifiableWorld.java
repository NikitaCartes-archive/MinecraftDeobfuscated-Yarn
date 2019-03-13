package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
	boolean method_8652(BlockPos blockPos, BlockState blockState, int i);

	boolean method_8650(BlockPos blockPos);

	boolean method_8651(BlockPos blockPos, boolean bl);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
