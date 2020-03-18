/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class StartRidingTask<E extends LivingEntity>
extends Task<E> {
    private final float field_23132;

    public StartRidingTask(float f) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryModuleState.VALUE_PRESENT));
        this.field_23132 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !((Entity)entity).hasVehicle();
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        if (this.isRideTargetClose(entity)) {
            ((Entity)entity).startRiding(this.getRideTarget(entity));
        } else {
            LookTargetUtil.walkTowards(entity, this.getRideTarget(entity), this.field_23132, 1);
        }
    }

    private boolean isRideTargetClose(E entity) {
        return this.getRideTarget(entity).isInRange((Entity)entity, 1.0);
    }

    private Entity getRideTarget(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}

