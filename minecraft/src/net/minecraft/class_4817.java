package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4817<E extends LivingEntity> extends Task<E> {
	public class_4817() {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.RIDE_TARGET,
				MemoryModuleState.VALUE_PRESENT
			)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return !entity.hasVehicle();
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		if (this.method_24591(entity)) {
			entity.startRiding(this.method_24592(entity));
		} else {
			LookTargetUtil.method_24557(entity, this.method_24592(entity), 1);
		}
	}

	private boolean method_24591(E livingEntity) {
		return this.method_24592(livingEntity).method_24516(livingEntity, 1.0);
	}

	private Entity method_24592(E livingEntity) {
		return (Entity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).get();
	}
}
