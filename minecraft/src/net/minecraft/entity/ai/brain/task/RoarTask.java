package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

public class RoarTask extends MultiTickTask<WardenEntity> {
	private static final int SOUND_DELAY = 25;
	private static final int ANGER_INCREASE = 20;

	public RoarTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.ROAR_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.ROAR_SOUND_COOLDOWN,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ROAR_SOUND_DELAY,
				MemoryModuleState.REGISTERED
			),
			WardenBrain.ROAR_DURATION
		);
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.remember(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
		brain.forget(MemoryModuleType.WALK_TARGET);
		LivingEntity livingEntity = (LivingEntity)wardenEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).get();
		LookTargetUtil.lookAt(wardenEntity, livingEntity);
		wardenEntity.setPose(EntityPose.ROARING);
		wardenEntity.increaseAngerAt(livingEntity, 20, false);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		return true;
	}

	protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		if (!wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_DELAY)
			&& !wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
			wardenEntity.getBrain().remember(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, (long)(WardenBrain.ROAR_DURATION - 25));
			wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 3.0F, 1.0F);
		}
	}

	protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		if (wardenEntity.isInPose(EntityPose.ROARING)) {
			wardenEntity.setPose(EntityPose.STANDING);
		}

		wardenEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).ifPresent(wardenEntity::updateAttackTarget);
		wardenEntity.getBrain().forget(MemoryModuleType.ROAR_TARGET);
	}
}
