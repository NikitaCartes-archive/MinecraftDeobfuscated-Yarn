/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayDeadTask
extends Task<AxolotlEntity> {
    public PlayDeadTask() {
        super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleState.VALUE_PRESENT), 200);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, AxolotlEntity axolotlEntity) {
        return axolotlEntity.isInsideWaterOrBubbleColumn();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, AxolotlEntity axolotlEntity, long l) {
        return axolotlEntity.isInsideWaterOrBubbleColumn() && axolotlEntity.getBrain().hasMemoryModule(MemoryModuleType.PLAY_DEAD_TICKS);
    }

    @Override
    protected void run(ServerWorld serverWorld, AxolotlEntity axolotlEntity, long l) {
        Brain<AxolotlEntity> brain = axolotlEntity.getBrain();
        brain.forget(MemoryModuleType.WALK_TARGET);
        brain.forget(MemoryModuleType.LOOK_TARGET);
        axolotlEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (AxolotlEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (AxolotlEntity)entity, time);
    }
}

