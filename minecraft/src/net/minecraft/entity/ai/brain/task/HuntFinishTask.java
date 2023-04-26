package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;

public class HuntFinishTask {
	public static Task<LivingEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.HUNTED_RECENTLY))
					.apply(context, (attackTarget, huntedRecently) -> (world, entity, time) -> {
							LivingEntity livingEntity = context.getValue(attackTarget);
							if (livingEntity.getType() == EntityType.HOGLIN && livingEntity.isDead()) {
								huntedRecently.remember(true, (long)PiglinBrain.HUNT_MEMORY_DURATION.get(entity.getWorld().random));
							}

							return true;
						})
		);
	}
}
