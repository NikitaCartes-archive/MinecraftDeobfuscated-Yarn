package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class HideInHomeDuringRaidTask extends HideInHomeTask {
	public HideInHomeDuringRaidTask(int i, float f) {
		super(i, f, 1);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Raid raid = serverWorld.getRaidAt(new BlockPos(livingEntity));
		return super.shouldRun(serverWorld, livingEntity) && raid != null && raid.isActive() && !raid.hasWon() && !raid.hasLost();
	}
}
