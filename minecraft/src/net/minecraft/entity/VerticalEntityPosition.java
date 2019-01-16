package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface VerticalEntityPosition {
	static VerticalEntityPosition minValue() {
		return VerticalEntityPositionImpl.field_17593;
	}

	static VerticalEntityPosition fromEntity(Entity entity) {
		return new VerticalEntityPositionImpl(entity);
	}

	boolean isSneaking();

	boolean isAboveBlock(VoxelShape voxelShape, BlockPos blockPos, boolean bl);

	boolean method_17785(Item item);
}
