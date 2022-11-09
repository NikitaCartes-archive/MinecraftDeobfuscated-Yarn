package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;

public class GoTowardsLookTargetTask {
	public static SingleTickTask<LivingEntity> create(float speed, int completionRange) {
		return create(entity -> true, entity -> speed, completionRange);
	}

	public static SingleTickTask<LivingEntity> create(Predicate<LivingEntity> predicate, Function<LivingEntity, Float> speed, int completionRange) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.LOOK_TARGET))
					.apply(context, (walkTarget, lookTarget) -> (world, entity, time) -> {
							if (!predicate.test(entity)) {
								return false;
							} else {
								walkTarget.remember(new WalkTarget(context.getValue(lookTarget), (Float)speed.apply(entity), completionRange));
								return true;
							}
						})
		);
	}
}
