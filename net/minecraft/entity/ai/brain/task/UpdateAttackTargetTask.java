/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class UpdateAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private final Predicate<E> startCondition;
    private final Function<E, Optional<? extends LivingEntity>> targetGetter;

    public UpdateAttackTargetTask(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.startCondition = startCondition;
        this.targetGetter = targetGetter;
    }

    public UpdateAttackTargetTask(Function<E, Optional<? extends LivingEntity>> targetGetter) {
        this((E mobEntity) -> true, targetGetter);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
        if (!this.startCondition.test(mobEntity)) {
            return false;
        }
        Optional<? extends LivingEntity> optional = this.targetGetter.apply(mobEntity);
        if (optional.isPresent()) {
            return ((LivingEntity)mobEntity).canTarget(optional.get());
        }
        return false;
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        this.targetGetter.apply(mobEntity).ifPresent(livingEntity -> this.updateAttackTarget(mobEntity, (LivingEntity)livingEntity));
    }

    private void updateAttackTarget(E entity, LivingEntity target) {
        ((LivingEntity)entity).getBrain().remember(MemoryModuleType.ATTACK_TARGET, target);
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}

