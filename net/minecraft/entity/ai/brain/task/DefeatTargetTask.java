/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.world.GameRules;

public class DefeatTargetTask {
    public static Task<LivingEntity> create(int celebrationDuration, BiPredicate<LivingEntity, LivingEntity> predicate) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.ANGRY_AT), context.queryMemoryAbsent(MemoryModuleType.CELEBRATE_LOCATION), context.queryMemoryOptional(MemoryModuleType.DANCING)).apply(context, (attackTarget, angryAt, celebrateLocation, dancing) -> (world, entity, time) -> {
            LivingEntity livingEntity = (LivingEntity)context.getValue(attackTarget);
            if (!livingEntity.isDead()) {
                return false;
            }
            if (predicate.test(entity, livingEntity)) {
                dancing.remember(true, celebrationDuration);
            }
            celebrateLocation.remember(livingEntity.getBlockPos(), celebrationDuration);
            if (livingEntity.getType() != EntityType.PLAYER || world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
                attackTarget.forget();
                angryAt.forget();
            }
            return true;
        }));
    }
}

