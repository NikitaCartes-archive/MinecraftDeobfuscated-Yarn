/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

public class WalkTowardJobSiteTask
extends Task<VillagerEntity> {
    private static final int RUN_TIME = 1200;
    final float speed;

    public WalkTowardJobSiteTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT), 1200);
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return villagerEntity.getBrain().getFirstPossibleNonCoreActivity().map(activity -> activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY).orElse(true);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        LookTargetUtil.walkTowards((LivingEntity)villagerEntity, villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.speed, 1);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent(globalPos -> {
            BlockPos blockPos = globalPos.getPos();
            ServerWorld serverWorld2 = serverWorld.getServer().getWorld(globalPos.getDimension());
            if (serverWorld2 == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld2.getPointOfInterestStorage();
            if (pointOfInterestStorage.test(blockPos, pointOfInterestType -> true)) {
                pointOfInterestStorage.releaseTicket(blockPos);
            }
            DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
        });
        villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }
}

