/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private static final int REMEMBER_TIME = 200;
    private final Predicate<LivingEntity> alternativeCondition;
    private final BiConsumer<E, LivingEntity> forgetCallback;
    private final boolean shouldForgetIfTargetUnreachable;

    public ForgetAttackTargetTask(Predicate<LivingEntity> alternativePredicate, BiConsumer<E, LivingEntity> forgetCallback, boolean shouldForgetIfTargetUnreachable) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.alternativeCondition = alternativePredicate;
        this.forgetCallback = forgetCallback;
        this.shouldForgetIfTargetUnreachable = shouldForgetIfTargetUnreachable;
    }

    public ForgetAttackTargetTask(Predicate<LivingEntity> alternativePredicate, BiConsumer<E, LivingEntity> forgetCallback) {
        this(alternativePredicate, forgetCallback, true);
    }

    public ForgetAttackTargetTask(Predicate<LivingEntity> alternativePredicate) {
        this(alternativePredicate, (mobEntity, livingEntity) -> {});
    }

    public ForgetAttackTargetTask(BiConsumer<E, LivingEntity> forgetCallback) {
        this((LivingEntity target) -> false, forgetCallback);
    }

    public ForgetAttackTargetTask() {
        this((LivingEntity target) -> false, (mobEntity, livingEntity) -> {});
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mobEntity);
        if (!((LivingEntity)mobEntity).canTarget(livingEntity)) {
            this.forgetAttackTarget(mobEntity);
            return;
        }
        if (this.shouldForgetIfTargetUnreachable && ForgetAttackTargetTask.cannotReachTarget(mobEntity)) {
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

    protected void forgetAttackTarget(E entity) {
        this.forgetCallback.accept(entity, this.getAttackTarget(entity));
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.ATTACK_TARGET);
    }
}

