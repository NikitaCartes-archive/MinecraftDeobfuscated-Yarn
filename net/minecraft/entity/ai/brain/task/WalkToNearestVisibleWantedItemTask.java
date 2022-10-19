/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class WalkToNearestVisibleWantedItemTask<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> startCondition;
    private final int radius;
    private final float speed;

    public WalkToNearestVisibleWantedItemTask(float speed, boolean requiresWalkTarget, int radius) {
        this(entity -> true, speed, requiresWalkTarget, radius);
    }

    public WalkToNearestVisibleWantedItemTask(Predicate<E> startCondition, float speed, boolean requiresWalkTarget, int radius) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, requiresWalkTarget ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.VALUE_PRESENT));
        this.startCondition = startCondition;
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        ItemEntity itemEntity = this.getNearestVisibleWantedItem(entity);
        return !this.isInPickupCooldown(entity) && this.startCondition.test(entity) && itemEntity.isInRange((Entity)entity, this.radius) && ((LivingEntity)entity).world.getWorldBorder().contains(itemEntity.getBlockPos());
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        LookTargetUtil.walkTowards(entity, this.getNearestVisibleWantedItem(entity), this.speed, 0);
    }

    private boolean isInPickupCooldown(E entity) {
        return ((LivingEntity)entity).getBrain().isMemoryInState(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
    }

    private ItemEntity getNearestVisibleWantedItem(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}

