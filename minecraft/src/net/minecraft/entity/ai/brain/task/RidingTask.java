package net.minecraft.entity.ai.brain.task;

import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class RidingTask {
	public static <E extends LivingEntity> Task<E> create(int range, BiPredicate<E, Entity> alternativeRideCondition) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryOptional(MemoryModuleType.RIDE_TARGET)).apply(context, rideTarget -> (world, entity, time) -> {
						Entity entity2 = entity.getVehicle();
						Entity entity3 = (Entity)context.getOptionalValue(rideTarget).orElse(null);
						if (entity2 == null && entity3 == null) {
							return false;
						} else {
							Entity entity4 = entity2 == null ? entity3 : entity2;
							if (canRideTarget(entity, entity4, range) && !alternativeRideCondition.test(entity, entity4)) {
								return false;
							} else {
								entity.stopRiding();
								rideTarget.forget();
								return true;
							}
						}
					})
		);
	}

	private static boolean canRideTarget(LivingEntity entity, Entity vehicle, int range) {
		return vehicle.isAlive() && vehicle.isInRange(entity, (double)range) && vehicle.getWorld() == entity.getWorld();
	}
}
