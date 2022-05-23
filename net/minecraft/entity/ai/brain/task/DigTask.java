/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class DigTask<E extends WardenEntity>
extends Task<E> {
    public DigTask(int duration) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), duration);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return ((Entity)wardenEntity).getRemovalReason() == null;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
        return ((Entity)wardenEntity).isOnGround() || ((Entity)wardenEntity).isTouchingWater() || ((Entity)wardenEntity).isInLava();
    }

    @Override
    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).isOnGround()) {
            ((Entity)wardenEntity).setPose(EntityPose.DIGGING);
            ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_DIG, 5.0f, 1.0f);
        } else {
            ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0f, 1.0f);
            this.finishRunning(serverWorld, wardenEntity, l);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).getRemovalReason() == null) {
            ((Entity)wardenEntity).remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (E)((WardenEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (E)((WardenEntity)entity), time);
    }
}

