package net.minecraft.entity.ai.brain.task;

import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.world.GameRules;

public class DefeatTargetTask {
	public static Task<LivingEntity> create(int celebrationDuration, BiPredicate<LivingEntity, LivingEntity> predicate) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.ANGRY_AT),
						context.queryMemoryAbsent(MemoryModuleType.CELEBRATE_LOCATION),
						context.queryMemoryOptional(MemoryModuleType.DANCING)
					)
					.apply(context, (attackTarget, angryAt, celebrateLocation, dancing) -> (world, entity, time) -> {
							LivingEntity livingEntity = context.getValue(attackTarget);
							if (!livingEntity.isDead()) {
								return false;
							} else {
								if (predicate.test(entity, livingEntity)) {
									dancing.remember(true, (long)celebrationDuration);
								}

								celebrateLocation.remember(livingEntity.getBlockPos(), (long)celebrationDuration);
								if (livingEntity.getType() != EntityType.PLAYER || world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
									attackTarget.forget();
									angryAt.forget();
								}

								return true;
							}
						})
		);
	}
}
