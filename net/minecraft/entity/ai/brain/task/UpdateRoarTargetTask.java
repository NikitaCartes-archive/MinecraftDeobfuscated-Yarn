/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class UpdateRoarTargetTask
extends UpdateAttackTargetTask<WardenEntity> {
    public UpdateRoarTargetTask(Predicate<WardenEntity> predicate, Function<WardenEntity, Optional<? extends LivingEntity>> function, int i) {
        super(predicate, function, i);
    }

    @Override
    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        LookTargetUtil.lookAt(wardenEntity, wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET).get());
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        this.runAndForget(serverWorld, wardenEntity, l);
    }

    private void runAndForget(ServerWorld world, WardenEntity warden, long time) {
        super.run(world, warden, time);
        warden.getBrain().forget(MemoryModuleType.ROAR_TARGET);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Optional<LivingEntity> optional = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET);
        return optional.filter(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR).isPresent();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (WardenEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (WardenEntity)entity, time);
    }
}

