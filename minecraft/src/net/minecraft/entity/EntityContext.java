package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface EntityContext {
	static EntityContext absent() {
		return EntityContextImpl.ABSENT;
	}

	static EntityContext of(Entity entity) {
		return new EntityContextImpl(entity);
	}

	boolean isSneaking();

	boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl);

	boolean isHolding(Item item);
}
