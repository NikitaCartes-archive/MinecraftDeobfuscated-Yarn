package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class WakeUpTask extends Task<LivingEntity> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of();
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return !livingEntity.getBrain().hasActivity(Activity.field_18597) && livingEntity.isSleeping();
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		livingEntity.wakeUp();
	}
}
