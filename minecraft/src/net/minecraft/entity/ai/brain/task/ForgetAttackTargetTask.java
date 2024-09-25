package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask {
	private static final int REMEMBER_TIME = 200;

	public static <E extends MobEntity> Task<E> create(ForgetAttackTargetTask.ForgetCallback<E> callback) {
		return create((world, target) -> false, callback, true);
	}

	public static <E extends MobEntity> Task<E> create(ForgetAttackTargetTask.AlternativeCondition condition) {
		return create(condition, (world, entity, target) -> {
		}, true);
	}

	public static <E extends MobEntity> Task<E> create() {
		return create((world, target) -> false, (world, entity, target) -> {
		}, true);
	}

	public static <E extends MobEntity> Task<E> create(
		ForgetAttackTargetTask.AlternativeCondition condition, ForgetAttackTargetTask.ForgetCallback<E> callback, boolean shouldForgetIfTargetUnreachable
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
									&& !condition.test(world, livingEntity)) {
									return true;
								} else {
									callback.accept(world, (E)entity, livingEntity);
									attackTarget.forget();
									return true;
								}
							}
					)
		);
	}

	private static boolean cannotReachTarget(LivingEntity target, Optional<Long> lastReachTime) {
		return lastReachTime.isPresent() && target.getWorld().getTime() - (Long)lastReachTime.get() > 200L;
	}

	@FunctionalInterface
	public interface AlternativeCondition {
		boolean test(ServerWorld world, LivingEntity target);
	}

	@FunctionalInterface
	public interface ForgetCallback<E> {
		void accept(ServerWorld world, E entity, LivingEntity target);
	}
}
