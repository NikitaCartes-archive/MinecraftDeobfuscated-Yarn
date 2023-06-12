package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

@Deprecated
public class LookAtMobWithIntervalTask {
	public static Task<LivingEntity> follow(float maxDistance, UniformIntProvider interval) {
		return follow(maxDistance, interval, entity -> true);
	}

	public static Task<LivingEntity> follow(EntityType<?> type, float maxDistance, UniformIntProvider interval) {
		return follow(maxDistance, interval, entity -> type.equals(entity.getType()));
	}

	private static Task<LivingEntity> follow(float maxDistance, UniformIntProvider interval, Predicate<LivingEntity> predicate) {
		float f = maxDistance * maxDistance;
		LookAtMobWithIntervalTask.Interval interval2 = new LookAtMobWithIntervalTask.Interval(interval);
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS))
					.apply(
						context,
						(lookTarget, visibleMobs) -> (world, entity, time) -> {
								Optional<LivingEntity> optional = context.<LivingTargetCache>getValue(visibleMobs)
									.findFirst(predicate.and(other -> other.squaredDistanceTo(entity) <= (double)f));
								if (optional.isEmpty()) {
									return false;
								} else if (!interval2.shouldRun(world.random)) {
									return false;
								} else {
									lookTarget.remember(new EntityLookTarget((Entity)optional.get(), true));
									return true;
								}
							}
					)
		);
	}

	public static final class Interval {
		private final UniformIntProvider interval;
		private int remainingTicks;

		public Interval(UniformIntProvider interval) {
			if (interval.getMin() <= 1) {
				throw new IllegalArgumentException();
			} else {
				this.interval = interval;
			}
		}

		public boolean shouldRun(Random random) {
			if (this.remainingTicks == 0) {
				this.remainingTicks = this.interval.get(random) - 1;
				return false;
			} else {
				return --this.remainingTicks == 0;
			}
		}
	}
}
