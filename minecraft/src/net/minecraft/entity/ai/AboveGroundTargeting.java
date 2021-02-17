package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Similar to {@link FuzzyTargeting}, but the positions this class' utility methods
 * find never have pathfinding penalties and are always above ground or water.
 */
public class AboveGroundTargeting {
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, double x, double z, float angle, int maxAboveSolid, int minAboveSolid) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(
			entity,
			() -> {
				BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, x, z, (double)angle);
				if (blockPos == null) {
					return null;
				} else {
					BlockPos blockPos2 = FuzzyTargeting.tryMake(entity, horizontalRange, bl, blockPos);
					if (blockPos2 == null) {
						return null;
					} else {
						blockPos2 = FuzzyPositions.upWhile(
							blockPos2,
							entity.getRandom().nextInt(maxAboveSolid - minAboveSolid + 1) + minAboveSolid,
							entity.world.getTopY(),
							pos -> NavigationConditions.isSolidAt(entity, pos)
						);
						return !NavigationConditions.isWaterAt(entity, blockPos2) && !NavigationConditions.hasPathfindingPenalty(entity, blockPos2) ? blockPos2 : null;
					}
				}
			}
		);
	}
}
