package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Similar to {@link FuzzyTargeting}, but the positions this class' utility methods
 * find never have pathfinding penalties and are always on solid blocks.
 */
public class NoPenaltySolidTargeting {
	@Nullable
	public static Vec3d find(
		PathAwareEntity entity, int horizontalRange, int verticalRange, int startHeight, double directionX, double directionZ, double rangeAngle
	) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> tryMake(entity, horizontalRange, verticalRange, startHeight, directionX, directionZ, rangeAngle, bl));
	}

	@Nullable
	public static BlockPos tryMake(
		PathAwareEntity entity,
		int horizontalRange,
		int verticalRange,
		int startHeight,
		double directionX,
		double directionZ,
		double rangeAngle,
		boolean posTargetInRange
	) {
		BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, startHeight, directionX, directionZ, rangeAngle);
		if (blockPos == null) {
			return null;
		} else {
			BlockPos blockPos2 = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), blockPos);
			if (!NavigationConditions.isHeightInvalid(blockPos2, entity) && !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos2)) {
				blockPos2 = FuzzyPositions.upWhile(blockPos2, entity.getWorld().getTopYInclusive(), pos -> NavigationConditions.isSolidAt(entity, pos));
				return NavigationConditions.hasPathfindingPenalty(entity, blockPos2) ? null : blockPos2;
			} else {
				return null;
			}
		}
	}
}
