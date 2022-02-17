package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class DigTask<E extends WardenEntity> extends Task<E> {
	public DigTask(int duration) {
		super(
			ImmutableMap.of(
				MemoryModuleType.IS_DIGGING,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			),
			duration
		);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		return this.shouldRun(serverWorld, wardenEntity);
	}

	protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
		return (Boolean)wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.IS_DIGGING).orElse(false);
	}

	protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.setPose(EntityPose.DIGGING);
		wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_DIG, 5.0F, 1.0F);
	}

	protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.remove(Entity.RemovalReason.DISCARDED);
	}
}
