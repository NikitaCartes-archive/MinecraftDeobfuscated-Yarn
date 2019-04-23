/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class StopPanicingTask
extends Task<LivingEntity> {
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }

    @Override
    protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        boolean bl;
        boolean bl2 = bl = PanicTask.wasHurt(livingEntity) || PanicTask.isHostileNearby(livingEntity) || StopPanicingTask.wasHurtByNearbyEntity(livingEntity);
        if (!bl) {
            livingEntity.getBrain().forget(MemoryModuleType.HURT_BY);
            livingEntity.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
            livingEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
        }
    }

    private static boolean wasHurtByNearbyEntity(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 36.0).isPresent();
    }
}

