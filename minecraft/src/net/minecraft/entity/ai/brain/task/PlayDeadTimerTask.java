package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class PlayDeadTimerTask {
	public static Task<LivingEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS), context.queryMemoryOptional(MemoryModuleType.HURT_BY_ENTITY))
					.apply(context, (playDeadTicks, hurtByEntity) -> (world, entity, time) -> {
							int i = context.<Integer>getValue(playDeadTicks);
							if (i <= 0) {
								playDeadTicks.forget();
								hurtByEntity.forget();
								entity.getBrain().resetPossibleActivities();
							} else {
								playDeadTicks.remember(i - 1);
							}

							return true;
						})
		);
	}
}
