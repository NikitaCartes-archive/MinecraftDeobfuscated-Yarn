package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class ScheduleActivityTask extends Task<LivingEntity> {
	public ScheduleActivityTask() {
		super(ImmutableMap.of());
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
	}
}
