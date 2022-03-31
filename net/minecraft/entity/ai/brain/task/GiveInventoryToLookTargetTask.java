/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GiveInventoryToLookTargetTask<E extends LivingEntity>
extends Task<E> {
    private static final int field_38387 = 3;
    private static final int field_38388 = 100;
    private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
    private final float speed;

    public GiveInventoryToLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, float speed) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.REGISTERED));
        this.lookTargetFunction = lookTargetFunction;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.hasItemAndTarget(entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return this.hasItemAndTarget(entity);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        this.lookTargetFunction.apply((LivingEntity)entity).ifPresent(target -> LookTargetUtil.walkTowards(entity, target, this.speed, 3));
    }

    @Override
    protected void keepRunning(ServerWorld world, E entity, long time) {
        ItemStack itemStack;
        Optional<LookTarget> optional = this.lookTargetFunction.apply((LivingEntity)entity);
        if (optional.isEmpty()) {
            return;
        }
        LookTarget lookTarget = optional.get();
        double d = lookTarget.getPos().distanceTo(((Entity)entity).getEyePos());
        if (d < 3.0 && !(itemStack = ((InventoryOwner)entity).getInventory().removeStack(0, 1)).isEmpty()) {
            LookTargetUtil.give(entity, itemStack, GiveInventoryToLookTargetTask.offsetTarget(lookTarget));
            ((LivingEntity)entity).getBrain().remember(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 100);
        }
    }

    private boolean hasItemAndTarget(E entity) {
        return !((InventoryOwner)entity).getInventory().isEmpty() && this.lookTargetFunction.apply((LivingEntity)entity).isPresent();
    }

    private static Vec3d offsetTarget(LookTarget target) {
        return target.getPos().add(0.0, 1.0, 0.0);
    }
}

