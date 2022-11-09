/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.util.math.GlobalPos;

public class MeetVillagerTask {
    private static final float WALK_SPEED = 0.3f;

    public static SingleTickTask<LivingEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.MEETING_POINT), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS), context.queryMemoryAbsent(MemoryModuleType.INTERACTION_TARGET)).apply(context, (walkTarget, lookTarget, meetingPoint, visibleMobs, interactionTarget) -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.getValue(meetingPoint);
            LivingTargetCache livingTargetCache = (LivingTargetCache)context.getValue(visibleMobs);
            if (world.getRandom().nextInt(100) == 0 && world.getRegistryKey() == globalPos.getDimension() && globalPos.getPos().isWithinDistance(entity.getPos(), 4.0) && livingTargetCache.anyMatch(target -> EntityType.VILLAGER.equals(target.getType()))) {
                livingTargetCache.findFirst(target -> EntityType.VILLAGER.equals(target.getType()) && target.squaredDistanceTo(entity) <= 32.0).ifPresent(target -> {
                    interactionTarget.remember(target);
                    lookTarget.remember(new EntityLookTarget((Entity)target, true));
                    walkTarget.remember(new WalkTarget(new EntityLookTarget((Entity)target, false), 0.3f, 1));
                });
                return true;
            }
            return false;
        }));
    }
}

