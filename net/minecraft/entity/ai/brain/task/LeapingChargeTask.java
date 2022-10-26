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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class LeapingChargeTask
extends Task<MobEntity> {
    public static final int RUN_TIME = 100;
    private final UniformIntProvider cooldownRange;
    private final SoundEvent sound;

    public LeapingChargeTask(UniformIntProvider cooldownRange, SoundEvent sound) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_PRESENT), 100);
        this.cooldownRange = cooldownRange;
        this.sound = sound;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return !mobEntity.isOnGround();
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.setNoDrag(true);
        mobEntity.setPose(EntityPose.LONG_JUMPING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (mobEntity.isOnGround()) {
            mobEntity.setVelocity(mobEntity.getVelocity().multiply(0.1f, 1.0, 0.1f));
            serverWorld.playSoundFromEntity(null, mobEntity, this.sound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        }
        mobEntity.setNoDrag(false);
        mobEntity.setPose(EntityPose.STANDING);
        mobEntity.getBrain().forget(MemoryModuleType.LONG_JUMP_MID_JUMP);
        mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (MobEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (MobEntity)entity, time);
    }
}

