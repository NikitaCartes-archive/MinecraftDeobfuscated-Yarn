package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
	boolean setBlockState(BlockPos pos, BlockState state, int flags);

	boolean removeBlock(BlockPos pos, boolean move);

	default boolean breakBlock(BlockPos pos, boolean drop) {
		return this.breakBlock(pos, drop, null);
	}

	boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
