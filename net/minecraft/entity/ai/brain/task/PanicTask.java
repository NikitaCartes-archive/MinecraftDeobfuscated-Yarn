/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class PanicTask
extends Task<VillagerEntity> {
    public PanicTask() {
        super(ImmutableMap.of());
    }

    protected boolean method_20646(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity);
    }

    protected void method_20647(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity)) {
            Brain<VillagerEntity> brain = villagerEntity.getBrain();
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

    protected void method_20648(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l % 100L == 0L) {
            villagerEntity.method_20688(l, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.method_20646(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_20648(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_20647(serverWorld, (VillagerEntity)livingEntity, l);
    }
}

