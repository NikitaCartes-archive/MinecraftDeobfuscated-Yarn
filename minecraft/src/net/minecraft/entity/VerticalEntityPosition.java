package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface VerticalEntityPosition {
	VerticalEntityPosition MIN_VALUE = new VerticalEntityPositionImpl(false, -Double.MAX_VALUE);

	static VerticalEntityPosition minValue() {
		return MIN_VALUE;
	}

	static VerticalEntityPosition fromEntity(@Nullable Entity entity) {
		return (VerticalEntityPosition)(entity == null ? minValue() : new VerticalEntityPositionImpl(entity));
	}

	boolean isSneaking();

	boolean isAboveBlock(VoxelShape voxelShape, BlockPos blockPos);
}
