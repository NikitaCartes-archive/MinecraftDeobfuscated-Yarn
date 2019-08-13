package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.server.world.ServerWorld;

public class WakeUpTask extends Task<LivingEntity> {
	public WakeUpTask() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return !livingEntity.getBrain().hasActivity(Activity.field_18597) && livingEntity.isSleeping();
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		livingEntity.wakeUp();
	}
}
