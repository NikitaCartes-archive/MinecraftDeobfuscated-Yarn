/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FollowCustomerTask
extends Task<VillagerEntity> {
    private final float speed;

    public FollowCustomerTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), Integer.MAX_VALUE);
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        PlayerEntity playerEntity = villagerEntity.getCustomer();
        return villagerEntity.isAlive() && playerEntity != null && !villagerEntity.isTouchingWater() && !villagerEntity.velocityModified && villagerEntity.squaredDistanceTo(playerEntity) <= 16.0 && playerEntity.currentScreenHandler != null;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.shouldRun(serverWorld, villagerEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        this.update(villagerEntity);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        brain.forget(MemoryModuleType.WALK_TARGET);
        brain.forget(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        this.update(villagerEntity);
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    private void update(VillagerEntity villager) {
        Brain<VillagerEntity> brain = villager.getBrain();
        brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(villager.getCustomer(), false), this.speed, 2));
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(villager.getCustomer(), true));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

