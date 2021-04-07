package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class StartRidingTask<E extends LivingEntity> extends Task<E> {
	private static final int COMPLETION_RANGE = 1;
	private final float speed;

	public StartRidingTask(float speed) {
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
		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return !entity.hasVehicle();
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		if (this.isRideTargetClose(entity)) {
			entity.startRiding(this.getRideTarget(entity));
		} else {
			LookTargetUtil.walkTowards(entity, this.getRideTarget(entity), this.speed, 1);
		}
	}

	private boolean isRideTargetClose(E entity) {
		return this.getRideTarget(entity).isInRange(entity, 1.0);
	}

	private Entity getRideTarget(E entity) {
		return (Entity)entity.getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).get();
	}
}
