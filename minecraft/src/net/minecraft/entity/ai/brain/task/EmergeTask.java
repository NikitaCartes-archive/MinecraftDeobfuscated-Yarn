package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class EmergeTask<E extends WardenEntity> extends MultiTickTask<E> {
	public EmergeTask(int duration) {
		super(
			ImmutableMap.of(
				MemoryModuleType.IS_EMERGING,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			),
			duration
		);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		return true;
	}

	protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.setPose(EntityPose.EMERGING);
		wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_EMERGE, 5.0F, 1.0F);
	}

	protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		if (wardenEntity.isInPose(EntityPose.EMERGING)) {
			wardenEntity.setPose(EntityPose.STANDING);
		}
	}
}
