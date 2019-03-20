package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class PanicTask extends Task<LivingEntity> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of();
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (method_19575(livingEntity) || method_19574(livingEntity)) {
			Brain<?> brain = livingEntity.getBrain();
			if (!brain.hasActivity(Activity.field_18599)) {
				brain.forget(MemoryModuleType.field_18449);
				brain.forget(MemoryModuleType.field_18445);
				brain.forget(MemoryModuleType.field_18446);
				brain.forget(MemoryModuleType.field_18448);
				brain.forget(MemoryModuleType.field_18447);
			}

			brain.method_18880(Activity.field_18599);
		}
	}

	public static boolean method_19574(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18453);
	}

	public static boolean method_19575(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18451);
	}
}
