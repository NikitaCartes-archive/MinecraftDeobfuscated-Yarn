/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PiglinEntity;

public class WantNewItemTask<E extends PiglinEntity> {
    public static Task<LivingEntity> create(int range) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.ADMIRING_ITEM), context.queryMemoryOptional(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(context, (admiringItem, nearestVisibleWantedItem) -> (world, entity, time) -> {
            if (!entity.getOffHandStack().isEmpty()) {
                return false;
            }
            Optional optional = context.getOptionalValue(nearestVisibleWantedItem);
            if (optional.isPresent() && ((ItemEntity)optional.get()).isInRange(entity, range)) {
                return false;
            }
            admiringItem.forget();
            return true;
        }));
    }
}

