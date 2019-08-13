package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class SeekSkyAfterRaidWinTask extends SeekSkyTask {
	public SeekSkyAfterRaidWinTask(float f) {
		super(f);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Raid raid = serverWorld.getRaidAt(new BlockPos(livingEntity));
		return raid != null && raid.hasWon() && super.shouldRun(serverWorld, livingEntity);
	}
}
