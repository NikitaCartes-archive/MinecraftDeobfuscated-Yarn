package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask extends Task<WardenEntity> {
	public LookAtDisturbanceTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.DISTURBANCE_LOCATION,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ROAR_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
		return wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)
			|| wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_TARGET);
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		BlockPos blockPos = (BlockPos)wardenEntity.getBrain()
			.getOptionalMemory(MemoryModuleType.ROAR_TARGET)
			.map(Entity::getBlockPos)
			.or(() -> wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION))
			.get();
		wardenEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
	}
}
