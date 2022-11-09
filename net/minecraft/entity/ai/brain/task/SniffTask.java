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
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class SniffTask<E extends WardenEntity>
extends MultiTickTask<E> {
    private static final double HORIZONTAL_RADIUS = 6.0;
    private static final double VERTICAL_RADIUS = 20.0;

    public SniffTask(int runTime) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleState.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleState.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleState.REGISTERED), runTime);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_SNIFF, 5.0f, 1.0f);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).isInPose(EntityPose.SNIFFING)) {
            ((Entity)wardenEntity).setPose(EntityPose.STANDING);
        }
        ((WardenEntity)wardenEntity).getBrain().forget(MemoryModuleType.IS_SNIFFING);
        ((WardenEntity)wardenEntity).getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(arg_0 -> wardenEntity.isValidTarget(arg_0)).ifPresent(target -> {
            if (wardenEntity.isInRange((Entity)target, 6.0, 20.0)) {
                wardenEntity.increaseAngerAt((Entity)target);
            }
            if (!wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)) {
                WardenBrain.lookAtDisturbance(wardenEntity, target.getBlockPos());
            }
        });
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (E)((WardenEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (E)((WardenEntity)entity), time);
    }
}

