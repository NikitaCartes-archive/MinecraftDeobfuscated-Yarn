package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class SniffTask<E extends WardenEntity> extends Task<E> {
	public SniffTask(int duration) {
		super(
			ImmutableMap.of(
				MemoryModuleType.IS_SNIFFING,
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
		return (Boolean)wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.IS_SNIFFING).orElse(false);
	}

	protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_SNIFF, 5.0F, 1.0F);
	}

	protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.setPose(EntityPose.STANDING);
		wardenEntity.getBrain().forget(MemoryModuleType.IS_SNIFFING);
		wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE).ifPresent(attackable -> {
			Optional<Long> optional = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.LAST_DISTURBANCE);
			boolean bl = (Boolean)optional.map(long_ -> serverWorld.getTime() - long_ >= 100L).orElse(true);
			boolean bl2 = wardenEntity.distanceTo(attackable) <= 6.0F;
			if (bl2) {
				wardenEntity.increaseAngerFor(attackable);
				WardenBrain.setLastDisturbance(wardenEntity, l);
			}

			if (bl) {
				WardenBrain.lookAtDisturbance(wardenEntity, attackable.getBlockPos());
			}
		});
	}
}
