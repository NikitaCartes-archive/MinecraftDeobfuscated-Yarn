package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetFinder {
	@Nullable
	public static Vec3d findTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance) {
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, true, (float) (Math.PI / 2), mob::getPathfindingFavor, false, 0, 0, true);
	}

	@Nullable
	public static Vec3d findGroundTarget(
		PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference
	) {
		return findTarget(
			mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, true, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false
		);
	}

	@Nullable
	public static Vec3d findGroundTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance) {
		return findGroundTarget(mob, maxHorizontalDistance, maxVerticalDistance, mob::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findGroundTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, ToDoubleFunction<BlockPos> pathfindingFavor) {
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, false, 0.0, pathfindingFavor, true, 0, 0, true);
	}

	@Nullable
	public static Vec3d findAirTarget(
		PathAwareEntity mob,
		int maxHorizontalDistance,
		int maxVerticalDistance,
		Vec3d preferredAngle,
		float maxAngleDifference,
		int distanceAboveGroundRange,
		int minDistanceAboveGround
	) {
		return findTarget(
			mob,
			maxHorizontalDistance,
			maxVerticalDistance,
			0,
			preferredAngle,
			false,
			(double)maxAngleDifference,
			mob::getPathfindingFavor,
			true,
			distanceAboveGroundRange,
			minDistanceAboveGround,
			true
		);
	}

	@Nullable
	public static Vec3d method_27929(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.getX(), pathAwareEntity.getY(), pathAwareEntity.getZ());
		return findTarget(pathAwareEntity, i, j, 0, vec3d2, false, (float) (Math.PI / 2), pathAwareEntity::getPathfindingFavor, true, 0, 0, true);
	}

	@Nullable
	public static Vec3d findTargetTowards(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, (float) (Math.PI / 2), mob::getPathfindingFavor, false, 0, 0, true);
	}

	@Nullable
	public static Vec3d findTargetTowards(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos, double maxAngleDifference) {
		Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, maxAngleDifference, mob::getPathfindingFavor, false, 0, 0, true);
	}

	@Nullable
	public static Vec3d findGroundTargetTowards(
		PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, Vec3d pos, double maxAngleDifference
	) {
		Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
		return findTarget(
			mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, vec3d, false, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false
		);
	}

	@Nullable
	public static Vec3d findTargetAwayFrom(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = mob.getPos().subtract(pos);
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, (float) (Math.PI / 2), mob::getPathfindingFavor, false, 0, 0, true);
	}

	@Nullable
	public static Vec3d findGroundTargetAwayFrom(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = mob.getPos().subtract(pos);
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, false, (float) (Math.PI / 2), mob::getPathfindingFavor, true, 0, 0, true);
	}

	@Nullable
	private static Vec3d findTarget(
		PathAwareEntity mob,
		int maxHorizontalDistance,
		int maxVerticalDistance,
		int preferredYDifference,
		@Nullable Vec3d preferredAngle,
		boolean notInWater,
		double maxAngleDifference,
		ToDoubleFunction<BlockPos> favorProvider,
		boolean aboveGround,
		int distanceAboveGroundRange,
		int minDistanceAboveGround,
		boolean validPositionsOnly
	) {
		EntityNavigation entityNavigation = mob.getNavigation();
		Random random = mob.getRandom();
		boolean bl;
		if (mob.hasPositionTarget()) {
			bl = mob.getPositionTarget().isWithinDistance(mob.getPos(), (double)(mob.getPositionTargetRange() + (float)maxHorizontalDistance) + 1.0);
		} else {
			bl = false;
		}

		boolean bl2 = false;
		double d = Double.NEGATIVE_INFINITY;
		BlockPos blockPos = mob.getBlockPos();

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = getRandomOffset(random, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, maxAngleDifference);
			if (blockPos2 != null) {
				int j = blockPos2.getX();
				int k = blockPos2.getY();
				int l = blockPos2.getZ();
				if (mob.hasPositionTarget() && maxHorizontalDistance > 1) {
					BlockPos blockPos3 = mob.getPositionTarget();
					if (mob.getX() > (double)blockPos3.getX()) {
						j -= random.nextInt(maxHorizontalDistance / 2);
					} else {
						j += random.nextInt(maxHorizontalDistance / 2);
					}

					if (mob.getZ() > (double)blockPos3.getZ()) {
						l -= random.nextInt(maxHorizontalDistance / 2);
					} else {
						l += random.nextInt(maxHorizontalDistance / 2);
					}
				}

				BlockPos blockPos3x = new BlockPos((double)j + mob.getX(), (double)k + mob.getY(), (double)l + mob.getZ());
				if (blockPos3x.getY() >= 0
					&& blockPos3x.getY() <= mob.world.getHeight()
					&& (!bl || mob.isInWalkTargetRange(blockPos3x))
					&& (!validPositionsOnly || entityNavigation.isValidPosition(blockPos3x))) {
					if (aboveGround) {
						blockPos3x = findValidPositionAbove(
							blockPos3x,
							random.nextInt(distanceAboveGroundRange + 1) + minDistanceAboveGround,
							mob.world.getHeight(),
							blockPosx -> mob.world.getBlockState(blockPosx).getMaterial().isSolid()
						);
					}

					if (notInWater || !mob.world.getFluidState(blockPos3x).isIn(FluidTags.field_15517)) {
						PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(mob.world, blockPos3x.mutableCopy());
						if (mob.getPathfindingPenalty(pathNodeType) == 0.0F) {
							double e = favorProvider.applyAsDouble(blockPos3x);
							if (e > d) {
								d = e;
								blockPos = blockPos3x;
								bl2 = true;
							}
						}
					}
				}
			}
		}

		return bl2 ? Vec3d.ofBottomCenter(blockPos) : null;
	}

	@Nullable
	private static BlockPos getRandomOffset(
		Random random, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference
	) {
		if (preferredAngle != null && !(maxAngleDifference >= Math.PI)) {
			double d = MathHelper.atan2(preferredAngle.z, preferredAngle.x) - (float) (Math.PI / 2);
			double e = d + (double)(2.0F * random.nextFloat() - 1.0F) * maxAngleDifference;
			double f = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)maxHorizontalDistance;
			double g = -f * Math.sin(e);
			double h = f * Math.cos(e);
			if (!(Math.abs(g) > (double)maxHorizontalDistance) && !(Math.abs(h) > (double)maxHorizontalDistance)) {
				int l = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
				return new BlockPos(g, (double)l, h);
			} else {
				return null;
			}
		} else {
			int i = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
			int j = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
			int k = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
			return new BlockPos(i, j, k);
		}
	}

	static BlockPos findValidPositionAbove(BlockPos pos, int minDistanceAboveIllegal, int maxOffset, Predicate<BlockPos> illegalPredicate) {
		if (minDistanceAboveIllegal < 0) {
			throw new IllegalArgumentException("aboveSolidAmount was " + minDistanceAboveIllegal + ", expected >= 0");
		} else if (!illegalPredicate.test(pos)) {
			return pos;
		} else {
			BlockPos blockPos = pos.up();

			while (blockPos.getY() < maxOffset && illegalPredicate.test(blockPos)) {
				blockPos = blockPos.up();
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos2.getY() < maxOffset && blockPos2.getY() - blockPos.getY() < minDistanceAboveIllegal) {
				BlockPos blockPos3 = blockPos2.up();
				if (illegalPredicate.test(blockPos3)) {
					break;
				}

				blockPos2 = blockPos3;
			}

			return blockPos2;
		}
	}
}
