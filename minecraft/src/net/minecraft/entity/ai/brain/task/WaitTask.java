package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class WaitTask extends Task<LivingEntity> {
	public WaitTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(), minRunTime, maxRunTime);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
		return true;
	}
}
