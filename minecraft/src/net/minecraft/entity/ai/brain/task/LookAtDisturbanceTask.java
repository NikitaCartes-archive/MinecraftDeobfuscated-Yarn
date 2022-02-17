package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask extends Task<WardenEntity> {
	public static final int field_36730 = 40;

	public LookAtDisturbanceTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.LAST_DISTURBANCE,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.DISTURBANCE_LOCATION,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		long l = (Long)brain.getOptionalMemory(MemoryModuleType.LAST_DISTURBANCE).get();
		return serverWorld.getTime() - l < 40L;
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Brain<WardenEntity> brain = wardenEntity.getBrain();
		brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget((BlockPos)brain.getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION).get()));
	}
}
