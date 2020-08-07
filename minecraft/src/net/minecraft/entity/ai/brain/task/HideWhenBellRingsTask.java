package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class HideWhenBellRingsTask extends Task<LivingEntity> {
	public HideWhenBellRingsTask() {
		super(ImmutableMap.of(MemoryModuleType.field_19009, MemoryModuleState.field_18456));
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		Raid raid = world.getRaidAt(entity.getBlockPos());
		if (raid == null) {
			brain.doExclusively(Activity.field_19043);
		}
	}
}
