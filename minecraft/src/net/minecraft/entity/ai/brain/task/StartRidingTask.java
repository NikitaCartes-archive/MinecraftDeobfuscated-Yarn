package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class StartRidingTask {
	private static final int COMPLETION_RANGE = 1;

	public static Task<LivingEntity> create(float speed) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
						context.queryMemoryValue(MemoryModuleType.RIDE_TARGET)
					)
					.apply(context, (lookTarget, walkTarget, rideTarget) -> (world, entity, time) -> {
							if (entity.hasVehicle()) {
								return false;
							} else {
								Entity entity2 = context.getValue(rideTarget);
								if (entity2.isInRange(entity, 1.0)) {
									entity.startRiding(entity2);
								} else {
									lookTarget.remember(new EntityLookTarget(entity2, true));
									walkTarget.remember(new WalkTarget(new EntityLookTarget(entity2, false), speed, 1));
								}

								return true;
							}
						})
		);
	}
}
