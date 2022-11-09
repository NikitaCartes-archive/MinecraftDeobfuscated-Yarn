/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class GoTowardsLookTargetTask {
    public static SingleTickTask<LivingEntity> create(float speed, int completionRange) {
        return GoTowardsLookTargetTask.create(entity -> true, entity -> Float.valueOf(speed), completionRange);
    }

    public static SingleTickTask<LivingEntity> create(Predicate<LivingEntity> predicate, Function<LivingEntity, Float> speed, int completionRange) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.LOOK_TARGET)).apply(context, (walkTarget, lookTarget) -> (world, entity, time) -> {
            if (!predicate.test(entity)) {
                return false;
            }
            walkTarget.remember(new WalkTarget((LookTarget)context.getValue(lookTarget), ((Float)speed.apply(entity)).floatValue(), completionRange));
            return true;
        }));
    }
}

