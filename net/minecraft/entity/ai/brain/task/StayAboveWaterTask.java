/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask
extends MultiTickTask<MobEntity> {
    private final float chance;

    public StayAboveWaterTask(float chance) {
        super(ImmutableMap.of());
        this.chance = chance;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        return mobEntity.isTouchingWater() && mobEntity.getFluidHeight(FluidTags.WATER) > mobEntity.getSwimHeight() || mobEntity.isInLava();
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
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (MobEntity)entity, time);
    }
}

