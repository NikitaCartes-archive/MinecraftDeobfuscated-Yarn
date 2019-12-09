/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class FindEntityTask<E extends LivingEntity, T extends LivingEntity>
extends Task<E> {
    private final int completionRange;
    private final float speed;
    private final EntityType<? extends T> entityType;
    private final int maxSquaredDistance;
    private final Predicate<T> predicate;
    private final Predicate<E> shouldRunPredicate;
    private final MemoryModuleType<T> targetModule;

    public FindEntityTask(EntityType<? extends T> entityType, int maxDistance, Predicate<E> shouldRunPredicate, Predicate<T> predicate, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, targetModule, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
        this.entityType = entityType;
        this.speed = speed;
        this.maxSquaredDistance = maxDistance * maxDistance;
        this.completionRange = completionRange;
        this.predicate = predicate;
        this.shouldRunPredicate = shouldRunPredicate;
        this.targetModule = targetModule;
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> entityType, int maxDistance, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        return new FindEntityTask<LivingEntity, LivingEntity>(entityType, maxDistance, livingEntity -> true, livingEntity -> true, targetModule, speed, completionRange);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.shouldRunPredicate.test(entity) && ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(livingEntity -> this.entityType.equals(livingEntity.getType()) && this.predicate.test((LivingEntity)livingEntity));
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        Brain<?> brain = ((LivingEntity)entity).getBrain();
        brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(livingEntity -> this.entityType.equals(livingEntity.getType())).map(livingEntity -> livingEntity).filter(livingEntity2 -> livingEntity2.squaredDistanceTo((Entity)entity) <= (double)this.maxSquaredDistance).filter(this.predicate).findFirst().ifPresent(livingEntity -> {
            brain.putMemory(this.targetModule, livingEntity);
            brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper((Entity)livingEntity));
            brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper((Entity)livingEntity), this.speed, this.completionRange));
        }));
    }
}

