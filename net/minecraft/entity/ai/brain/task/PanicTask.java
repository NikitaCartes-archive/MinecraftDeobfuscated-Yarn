/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class PanicTask
extends Task<LivingEntity> {
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }

    @Override
    protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        if (PanicTask.wasHurt(livingEntity) || PanicTask.isHostileNearby(livingEntity)) {
            Brain<Object> brain = livingEntity.getBrain();
            if (!brain.hasActivity(Activity.PANIC)) {
                brain.forget(MemoryModuleType.PATH);
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.LOOK_TARGET);
                brain.forget(MemoryModuleType.BREED_TARGET);
                brain.forget(MemoryModuleType.INTERACTION_TARGET);
            }
            brain.resetPossibleActivities(Activity.PANIC);
        }
    }

    public static boolean isHostileNearby(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }
}

