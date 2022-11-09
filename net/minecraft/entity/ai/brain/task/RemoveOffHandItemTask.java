/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Items;

public class RemoveOffHandItemTask {
    public static Task<PiglinEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.ADMIRING_ITEM)).apply(context, admiringItem -> (world, entity, time) -> {
            if (entity.getOffHandStack().isEmpty() || entity.getOffHandStack().isOf(Items.SHIELD)) {
                return false;
            }
            PiglinBrain.consumeOffHandItem(entity, true);
            return true;
        }));
    }
}

