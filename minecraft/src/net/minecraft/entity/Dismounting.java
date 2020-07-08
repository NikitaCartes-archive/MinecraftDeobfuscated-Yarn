package net.minecraft.entity;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

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

	public static boolean canPlaceEntityAt(CollisionView world, LivingEntity entity, Box targetBox) {
		return world.getBlockCollisions(entity, targetBox).allMatch(VoxelShape::isEmpty);
	}

	@Nullable
	public static Vec3d findDismountPos(CollisionView world, double x, double height, double z, LivingEntity entity, EntityPose pose) {
		if (canDismountInBlock(height)) {
			Vec3d vec3d = new Vec3d(x, height, z);
			if (canPlaceEntityAt(world, entity, entity.getBoundingBox(pose).offset(vec3d))) {
				return vec3d;
			}
		}

		return null;
	}

	public static VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return !blockState.isIn(BlockTags.CLIMBABLE) && (!(blockState.getBlock() instanceof TrapdoorBlock) || !blockState.get(TrapdoorBlock.OPEN))
			? blockState.getCollisionShape(world, pos)
			: VoxelShapes.empty();
	}

	public static double getCeilingHeight(BlockPos pos, int maxDistance, Function<BlockPos, VoxelShape> collisionShapeGetter) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		int i = 0;

		while (i < maxDistance) {
			VoxelShape voxelShape = (VoxelShape)collisionShapeGetter.apply(mutable);
			if (!voxelShape.isEmpty()) {
				return (double)(pos.getY() + i) + voxelShape.getMin(Direction.Axis.Y);
			}

			i++;
			mutable.move(Direction.UP);
		}

		return Double.POSITIVE_INFINITY;
	}
}
