package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class AdmireItemTimeLimitTask {
	public static Task<LivingEntity> create(int cooldown, int timeLimit) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.ADMIRING_ITEM),
						context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM),
						context.queryMemoryOptional(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM),
						context.queryMemoryOptional(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)
					)
					.apply(context, (admiringItem, nearestVisibleWantedItem, timeTryingToReachAdmireItem, disableWalkToAdmireItem) -> (world, entity, time) -> {
							if (!entity.getOffHandStack().isEmpty()) {
								return false;
							} else {
								Optional<Integer> optional = context.getOptionalValue(timeTryingToReachAdmireItem);
								if (optional.isEmpty()) {
									timeTryingToReachAdmireItem.remember(0);
								} else {
									int k = (Integer)optional.get();
									if (k > cooldown) {
										admiringItem.forget();
										timeTryingToReachAdmireItem.forget();
										disableWalkToAdmireItem.remember(true, (long)timeLimit);
									} else {
										timeTryingToReachAdmireItem.remember(k + 1);
									}
								}

								return true;
							}
						})
		);
	}
}
