package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class StopPanicingTask extends Task<VillagerEntity> {
	public StopPanicingTask() {
		super(ImmutableMap.of());
	}

	protected void method_20645(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		boolean bl = PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity) || wasHurtByNearbyEntity(villagerEntity);
		if (!bl) {
			villagerEntity.getBrain().forget(MemoryModuleType.HURT_BY);
			villagerEntity.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
			villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
		}
	}

	private static boolean wasHurtByNearbyEntity(VillagerEntity villagerEntity) {
		return villagerEntity.getBrain()
			.getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY)
			.filter(livingEntity -> livingEntity.squaredDistanceTo(villagerEntity) <= 36.0)
			.isPresent();
	}
}
