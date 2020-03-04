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
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.PACIFIED,
				MemoryModuleState.VALUE_ABSENT,
				requiredMemoryModuleType,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.duration = duration;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().remember(MemoryModuleType.PACIFIED, true, (long)this.duration);
		entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
	}
}
