package net.minecraft.entity.ai;

import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Path targeting utilities using fuzzy, or approximated, positions from
 * {@link FuzzyPositions}.
 */
public class FuzzyTargeting {
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
		return find(entity, horizontalRange, verticalRange, entity::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, ToDoubleFunction<BlockPos> scorer) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBest(() -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
			BlockPos blockPos2 = tryMake(entity, horizontalRange, bl, blockPos);
			return blockPos2 == null ? null : validate(entity, blockPos2);
		}, scorer);
	}

	@Nullable
	public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end) {
		Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return findValid(entity, horizontalRange, verticalRange, vec3d, bl);
	}

	@Nullable
	public static Vec3d findFrom(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d start) {
		Vec3d vec3d = entity.getPos().subtract(start);
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return findValid(entity, horizontalRange, verticalRange, vec3d, bl);
	}

	@Nullable
	private static Vec3d findValid(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d direction, boolean posTargetInRange) {
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, direction.x, direction.z, (float) (Math.PI / 2));
			if (blockPos == null) {
				return null;
			} else {
				BlockPos blockPos2 = tryMake(entity, horizontalRange, posTargetInRange, blockPos);
				return blockPos2 == null ? null : validate(entity, blockPos2);
			}
		});
	}

	@Nullable
	public static BlockPos validate(PathAwareEntity entity, BlockPos pos) {
		pos = FuzzyPositions.upWhile(pos, entity.world.getTopY(), currentPos -> NavigationConditions.isSolidAt(entity, currentPos));
		return !NavigationConditions.isWaterAt(entity, pos) && !NavigationConditions.hasPathfindingPenalty(entity, pos) ? pos : null;
	}

	@Nullable
	public static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos relativeInRangePos) {
		BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), relativeInRangePos);
		return !NavigationConditions.isHeightInvalid(blockPos, entity)
				&& !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos)
				&& !NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)
			? blockPos
			: null;
	}
}
