/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class FindRoarTargetTask<E extends WardenEntity>
extends Task<E> {
    private final Function<E, Optional<? extends LivingEntity>> targetFinder;

    public FindRoarTargetTask(Function<E, Optional<? extends LivingEntity>> targetFinder) {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.targetFinder = targetFinder;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
        return this.targetFinder.apply(wardenEntity).filter(arg_0 -> wardenEntity.isValidTarget(arg_0)).isPresent();
    }

    @Override
    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        this.targetFinder.apply(wardenEntity).ifPresent(target -> {
            wardenEntity.getBrain().remember(MemoryModuleType.ROAR_TARGET, target);
            wardenEntity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        });
    }
}

