package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4808 extends Task<LivingEntity> {
	private final int field_22287;

	public class_4808(MemoryModuleType<?> memoryModuleType, int i) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.PACIFIED,
				MemoryModuleState.VALUE_ABSENT,
				memoryModuleType,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.field_22287 = i;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().method_24525(MemoryModuleType.PACIFIED, true, time, (long)this.field_22287);
		entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
	}
}
