package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class ScheduleActivityTask {
	public static Task<LivingEntity> create() {
		return TaskTriggerer.task(context -> context.point((TaskRunnable<LivingEntity>)(world, entity, time) -> {
				entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
				return true;
			}));
	}
}
