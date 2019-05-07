package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class PanicTask extends Task<LivingEntity> {
	public PanicTask() {
		super(ImmutableMap.of());
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (wasHurt(livingEntity) || isHostileNearby(livingEntity)) {
			Brain<?> brain = livingEntity.getBrain();
			if (!brain.hasActivity(Activity.field_18599)) {
				brain.forget(MemoryModuleType.field_18449);
				brain.forget(MemoryModuleType.field_18445);
				brain.forget(MemoryModuleType.field_18446);
				brain.forget(MemoryModuleType.field_18448);
				brain.forget(MemoryModuleType.field_18447);
			}

			brain.resetPossibleActivities(Activity.field_18599);
		}
	}

	public static boolean isHostileNearby(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18453);
	}

	public static boolean wasHurt(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18451);
	}
}
