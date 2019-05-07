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

    public FindEntityTask(EntityType<? extends T> entityType, int i, Predicate<E> predicate, Predicate<T> predicate2, MemoryModuleType<T> memoryModuleType, float f, int j) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, memoryModuleType, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
        this.entityType = entityType;
        this.speed = f;
        this.maxSquaredDistance = i * i;
        this.completionRange = j;
        this.predicate = predicate2;
        this.shouldRunPredicate = predicate;
        this.targetModule = memoryModuleType;
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> entityType, int i, MemoryModuleType<T> memoryModuleType, float f, int j) {
        return new FindEntityTask<LivingEntity, LivingEntity>(entityType, i, livingEntity -> true, livingEntity -> true, memoryModuleType, f, j);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E livingEntity2) {
        return this.shouldRunPredicate.test(livingEntity2) && ((LivingEntity)livingEntity2).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(livingEntity -> this.entityType.equals(livingEntity.getType()) && this.predicate.test((LivingEntity)livingEntity));
    }

    @Override
    protected void run(ServerWorld serverWorld, E livingEntity, long l) {
        Brain<?> brain = ((LivingEntity)livingEntity).getBrain();
        brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> list.stream().filter(livingEntity -> this.entityType.equals(livingEntity.getType())).map(livingEntity -> livingEntity).filter(livingEntity2 -> livingEntity2.squaredDistanceTo((Entity)livingEntity) <= (double)this.maxSquaredDistance).filter(this.predicate).findFirst().ifPresent(livingEntity -> {
            brain.putMemory(this.targetModule, livingEntity);
            brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper((LivingEntity)livingEntity));
            brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper((LivingEntity)livingEntity), this.speed, this.completionRange));
        }));
    }
}

