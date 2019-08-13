package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RunAroundAfterRaidTask extends FindWalkTargetTask {
	public RunAroundAfterRaidTask(float f) {
		super(f);
	}

	protected boolean method_19989(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Raid raid = serverWorld.getRaidAt(new BlockPos(mobEntityWithAi));
		return raid != null && raid.hasWon() && super.shouldRun(serverWorld, mobEntityWithAi);
	}
}
