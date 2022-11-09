/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class WalkTowardsLookTargetTask {
    public static Task<LivingEntity> create(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, int completionRange, int searchRange, float speed) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, (lookTarget, walkTarget) -> (world, entity, time) -> {
            Optional optional = (Optional)lookTargetFunction.apply(entity);
            if (optional.isEmpty()) {
                return false;
            }
            LookTarget lookTarget = (LookTarget)optional.get();
            if (entity.getPos().isInRange(lookTarget.getPos(), searchRange)) {
                return false;
            }
            LookTarget lookTarget2 = (LookTarget)optional.get();
            lookTarget.remember(lookTarget2);
            walkTarget.remember(new WalkTarget(lookTarget2, speed, completionRange));
            return true;
        }));
    }
}

