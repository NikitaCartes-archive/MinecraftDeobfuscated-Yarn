package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;

public class UpdateAttackTargetTask {
	public static <E extends MobEntity> Task<E> create(Function<E, Optional<? extends LivingEntity>> targetGetter) {
		return create(entity -> true, targetGetter);
	}

	public static <E extends MobEntity> Task<E> create(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
					)
					.apply(context, (attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
							if (!startCondition.test(entity)) {
								return false;
							} else {
								Optional<? extends LivingEntity> optional = (Optional<? extends LivingEntity>)targetGetter.apply(entity);
								if (optional.isEmpty()) {
									return false;
								} else {
									LivingEntity livingEntity = (LivingEntity)optional.get();
									if (!entity.canTarget(livingEntity)) {
										return false;
									} else {
										attackTarget.remember(livingEntity);
										cantReachWalkTargetSince.forget();
										return true;
									}
								}
							}
						})
		);
	}
}
