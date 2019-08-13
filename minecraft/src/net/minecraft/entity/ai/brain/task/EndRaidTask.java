package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class EndRaidTask extends Task<LivingEntity> {
	public EndRaidTask() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return serverWorld.random.nextInt(20) == 0;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Raid raid = serverWorld.getRaidAt(new BlockPos(livingEntity));
		if (raid == null || raid.hasStopped() || raid.hasLost()) {
			brain.setDefaultActivity(Activity.field_18595);
			brain.refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
		}
	}
}
