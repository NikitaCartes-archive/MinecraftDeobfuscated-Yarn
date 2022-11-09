/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class ForgetTask {
    public static <E extends LivingEntity> Task<E> create(Predicate<E> condition, MemoryModuleType<?> memory) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(memory)).apply(context, queryResult -> (world, entity, time) -> {
            if (condition.test(entity)) {
                queryResult.forget();
                return true;
            }
            return false;
        }));
    }
}

