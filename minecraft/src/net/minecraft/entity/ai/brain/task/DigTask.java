package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class DigTask<E extends WardenEntity> extends MultiTickTask<E> {
	public DigTask(int duration) {
		super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), duration);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		return wardenEntity.getRemovalReason() == null;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
		return wardenEntity.isOnGround() || wardenEntity.isTouchingWater() || wardenEntity.isInLava();
	}

	protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
		if (wardenEntity.isOnGround()) {
			wardenEntity.setPose(EntityPose.DIGGING);
			wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_DIG, 5.0F, 1.0F);
		} else {
			wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F);
			this.finishRunning(serverWorld, wardenEntity, l);
		}
	}

	protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		if (wardenEntity.getRemovalReason() == null) {
			wardenEntity.remove(Entity.RemovalReason.DISCARDED);
		}
	}
}
