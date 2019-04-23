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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask
extends Task<MobEntity> {
    public LookAroundTask(int i, int j) {
        super(i, j);
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).filter(lookTarget -> lookTarget.isSeenBy(mobEntity)).isPresent();
    }

    protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).ifPresent(lookTarget -> mobEntity.getLookControl().method_19615(lookTarget.getPos()));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.method_18967(serverWorld, (MobEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_18968(serverWorld, (MobEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_18969(serverWorld, (MobEntity)livingEntity, l);
    }
}

