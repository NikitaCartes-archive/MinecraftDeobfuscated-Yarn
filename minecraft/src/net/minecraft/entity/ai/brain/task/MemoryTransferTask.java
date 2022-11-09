package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class MemoryTransferTask {
	public static <E extends LivingEntity, T> Task<E> create(
		Predicate<E> runPredicate, MemoryModuleType<? extends T> sourceType, MemoryModuleType<T> targetType, UniformIntProvider expiry
	) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(sourceType), context.queryMemoryAbsent(targetType))
					.apply(context, (source, target) -> (world, entity, time) -> {
							if (!runPredicate.test(entity)) {
								return false;
							} else {
								target.remember(context.getValue(source), (long)expiry.get(world.random));
								return true;
							}
						})
		);
	}
}
