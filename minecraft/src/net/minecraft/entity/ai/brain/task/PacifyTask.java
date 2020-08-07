package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class PacifyTask extends Task<LivingEntity> {
	private final int duration;

	public PacifyTask(MemoryModuleType<?> requiredMemoryModuleType, int duration) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_22353,
				MemoryModuleState.field_18457,
				requiredMemoryModuleType,
				MemoryModuleState.field_18456
			)
		);
		this.duration = duration;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().remember(MemoryModuleType.field_22353, true, (long)this.duration);
		entity.getBrain().forget(MemoryModuleType.field_22355);
	}
}
