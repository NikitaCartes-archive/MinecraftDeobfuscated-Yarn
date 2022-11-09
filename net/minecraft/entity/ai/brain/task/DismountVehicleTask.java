/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

public class DismountVehicleTask
extends MultiTickTask<LivingEntity> {
    public DismountVehicleTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return entity.hasVehicle();
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        entity.detach();
    }
}

