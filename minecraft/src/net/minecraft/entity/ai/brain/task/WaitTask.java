package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class WaitTask extends Task<LivingEntity> {
	public WaitTask(int i, int j) {
		super(ImmutableMap.of(), i, j);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		return true;
	}
}
