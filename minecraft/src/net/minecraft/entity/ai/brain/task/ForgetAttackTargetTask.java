package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;

public class ForgetAttackTargetTask {
	private static final int REMEMBER_TIME = 200;

	public static <E extends MobEntity> Task<E> create(BiConsumer<E, LivingEntity> forgetCallback) {
		return create(entity -> false, forgetCallback, true);
	}

	public static <E extends MobEntity> Task<E> create(Predicate<LivingEntity> alternativeCondition) {
		return create(alternativeCondition, (entity, target) -> {
		}, true);
	}

	public static <E extends MobEntity> Task<E> create() {
		return create(entity -> false, (entity, target) -> {
		}, true);
	}

	public static <E extends MobEntity> Task<E> create(
		Predicate<LivingEntity> alternativeCondition, BiConsumer<E, LivingEntity> forgetCallback, boolean shouldForgetIfTargetUnreachable
	) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
					)
					.apply(
						context,
						(attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
								LivingEntity livingEntity = context.getValue(attackTarget);
								if (entity.canTarget(livingEntity)
									&& (!shouldForgetIfTargetUnreachable || !cannotReachTarget(entity, context.getOptionalValue(cantReachWalkTargetSince)))
									&& livingEntity.isAlive()
									&& livingEntity.getWorld() == entity.getWorld()
									&& !alternativeCondition.test(livingEntity)) {
									return true;
								} else {
									forgetCallback.accept(entity, livingEntity);
									attackTarget.forget();
									return true;
								}
							}
					)
		);
	}

	private static boolean cannotReachTarget(LivingEntity livingEntity, Optional<Long> optional) {
		return optional.isPresent() && livingEntity.getWorld().getTime() - (Long)optional.get() > 200L;
	}
}
