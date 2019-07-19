/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask
extends Task<MobEntity> {
    private final float minWaterHeight;
    private final float chance;

    public StayAboveWaterTask(float f, float g) {
        super(ImmutableMap.of());
        this.minWaterHeight = f;
        this.chance = g;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        return mobEntity.isTouchingWater() && mobEntity.getWaterHeight() > (double)this.minWaterHeight || mobEntity.isInLava();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return this.shouldRun(serverWorld, mobEntity);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (mobEntity.getRandom().nextFloat() < this.chance) {
            mobEntity.getJumpControl().setActive();
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.shouldKeepRunning(serverWorld, (MobEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.keepRunning(serverWorld, (MobEntity)livingEntity, l);
    }
}

