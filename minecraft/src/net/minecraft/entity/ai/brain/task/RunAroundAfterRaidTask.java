package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;

public class RunAroundAfterRaidTask extends FindWalkTargetTask {
	public RunAroundAfterRaidTask(float f) {
		super(f);
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Raid raid = serverWorld.getRaidAt(mobEntityWithAi.getSenseCenterPos());
		return raid != null && raid.hasWon() && super.shouldRun(serverWorld, mobEntityWithAi);
	}
}
