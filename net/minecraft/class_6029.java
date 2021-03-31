/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class class_6029
extends Task<MobEntity> {
    public static final int field_30132 = 100;
    private final UniformIntProvider field_30133;

    public class_6029(UniformIntProvider uniformIntProvider) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_PRESENT), 100);
        this.field_30133 = uniformIntProvider;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return !mobEntity.isOnGround();
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.method_35054(true);
        mobEntity.setPose(EntityPose.LONG_JUMPING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (mobEntity.isOnGround()) {
            mobEntity.setVelocity(mobEntity.getVelocity().multiply(0.1f));
        }
        mobEntity.method_35054(false);
        mobEntity.setPose(EntityPose.STANDING);
        mobEntity.getBrain().forget(MemoryModuleType.LONG_JUMP_MID_JUMP);
        mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.field_30133.get(serverWorld.random));
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

