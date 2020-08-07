package net.minecraft.entity.mob;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerLidCollisions {
	public static Box getLidCollisionBox(BlockPos pos, Direction direction) {
		return VoxelShapes.fullCube()
			.getBoundingBox()
			.stretch((double)(0.5F * (float)direction.getOffsetX()), (double)(0.5F * (float)direction.getOffsetY()), (double)(0.5F * (float)direction.getOffsetZ()))
			.shrink((double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ())
			.offset(pos.offset(direction));
	}
}
