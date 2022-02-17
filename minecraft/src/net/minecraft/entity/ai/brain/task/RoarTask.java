package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class RoarTask extends Task<WardenEntity> {
	private static final int field_36727 = 25;

	public RoarTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.ROAR_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LAST_ROAR,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.CURRENT_ROAR_STARTED,
				MemoryModuleState.REGISTERED
			),
			WardenBrain.ROAR_DURATION
		);
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.remember(MemoryModuleType.CURRENT_ROAR_STARTED, l);
		brain.forget(MemoryModuleType.WALK_TARGET);
		wardenEntity.setPose(EntityPose.ROARING);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		return wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_TARGET);
	}

	protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Optional<Long> optional = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.CURRENT_ROAR_STARTED);
		optional.ifPresent(long_ -> {
			Optional<Long> optionalx = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.LAST_ROAR);
			boolean bl = optionalx.isEmpty() || l > (Long)optionalx.get() + (long)WardenBrain.ROAR_DURATION;
			if (bl && l >= 25L + long_) {
				wardenEntity.getBrain().remember(MemoryModuleType.LAST_ROAR, l);
				wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 3.0F, 1.0F);
			}
		});
	}

	protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.forget(MemoryModuleType.LAST_ROAR);
		brain.forget(MemoryModuleType.CURRENT_ROAR_STARTED);
		wardenEntity.setPose(EntityPose.STANDING);
	}
}
