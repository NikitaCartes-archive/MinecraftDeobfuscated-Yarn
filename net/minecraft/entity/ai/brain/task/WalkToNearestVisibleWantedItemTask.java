/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class WalkToNearestVisibleWantedItemTask {
    public static Task<LivingEntity> create(float speed, boolean requiresWalkTarget, int radius) {
        return WalkToNearestVisibleWantedItemTask.create(entity -> true, speed, requiresWalkTarget, radius);
    }

    public static <E extends LivingEntity> Task<E> create(Predicate<E> startCondition, float speed, boolean requiresWalkTarget, int radius) {
        return TaskTriggerer.task(context -> {
            TaskTriggerer taskTriggerer = requiresWalkTarget ? context.queryMemoryOptional(MemoryModuleType.WALK_TARGET) : context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET);
            return context.group(context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), taskTriggerer, context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), context.queryMemoryOptional(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)).apply(context, (lookTarget, walkTarget, nearestVisibleWantedItem, itemPickupCooldownTicks) -> (world, entity, time) -> {
                ItemEntity itemEntity = (ItemEntity)context.getValue(nearestVisibleWantedItem);
                if (context.getOptionalValue(itemPickupCooldownTicks).isEmpty() && startCondition.test(entity) && itemEntity.isInRange(entity, radius) && entity.world.getWorldBorder().contains(itemEntity.getBlockPos())) {
                    WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(itemEntity, false), speed, 0);
                    lookTarget.remember(new EntityLookTarget(itemEntity, true));
                    walkTarget.remember(walkTarget);
                    return true;
                }
                return false;
            });
        });
    }
}

