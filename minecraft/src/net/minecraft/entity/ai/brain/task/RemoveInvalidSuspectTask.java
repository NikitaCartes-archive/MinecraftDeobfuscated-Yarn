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

public class RemoveInvalidSuspectTask extends Task<WardenEntity> {
	public RemoveInvalidSuspectTask() {
		super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryModuleState.VALUE_PRESENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		return !WardenBrain.isValidTarget((LivingEntity)brain.getOptionalMemory(MemoryModuleType.ROAR_TARGET).get());
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.ROAR_TARGET).get();
		wardenEntity.getAngerManager().removeSuspect(livingEntity.getUuid());
		brain.forget(MemoryModuleType.ROAR_TARGET);
		wardenEntity.setPose(EntityPose.STANDING);
	}
}
