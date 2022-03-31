/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;

public class CroakTask
extends Task<FrogEntity> {
    private static final int field_37424 = 60;
    private static final int field_37425 = 100;
    private int runningTicks;

    public CroakTask() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), 100);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return frogEntity.getPose() == EntityPose.STANDING;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return this.runningTicks < 60;
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        if (frogEntity.isInsideWaterOrBubbleColumn() || frogEntity.isInLava()) {
            return;
        }
        frogEntity.setPose(EntityPose.CROAKING);
        this.runningTicks = 0;
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.setPose(EntityPose.STANDING);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        ++this.runningTicks;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (FrogEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (FrogEntity)entity, time);
    }
}

