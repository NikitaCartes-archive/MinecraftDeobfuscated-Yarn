package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;

public class AdmireItemTask {
	public static Task<LivingEntity> create(int duration) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM),
						context.queryMemoryAbsent(MemoryModuleType.ADMIRING_ITEM),
						context.queryMemoryAbsent(MemoryModuleType.ADMIRING_DISABLED),
						context.queryMemoryAbsent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)
					)
					.apply(context, (nearestVisibleWantedItem, admiringItem, admiringDisabled, disableWalkToAdmireItem) -> (world, entity, time) -> {
							ItemEntity itemEntity = context.getValue(nearestVisibleWantedItem);
							if (!PiglinBrain.isGoldenItem(itemEntity.getStack())) {
								return false;
							} else {
								admiringItem.remember(true, (long)duration);
								return true;
							}
						})
		);
	}
}
