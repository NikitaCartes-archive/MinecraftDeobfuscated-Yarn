package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class WalkTowardClosestAdultTask {
	public static SingleTickTask<PassiveEntity> create(UniformIntProvider executionRange, float speed) {
		return create(executionRange, entity -> speed);
	}

	public static SingleTickTask<PassiveEntity> create(UniformIntProvider executionRange, Function<LivingEntity, Float> speed) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ADULT),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)
					)
					.apply(context, (nearestVisibleAdult, lookTarget, walkTarget) -> (world, entity, time) -> {
							if (!entity.isBaby()) {
								return false;
							} else {
								PassiveEntity passiveEntity = context.getValue(nearestVisibleAdult);
								if (entity.isInRange(passiveEntity, (double)(executionRange.getMax() + 1)) && !entity.isInRange(passiveEntity, (double)executionRange.getMin())) {
									WalkTarget walkTargetx = new WalkTarget(new EntityLookTarget(passiveEntity, false), (Float)speed.apply(entity), executionRange.getMin() - 1);
									lookTarget.remember(new EntityLookTarget(passiveEntity, true));
									walkTarget.remember(walkTargetx);
									return true;
								} else {
									return false;
								}
							}
						})
		);
	}
}
