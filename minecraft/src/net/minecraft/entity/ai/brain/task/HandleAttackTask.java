package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class HandleAttackTask extends Task<LivingEntity> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of();
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		boolean bl = brain.hasMemoryModule(MemoryModuleType.field_18451) && brain.getMemory(MemoryModuleType.field_18451).isPresent();
		boolean bl2 = brain.hasMemoryModule(MemoryModuleType.field_18453) && brain.getMemory(MemoryModuleType.field_18453).isPresent();
		boolean bl3 = brain.hasMemoryModule(MemoryModuleType.field_18452)
			&& brain.getMemory(MemoryModuleType.field_18452).isPresent()
			&& ((LivingEntity)brain.getMemory(MemoryModuleType.field_18452).get()).squaredDistanceTo(livingEntity) <= 36.0;
		if (!bl && !bl2 && !bl3) {
			brain.forget(MemoryModuleType.field_18451);
			brain.forget(MemoryModuleType.field_18452);
			brain.doActivity(serverWorld.getTimeOfDay());
		}
	}
}
