/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DefeatTargetTask
extends Task<LivingEntity> {
    private final int duration;

    public DefeatTargetTask(int duration) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleState.VALUE_ABSENT));
        this.duration = duration;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return this.getAttackTarget(entity).getHealth() <= 0.0f;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        BlockPos blockPos = this.getAttackTarget(entity).getSenseCenterPos();
        entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        entity.getBrain().forget(MemoryModuleType.ANGRY_AT);
        entity.getBrain().remember(MemoryModuleType.CELEBRATE_LOCATION, blockPos, time, this.duration);
    }

    private LivingEntity getAttackTarget(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

