package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.K1;
import java.util.function.Predicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.WalkTarget;

public class WalkToNearestVisibleWantedItemTask {
	public static Task<LivingEntity> create(float speed, boolean requiresWalkTarget, int radius) {
		return create(entity -> true, speed, requiresWalkTarget, radius);
	}

	public static <E extends LivingEntity> Task<E> create(Predicate<E> startCondition, float speed, boolean requiresWalkTarget, int radius) {
		return TaskTriggerer.task(
			context -> {
				TaskTriggerer<E, ? extends MemoryQueryResult<? extends K1, WalkTarget>> taskTriggerer = requiresWalkTarget
					? context.queryMemoryOptional(MemoryModuleType.WALK_TARGET)
					: context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET);
				return context.group(
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						taskTriggerer,
						context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM),
						context.queryMemoryOptional(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
					)
					.apply(
						context,
						(lookTarget, walkTarget, nearestVisibleWantedItem, itemPickupCooldownTicks) -> (world, entity, time) -> {
								ItemEntity itemEntity = context.getValue(nearestVisibleWantedItem);
								if (context.getOptionalValue(itemPickupCooldownTicks).isEmpty()
									&& startCondition.test(entity)
									&& itemEntity.isInRange(entity, (double)radius)
									&& entity.getWorld().getWorldBorder().contains(itemEntity.getBlockPos())) {
									WalkTarget walkTargetx = new WalkTarget(new EntityLookTarget(itemEntity, false), speed, 0);
									lookTarget.remember(new EntityLookTarget(itemEntity, true));
									walkTarget.remember(walkTargetx);
									return true;
								} else {
									return false;
								}
							}
					);
			}
		);
	}
}
