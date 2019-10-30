package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetFinder {
	@Nullable
	public static Vec3d findTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance) {
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, null);
	}

	@Nullable
	public static Vec3d method_21757(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, float f, int k, int l) {
		return findTarget(
			mobEntityWithAi,
			i,
			j,
			0,
			vec3d,
			true,
			(double)f,
			mobEntityWithAi::getPathfindingFavor,
			true,
			blockPos -> mobEntityWithAi.getNavigation().isValidPosition(blockPos),
			k,
			l,
			true
		);
	}

	@Nullable
	public static Vec3d findGroundTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance) {
		return findGroundTarget(mob, maxHorizontalDistance, maxVerticalDistance, mob::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findGroundTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, ToDoubleFunction<BlockPos> pathfindingFavor) {
		return method_21758(mob, maxHorizontalDistance, maxVerticalDistance, null, false, 0.0, pathfindingFavor);
	}

	@Nullable
	public static Vec3d findTargetTowards(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, vec3d);
	}

	@Nullable
	public static Vec3d findTargetTowards(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos, double maxAngleDifference) {
		Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
		return method_21758(mob, maxHorizontalDistance, maxVerticalDistance, vec3d, true, maxAngleDifference, mob::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = mob.getPos().subtract(pos);
		return method_21758(mob, maxHorizontalDistance, maxVerticalDistance, vec3d, false, (float) (Math.PI / 2), mob::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findTargetAwayFrom(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
		Vec3d vec3d = mob.getPos().subtract(pos);
		return findTarget(mob, maxHorizontalDistance, maxVerticalDistance, vec3d);
	}

	@Nullable
	public static Vec3d method_21756(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
		return findTarget(mobEntityWithAi, i, j, k, vec3d, true, d, mobEntityWithAi::getPathfindingFavor, false, blockPos -> false, 0, 0, false);
	}

	@Nullable
	private static Vec3d findTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, @Nullable Vec3d direction) {
		return method_21758(mob, maxHorizontalDistance, maxVerticalDistance, direction, true, (float) (Math.PI / 2), mob::getPathfindingFavor);
	}

	@Nullable
	private static Vec3d method_21758(
		MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction
	) {
		return findTarget(
			mobEntityWithAi, i, j, 0, vec3d, bl, d, toDoubleFunction, !bl, blockPos -> mobEntityWithAi.world.getBlockState(blockPos).getMaterial().isSolid(), 0, 0, true
		);
	}

	@Nullable
	private static Vec3d findTarget(
		MobEntityWithAi mob,
		int maxHorizontalDistance,
		int maxVerticalDistance,
		int i,
		@Nullable Vec3d direction,
		boolean anywhere,
		double maxAngleDifference,
		ToDoubleFunction<BlockPos> favorProvider,
		boolean bl,
		Predicate<BlockPos> predicate,
		int j,
		int k,
		boolean bl2
	) {
		EntityNavigation entityNavigation = mob.getNavigation();
		Random random = mob.getRandom();
		boolean bl3;
		if (mob.hasPositionTarget()) {
			bl3 = mob.getPositionTarget().isWithinDistance(mob.getPos(), (double)(mob.getPositionTargetRange() + (float)maxHorizontalDistance) + 1.0);
		} else {
			bl3 = false;
		}

		boolean bl4 = false;
		double d = Double.NEGATIVE_INFINITY;
		BlockPos blockPos = new BlockPos(mob);

		for (int l = 0; l < 10; l++) {
			BlockPos blockPos2 = method_6374(random, maxHorizontalDistance, maxVerticalDistance, i, direction, maxAngleDifference);
			if (blockPos2 != null) {
				int m = blockPos2.getX();
				int n = blockPos2.getY();
				int o = blockPos2.getZ();
				if (mob.hasPositionTarget() && maxHorizontalDistance > 1) {
					BlockPos blockPos3 = mob.getPositionTarget();
					if (mob.getX() > (double)blockPos3.getX()) {
						m -= random.nextInt(maxHorizontalDistance / 2);
					} else {
						m += random.nextInt(maxHorizontalDistance / 2);
					}

					if (mob.getZ() > (double)blockPos3.getZ()) {
						o -= random.nextInt(maxHorizontalDistance / 2);
					} else {
						o += random.nextInt(maxHorizontalDistance / 2);
					}
				}

				BlockPos blockPos3x = new BlockPos((double)m + mob.getX(), (double)n + mob.getY(), (double)o + mob.getZ());
				if ((!bl3 || mob.isInWalkTargetRange(blockPos3x)) && (!bl2 || entityNavigation.isValidPosition(blockPos3x))) {
					if (bl) {
						blockPos3x = method_21761(blockPos3x, random.nextInt(j + 1) + k, mob.world.getHeight(), predicate);
					}

					if (anywhere || !isWater(blockPos3x, mob)) {
						double e = favorProvider.applyAsDouble(blockPos3x);
						if (e > d) {
							d = e;
							blockPos = blockPos3x;
							bl4 = true;
						}
					}
				}
			}
		}

		return bl4 ? new Vec3d(blockPos) : null;
	}

	@Nullable
	private static BlockPos method_6374(Random random, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
		if (vec3d != null && !(d >= Math.PI)) {
			double e = MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
			double f = e + (double)(2.0F * random.nextFloat() - 1.0F) * d;
			double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
			double h = -g * Math.sin(f);
			double o = g * Math.cos(f);
			if (!(Math.abs(h) > (double)i) && !(Math.abs(o) > (double)i)) {
				int p = random.nextInt(2 * j + 1) - j + k;
				return new BlockPos(h, (double)p, o);
			} else {
				return null;
			}
		} else {
			int l = random.nextInt(2 * i + 1) - i;
			int m = random.nextInt(2 * j + 1) - j + k;
			int n = random.nextInt(2 * i + 1) - i;
			return new BlockPos(l, m, n);
		}
	}

	static BlockPos method_21761(BlockPos blockPos, int i, int j, Predicate<BlockPos> predicate) {
		if (i < 0) {
			throw new IllegalArgumentException("aboveSolidAmount was " + i + ", expected >= 0");
		} else if (!predicate.test(blockPos)) {
			return blockPos;
		} else {
			BlockPos blockPos2 = blockPos.up();

			while (blockPos2.getY() < j && predicate.test(blockPos2)) {
				blockPos2 = blockPos2.up();
			}

			BlockPos blockPos3 = blockPos2;

			while (blockPos3.getY() < j && blockPos3.getY() - blockPos2.getY() < i) {
				BlockPos blockPos4 = blockPos3.up();
				if (predicate.test(blockPos4)) {
					break;
				}

				blockPos3 = blockPos4;
			}

			return blockPos3;
		}
	}

	private static boolean isWater(BlockPos pos, MobEntityWithAi mob) {
		return mob.world.getFluidState(pos).matches(FluidTags.WATER);
	}
}
