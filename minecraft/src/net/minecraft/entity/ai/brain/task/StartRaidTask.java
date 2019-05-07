package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class StartRaidTask extends Task<LivingEntity> {
	public StartRaidTask() {
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
		if (raid != null) {
			if (raid.hasSpawned() && !raid.isPreRaid()) {
				brain.setDefaultActivity(Activity.field_19041);
				brain.resetPossibleActivities(Activity.field_19041);
			} else {
				brain.setDefaultActivity(Activity.field_19042);
				brain.resetPossibleActivities(Activity.field_19042);
			}
		}
	}
}
