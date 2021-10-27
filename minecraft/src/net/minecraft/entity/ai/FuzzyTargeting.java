package net.minecraft.entity.ai;

import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Path targeting utilities using fuzzy, or approximated, positions from
 * {@link FuzzyPositions}.
 * <p>
 * Methods in this class can be used to do pathing for an entity to a random position.
 * Positions are chosen to stay within range of the entity's chosen {@linkplain net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}
 * if applicable, and will be suitably randomized within that constraint.
 */
public class FuzzyTargeting {
	/**
	 * Paths to a random reachable position with positive path-finding favorability.
	 * 
	 * @return chosen position or null if none could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 */
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
		return find(entity, horizontalRange, verticalRange, entity::getPathfindingFavor);
	}

	/**
	 * Paths to a random reachable position with positive path-finding favorability computed by a given function.
	 * 
	 * @return the chosen position or null if none could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 * @param scorer function to compute the path-finding favorability of a candidate position
	 */
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, ToDoubleFunction<BlockPos> scorer) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBest(() -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
			BlockPos blockPos2 = towardTarget(entity, horizontalRange, bl, blockPos);
			return blockPos2 == null ? null : validate(entity, blockPos2);
		}, scorer);
	}

	/**
	 * Paths to a random reachable position leading towards a given end-point.
	 * 
	 * @return the chosen position or null if none could be found
	 * 
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
	 * @param end the position to path towards
	 */
	@Nullable
	public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end) {
		Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return findValid(entity, horizontalRange, verticalRange, vec3d, bl);
	}

	/**
	 * Paths to a random reachable position leading away from a given starting point.
	 * 
	 * @return the chosen position or null if none could be found
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
		return findValid(entity, horizontalRange, verticalRange, vec3d, bl);
	}

	@Nullable
	private static Vec3d findValid(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d direction, boolean posTargetInRange) {
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, direction.x, direction.z, (float) (Math.PI / 2));
			if (blockPos == null) {
				return null;
			} else {
				BlockPos blockPos2 = towardTarget(entity, horizontalRange, posTargetInRange, blockPos);
				return blockPos2 == null ? null : validate(entity, blockPos2);
			}
		});
	}

	/**
	 * Checks whether a given position is a valid pathable target.
	 * 
	 * @return the input position, or null if validation failed
	 * 
	 * @param entity the entity doing the pathing
	 * @param pos the candidate position
	 */
	@Nullable
	public static BlockPos validate(PathAwareEntity entity, BlockPos pos) {
		pos = FuzzyPositions.upWhile(pos, entity.world.getTopY(), currentPos -> NavigationConditions.isSolidAt(entity, currentPos));
		return !NavigationConditions.isWaterAt(entity, pos) && !NavigationConditions.hasPathfindingPenalty(entity, pos) ? pos : null;
	}

	/**
	 * Paths to a random reachable position approaching an entity's chosen {@link net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}.
	 * 
	 * @return the chosen position or null if none could be found
	 * 
	 * @param entity the entity doing the pathing
	 * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
	 */
	@Nullable
	public static BlockPos towardTarget(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos relativeInRangePos) {
		BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), relativeInRangePos);
		return !NavigationConditions.isHeightInvalid(blockPos, entity)
				&& !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos)
				&& !NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)
			? blockPos
			: null;
	}
}
