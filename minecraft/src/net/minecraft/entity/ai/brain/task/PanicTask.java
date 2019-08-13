package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class PanicTask extends Task<VillagerEntity> {
	public PanicTask() {
		super(ImmutableMap.of());
	}

	protected boolean method_20646(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return wasHurt(villagerEntity) || isHostileNearby(villagerEntity);
	}

	protected void method_20647(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (wasHurt(villagerEntity) || isHostileNearby(villagerEntity)) {
			Brain<?> brain = villagerEntity.getBrain();
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

	protected void method_20648(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l % 100L == 0L) {
			villagerEntity.summonGolem(l, 3);
		}
	}

	public static boolean isHostileNearby(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18453);
	}

	public static boolean wasHurt(LivingEntity livingEntity) {
		return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.field_18451);
	}
}
