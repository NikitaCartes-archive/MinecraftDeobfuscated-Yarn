package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.village.raid.Raid;

public class EndRaidTask {
	public static Task<LivingEntity> create() {
		return TaskTriggerer.task(context -> context.point((world, entity, time) -> {
				if (world.random.nextInt(20) != 0) {
					return false;
				} else {
					Brain<?> brain = entity.getBrain();
					Raid raid = world.getRaidAt(entity.getBlockPos());
					if (raid == null || raid.hasStopped() || raid.hasLost()) {
						brain.setDefaultActivity(Activity.IDLE);
						brain.refreshActivities(world.getTimeOfDay(), world.getTime());
					}

					return true;
				}
			}));
	}
}
