/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class AttackTask {
    public static SingleTickTask<MobEntity> create(int distance, float forwardMovement) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply(context, (walkTarget, lookTarget, attackTarget, visibleMobs) -> (world, entity, time) -> {
            LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
            if (livingEntity.isInRange(entity, distance) && ((LivingTargetCache)context.getValue(visibleMobs)).contains(livingEntity)) {
                lookTarget.remember(new EntityLookTarget(livingEntity, true));
                entity.getMoveControl().strafeTo(-forwardMovement, 0.0f);
                entity.setYaw(MathHelper.clampAngle(entity.getYaw(), entity.headYaw, 0.0f));
                return true;
            }
            return false;
        }));
    }
}

