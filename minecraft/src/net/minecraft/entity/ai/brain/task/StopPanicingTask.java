package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class StopPanicingTask extends Task<LivingEntity> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of();
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		boolean bl = PanicTask.wasHurt(livingEntity) || PanicTask.isHostileNearby(livingEntity) || wasHurtByNearbyEntity(livingEntity);
		if (!bl) {
			livingEntity.getBrain().forget(MemoryModuleType.field_18451);
			livingEntity.getBrain().forget(MemoryModuleType.field_18452);
			livingEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
		}
	}

	private static boolean wasHurtByNearbyEntity(LivingEntity livingEntity) {
		return livingEntity.getBrain()
			.getOptionalMemory(MemoryModuleType.field_18452)
			.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 36.0)
			.isPresent();
	}
}
