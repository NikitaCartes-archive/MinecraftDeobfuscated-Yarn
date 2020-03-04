package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;

public class HideInHomeDuringRaidTask extends HideInHomeTask {
	public HideInHomeDuringRaidTask(int maxDistance, float walkSpeed) {
		super(maxDistance, walkSpeed, 1);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		Raid raid = world.getRaidAt(entity.getSenseCenterPos());
		return super.shouldRun(world, entity) && raid != null && raid.isActive() && !raid.hasWon() && !raid.hasLost();
	}
}
