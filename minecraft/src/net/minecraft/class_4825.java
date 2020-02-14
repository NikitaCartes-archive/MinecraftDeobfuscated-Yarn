package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4825 extends Task<LivingEntity> {
	private final int field_22327;

	public class_4825(int i) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ANGRY_AT,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.CELEBRATE_LOCATION,
				MemoryModuleState.VALUE_ABSENT
			)
		);
		this.field_22327 = i;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		return this.method_24616(entity).getHealth() <= 0.0F;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		BlockPos blockPos = this.method_24616(entity).method_24515();
		entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
		entity.getBrain().forget(MemoryModuleType.ANGRY_AT);
		entity.getBrain().method_24525(MemoryModuleType.CELEBRATE_LOCATION, blockPos, time, (long)this.field_22327);
	}

	private LivingEntity method_24616(LivingEntity livingEntity) {
		return (LivingEntity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
