package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class StopPanickingTask extends Task<VillagerEntity> {
	private static final int MAX_DISTANCE = 36;

	public StopPanickingTask() {
		super(ImmutableMap.of());
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		boolean bl = PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity) || wasHurtByNearbyEntity(villagerEntity);
		if (!bl) {
			villagerEntity.getBrain().forget(MemoryModuleType.HURT_BY);
			villagerEntity.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
			villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
		}
	}

	private static boolean wasHurtByNearbyEntity(VillagerEntity entity) {
		return entity.getBrain()
			.getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY)
			.filter(livingEntity -> livingEntity.squaredDistanceTo(entity) <= 36.0)
			.isPresent();
	}
}
