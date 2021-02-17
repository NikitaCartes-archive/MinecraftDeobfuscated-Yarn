package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Similar to {@link FuzzyTargeting}, but the positions this class' utility methods
 * find are never water.
 */
public class NoWaterTargeting {
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, int startHeight, Vec3d direction, double angleRange) {
		Vec3d vec3d = direction.subtract(entity.getX(), entity.getY(), entity.getZ());
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = NoPenaltySolidTargeting.tryMake(entity, horizontalRange, verticalRange, startHeight, vec3d.x, vec3d.z, angleRange, bl);
			return blockPos != null && !NavigationConditions.isWaterAt(entity, blockPos) ? blockPos : null;
		});
	}
}
