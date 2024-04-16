package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToRememberedPositionTask {
	public static Task<PathAwareEntity> createPosBased(MemoryModuleType<BlockPos> posModule, float speed, int range, boolean requiresWalkTarget) {
		return create(posModule, speed, range, requiresWalkTarget, Vec3d::ofBottomCenter);
	}

	public static SingleTickTask<PathAwareEntity> createEntityBased(
		MemoryModuleType<? extends Entity> entityModule, float speed, int range, boolean requiresWalkTarget
	) {
		return create(entityModule, speed, range, requiresWalkTarget, Entity::getPos);
	}

	private static <T> SingleTickTask<PathAwareEntity> create(
		MemoryModuleType<T> posSource, float speed, int range, boolean requiresWalkTarget, Function<T, Vec3d> posGetter
	) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(posSource))
					.apply(context, (walkTarget, posSourcex) -> (world, entity, time) -> {
							Optional<WalkTarget> optional = context.getOptionalValue(walkTarget);
							if (optional.isPresent() && !requiresWalkTarget) {
								return false;
							} else {
								Vec3d vec3d = entity.getPos();
								Vec3d vec3d2 = (Vec3d)posGetter.apply(context.getValue(posSourcex));
								if (!vec3d.isInRange(vec3d2, (double)range)) {
									return false;
								} else {
									if (optional.isPresent() && ((WalkTarget)optional.get()).getSpeed() == speed) {
										Vec3d vec3d3 = ((WalkTarget)optional.get()).getLookTarget().getPos().subtract(vec3d);
										Vec3d vec3d4 = vec3d2.subtract(vec3d);
										if (vec3d3.dotProduct(vec3d4) < 0.0) {
											return false;
										}
									}

									for (int j = 0; j < 10; j++) {
										Vec3d vec3d4 = FuzzyTargeting.findFrom(entity, 16, 7, vec3d2);
										if (vec3d4 != null) {
											walkTarget.remember(new WalkTarget(vec3d4, speed, 0));
											break;
										}
									}

									return true;
								}
							}
						})
		);
	}
}
