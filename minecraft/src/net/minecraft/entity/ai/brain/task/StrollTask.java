package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class StrollTask {
	private static final int DEFAULT_HORIZONTAL_RADIUS = 10;
	private static final int DEFAULT_VERTICAL_RADIUS = 7;
	private static final int[][] RADII = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

	public static SingleTickTask<PathAwareEntity> create(float speed) {
		return create(speed, true);
	}

	public static SingleTickTask<PathAwareEntity> create(float speed, boolean strollInsideWater) {
		return create(speed, entity -> FuzzyTargeting.find(entity, 10, 7), strollInsideWater ? entity -> true : entity -> !entity.isInsideWaterOrBubbleColumn());
	}

	public static Task<PathAwareEntity> create(float speed, int horizontalRadius, int verticalRadius) {
		return create(speed, entity -> FuzzyTargeting.find(entity, horizontalRadius, verticalRadius), entity -> true);
	}

	public static Task<PathAwareEntity> createSolidTargeting(float speed) {
		return create(speed, entity -> findTargetPos(entity, 10, 7), entity -> true);
	}

	public static Task<PathAwareEntity> createDynamicRadius(float speed) {
		return create(speed, StrollTask::findTargetPos, Entity::isInsideWaterOrBubbleColumn);
	}

	private static SingleTickTask<PathAwareEntity> create(float speed, Function<PathAwareEntity, Vec3d> targetGetter, Predicate<PathAwareEntity> shouldRun) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, walkTarget -> (world, entity, time) -> {
						if (!shouldRun.test(entity)) {
							return false;
						} else {
							Optional<Vec3d> optional = Optional.ofNullable((Vec3d)targetGetter.apply(entity));
							walkTarget.remember(optional.map(pos -> new WalkTarget(pos, speed, 0)));
							return true;
						}
					})
		);
	}

	@Nullable
	private static Vec3d findTargetPos(PathAwareEntity entity) {
		Vec3d vec3d = null;
		Vec3d vec3d2 = null;

		for (int[] is : RADII) {
			if (vec3d == null) {
				vec3d2 = LookTargetUtil.find(entity, is[0], is[1]);
			} else {
				vec3d2 = entity.getPos().add(entity.getPos().relativize(vec3d).normalize().multiply((double)is[0], (double)is[1], (double)is[0]));
			}

			if (vec3d2 == null || entity.world.getFluidState(new BlockPos(vec3d2)).isEmpty()) {
				return vec3d;
			}

			vec3d = vec3d2;
		}

		return vec3d2;
	}

	@Nullable
	private static Vec3d findTargetPos(PathAwareEntity entity, int horizontalRadius, int verticalRadius) {
		Vec3d vec3d = entity.getRotationVec(0.0F);
		return NoPenaltySolidTargeting.find(entity, horizontalRadius, verticalRadius, -2, vec3d.x, vec3d.z, (float) (Math.PI / 2));
	}
}
