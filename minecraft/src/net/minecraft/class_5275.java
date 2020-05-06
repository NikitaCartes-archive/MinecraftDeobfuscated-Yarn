package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class class_5275 {
	public static int[][] method_27934(Direction direction) {
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction2.getOpposite();
		Direction direction4 = direction.getOpposite();
		return new int[][]{
			{direction2.getOffsetX(), direction2.getOffsetZ()},
			{direction3.getOffsetX(), direction3.getOffsetZ()},
			{direction4.getOffsetX() + direction2.getOffsetX(), direction4.getOffsetZ() + direction2.getOffsetZ()},
			{direction4.getOffsetX() + direction3.getOffsetX(), direction4.getOffsetZ() + direction3.getOffsetZ()},
			{direction.getOffsetX() + direction2.getOffsetX(), direction.getOffsetZ() + direction2.getOffsetZ()},
			{direction.getOffsetX() + direction3.getOffsetX(), direction.getOffsetZ() + direction3.getOffsetZ()},
			{direction4.getOffsetX(), direction4.getOffsetZ()},
			{direction.getOffsetX(), direction.getOffsetZ()}
		};
	}

	public static boolean method_27932(double d) {
		return !Double.isInfinite(d) && d < 1.0;
	}

	public static boolean method_27933(World world, LivingEntity livingEntity, Box box) {
		return world.getBlockCollisions(livingEntity, box).allMatch(VoxelShape::isEmpty);
	}
}
