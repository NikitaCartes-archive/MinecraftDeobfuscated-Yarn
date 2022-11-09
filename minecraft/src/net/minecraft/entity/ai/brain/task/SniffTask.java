package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class SniffTask<E extends WardenEntity> extends MultiTickTask<E> {
	private static final double HORIZONTAL_RADIUS = 6.0;
	private static final double VERTICAL_RADIUS = 20.0;

	public SniffTask(int runTime) {
		super(
			ImmutableMap.of(
				MemoryModuleType.IS_SNIFFING,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.DISTURBANCE_LOCATION,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.SNIFF_COOLDOWN,
				MemoryModuleState.REGISTERED
			),
			runTime
		);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		return true;
	}

	protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
		wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_SNIFF, 5.0F, 1.0F);
	}

	protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
		if (wardenEntity.isInPose(EntityPose.SNIFFING)) {
			wardenEntity.setPose(EntityPose.STANDING);
		}

		wardenEntity.getBrain().forget(MemoryModuleType.IS_SNIFFING);
		wardenEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(wardenEntity::isValidTarget).ifPresent(target -> {
			if (wardenEntity.isInRange(target, 6.0, 20.0)) {
				wardenEntity.increaseAngerAt(target);
			}

			if (!wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)) {
				WardenBrain.lookAtDisturbance(wardenEntity, target.getBlockPos());
			}
		});
	}
}
