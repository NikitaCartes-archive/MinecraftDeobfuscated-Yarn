package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Similar to {@link FuzzyTargeting}, but the positions this class' utility methods
 * find never have pathfinding penalties.
 */
public class NoPenaltyTargeting {
	/**
	 * Paths to a random reachable position with no penalty.
	 * 
	 * @return the chosen end position or null if no valid positions could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 */
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
			return tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	/**
	 * Paths to a position leading towards a given end-point.
	 * 
	 * @return the chosen end position or null if no valid positions could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 * @param end the position to path towards
	 * @param angleRange the minimum angle of approach
	 */
	@Nullable
	public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end, double angleRange) {
		Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, angleRange);
			return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	/**
	 * Paths to a position leading away from a given starting point.
	 * 
	 * @return the chosen end position or null if no valid positions could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 * @param start the position to path away from
	 */
	@Nullable
	public static Vec3d findFrom(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d start) {
		Vec3d vec3d = entity.getPos().subtract(start);
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, (float) (Math.PI / 2));
			return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	@Nullable
	private static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos fuzz) {
		BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), fuzz);
		return !NavigationConditions.isHeightInvalid(blockPos, entity)
				&& !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos)
				&& !NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)
				&& !NavigationConditions.hasPathfindingPenalty(entity, blockPos)
			? blockPos
			: null;
	}
}
