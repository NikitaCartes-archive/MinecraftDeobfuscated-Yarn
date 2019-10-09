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
	public static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
		return findTarget(mobEntityWithAi, i, j, null);
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
	public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
		return findGroundTarget(mobEntityWithAi, i, j, mobEntityWithAi::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
		return method_21758(mobEntityWithAi, i, j, null, false, 0.0, toDoubleFunction);
	}

	@Nullable
	public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.getX(), mobEntityWithAi.getY(), mobEntityWithAi.getZ());
		return findTarget(mobEntityWithAi, i, j, vec3d2);
	}

	@Nullable
	public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, double d) {
		Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.getX(), mobEntityWithAi.getY(), mobEntityWithAi.getZ());
		return method_21758(mobEntityWithAi, i, j, vec3d2, true, d, mobEntityWithAi::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = mobEntityWithAi.getPos().subtract(vec3d);
		return method_21758(mobEntityWithAi, i, j, vec3d2, false, (float) (Math.PI / 2), mobEntityWithAi::getPathfindingFavor);
	}

	@Nullable
	public static Vec3d findTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = mobEntityWithAi.getPos().subtract(vec3d);
		return findTarget(mobEntityWithAi, i, j, vec3d2);
	}

	@Nullable
	public static Vec3d method_21756(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
		return findTarget(mobEntityWithAi, i, j, k, vec3d, true, d, mobEntityWithAi::getPathfindingFavor, false, blockPos -> false, 0, 0, false);
	}

	@Nullable
	private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d) {
		return method_21758(mobEntityWithAi, i, j, vec3d, true, (float) (Math.PI / 2), mobEntityWithAi::getPathfindingFavor);
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
		MobEntityWithAi mobEntityWithAi,
		int i,
		int j,
		int k,
		@Nullable Vec3d vec3d,
		boolean bl,
		double d,
		ToDoubleFunction<BlockPos> toDoubleFunction,
		boolean bl2,
		Predicate<BlockPos> predicate,
		int l,
		int m,
		boolean bl3
	) {
		EntityNavigation entityNavigation = mobEntityWithAi.getNavigation();
		Random random = mobEntityWithAi.getRandom();
		boolean bl4;
		if (mobEntityWithAi.hasPositionTarget()) {
			bl4 = mobEntityWithAi.getPositionTarget().isWithinDistance(mobEntityWithAi.getPos(), (double)(mobEntityWithAi.getPositionTargetRange() + (float)i) + 1.0);
		} else {
			bl4 = false;
		}

		boolean bl5 = false;
		double e = Double.NEGATIVE_INFINITY;
		BlockPos blockPos = new BlockPos(mobEntityWithAi);

		for (int n = 0; n < 10; n++) {
			BlockPos blockPos2 = method_6374(random, i, j, k, vec3d, d);
			if (blockPos2 != null) {
				int o = blockPos2.getX();
				int p = blockPos2.getY();
				int q = blockPos2.getZ();
				if (mobEntityWithAi.hasPositionTarget() && i > 1) {
					BlockPos blockPos3 = mobEntityWithAi.getPositionTarget();
					if (mobEntityWithAi.getX() > (double)blockPos3.getX()) {
						o -= random.nextInt(i / 2);
					} else {
						o += random.nextInt(i / 2);
					}

					if (mobEntityWithAi.getZ() > (double)blockPos3.getZ()) {
						q -= random.nextInt(i / 2);
					} else {
						q += random.nextInt(i / 2);
					}
				}

				BlockPos blockPos3x = new BlockPos((double)o + mobEntityWithAi.getX(), (double)p + mobEntityWithAi.getY(), (double)q + mobEntityWithAi.getZ());
				if ((!bl4 || mobEntityWithAi.isInWalkTargetRange(blockPos3x)) && (!bl3 || entityNavigation.isValidPosition(blockPos3x))) {
					if (bl2) {
						blockPos3x = method_21761(blockPos3x, random.nextInt(l + 1) + m, mobEntityWithAi.world.getHeight(), predicate);
					}

					if (bl || !isWater(blockPos3x, mobEntityWithAi)) {
						double f = toDoubleFunction.applyAsDouble(blockPos3x);
						if (f > e) {
							e = f;
							blockPos = blockPos3x;
							bl5 = true;
						}
					}
				}
			}
		}

		return bl5 ? new Vec3d(blockPos) : null;
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

	private static boolean isWater(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
		return mobEntityWithAi.world.getFluidState(blockPos).matches(FluidTags.WATER);
	}
}
