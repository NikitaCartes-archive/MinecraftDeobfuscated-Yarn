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

	boolean isDescending();

	boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue);

	boolean isHolding(Item item);
}
