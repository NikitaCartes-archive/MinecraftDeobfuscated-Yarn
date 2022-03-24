package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask extends Task<WardenEntity> {
	public LookAtDisturbanceTask() {
		super(ImmutableMap.of(MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT));
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		wardenEntity.getBrain()
			.remember(
				MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget((BlockPos)wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION).get())
			);
	}
}
