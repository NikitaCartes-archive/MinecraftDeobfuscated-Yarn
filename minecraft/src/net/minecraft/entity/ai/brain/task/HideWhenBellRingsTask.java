package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class HideWhenBellRingsTask extends Task<LivingEntity> {
	public HideWhenBellRingsTask() {
		super(ImmutableMap.of(MemoryModuleType.field_19009, MemoryModuleState.field_18456));
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Raid raid = serverWorld.getRaidAt(new BlockPos(livingEntity));
		if (raid == null) {
			brain.resetPossibleActivities(Activity.field_19043);
		}
	}
}
