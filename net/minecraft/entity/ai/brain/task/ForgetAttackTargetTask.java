/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private final Predicate<LivingEntity> alternativeCondition;

    public ForgetAttackTargetTask(Predicate<LivingEntity> alternativeCondition) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.alternativeCondition = alternativeCondition;
    }

    public ForgetAttackTargetTask() {
        this((LivingEntity livingEntity) -> false);
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mobEntity);
        if (!livingEntity.canTakeDamage()) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (ForgetAttackTargetTask.cannotReachTarget(mobEntity)) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (this.isAttackTargetDead(mobEntity)) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (this.isAttackTargetInAnotherWorld(mobEntity)) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (!EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(this.getAttackTarget(mobEntity))) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (this.alternativeCondition.test(this.getAttackTarget(mobEntity))) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
    }

    private boolean isAttackTargetInAnotherWorld(E entity) {
        return this.getAttackTarget(entity).world != ((MobEntity)entity).world;
    }

    private LivingEntity getAttackTarget(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean cannotReachTarget(E entity) {
        Optional<Long> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && entity.world.getTime() - optional.get() > 200L;
    }

    private boolean isAttackTargetDead(E entity) {
        Optional<LivingEntity> optional = ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    private void forgetAttackTarget(E entity) {
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.ATTACK_TARGET);
    }
}

