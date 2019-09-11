package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
	boolean setBlockState(BlockPos blockPos, BlockState blockState, int i);

	boolean removeBlock(BlockPos blockPos, boolean bl);

	default boolean breakBlock(BlockPos blockPos, boolean bl) {
		return this.breakBlock(blockPos, bl, null);
	}

	boolean breakBlock(BlockPos blockPos, boolean bl, @Nullable Entity entity);

	default boolean spawnEntity(Entity entity) {
		return false;
	}
}
