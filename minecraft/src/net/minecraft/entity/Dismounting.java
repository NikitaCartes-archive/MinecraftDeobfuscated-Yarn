package net.minecraft.entity;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class Dismounting {
	public static int[][] getDismountOffsets(Direction movementDirection) {
		Direction direction = movementDirection.rotateYClockwise();
		Direction direction2 = direction.getOpposite();
		Direction direction3 = movementDirection.getOpposite();
		return new int[][]{
			{direction.getOffsetX(), direction.getOffsetZ()},
			{direction2.getOffsetX(), direction2.getOffsetZ()},
			{direction3.getOffsetX() + direction.getOffsetX(), direction3.getOffsetZ() + direction.getOffsetZ()},
			{direction3.getOffsetX() + direction2.getOffsetX(), direction3.getOffsetZ() + direction2.getOffsetZ()},
			{movementDirection.getOffsetX() + direction.getOffsetX(), movementDirection.getOffsetZ() + direction.getOffsetZ()},
			{movementDirection.getOffsetX() + direction2.getOffsetX(), movementDirection.getOffsetZ() + direction2.getOffsetZ()},
			{direction3.getOffsetX(), direction3.getOffsetZ()},
			{movementDirection.getOffsetX(), movementDirection.getOffsetZ()}
		};
	}

	public static boolean canDismountInBlock(double height) {
		return !Double.isInfinite(height) && height < 1.0;
	}

	public static boolean canPlaceEntityAt(World world, LivingEntity entity, Box targetBox) {
		return world.getBlockCollisions(entity, targetBox).allMatch(VoxelShape::isEmpty);
	}
}
