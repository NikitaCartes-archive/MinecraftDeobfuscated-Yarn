package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class PanicTask extends MultiTickTask<VillagerEntity> {
	public PanicTask() {
		super(ImmutableMap.of());
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return wasHurt(villagerEntity) || isHostileNearby(villagerEntity);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (wasHurt(villagerEntity) || isHostileNearby(villagerEntity)) {
			Brain<?> brain = villagerEntity.getBrain();
			if (!brain.hasActivity(Activity.PANIC)) {
				brain.forget(MemoryModuleType.PATH);
				brain.forget(MemoryModuleType.WALK_TARGET);
				brain.forget(MemoryModuleType.LOOK_TARGET);
				brain.forget(MemoryModuleType.BREED_TARGET);
				brain.forget(MemoryModuleType.INTERACTION_TARGET);
			}

			brain.doExclusively(Activity.PANIC);
		}
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l % 100L == 0L) {
			villagerEntity.summonGolem(serverWorld, l, 3);
		}
	}

	public static boolean isHostileNearby(LivingEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
	}

	public static boolean wasHurt(LivingEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
	}
}
