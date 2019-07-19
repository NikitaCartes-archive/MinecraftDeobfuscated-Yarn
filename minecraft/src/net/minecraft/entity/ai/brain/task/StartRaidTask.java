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
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		return world.random.nextInt(20) == 0;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		Raid raid = world.getRaidAt(new BlockPos(entity));
		if (raid != null) {
			if (raid.hasSpawned() && !raid.isPreRaid()) {
				brain.setDefaultActivity(Activity.RAID);
				brain.resetPossibleActivities(Activity.RAID);
			} else {
				brain.setDefaultActivity(Activity.PRE_RAID);
				brain.resetPossibleActivities(Activity.PRE_RAID);
			}
		}
	}
}
