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

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity)) {
            Brain<VillagerEntity> brain = villagerEntity.getBrain();
            if (!brain.hasActivity(Activity.PANIC)) {
                brain.forget(MemoryModuleType.PATH);
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.LOOK_TARGET);
                brain.forget(MemoryModuleType.BREED_TARGET);
                brain.forget(MemoryModuleType.INTERACTION_TARGET);
            }
            brain.method_24526(Activity.PANIC);
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l % 100L == 0L) {
            villagerEntity.summonGolem(l, 3);
        }
    }

    public static boolean isHostileNearby(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean wasHurt(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

