/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

public class VillagerWorkTask
extends Task<VillagerEntity> {
    private static final int RUN_TIME = 300;
    private static final double MAX_DISTANCE = 1.73;
    private long lastCheckedTime;

    public VillagerWorkTask() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getTime();
        GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        brain.remember(MemoryModuleType.LAST_WORKED_AT_POI, l);
        brain.getOptionalMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
        villagerEntity.playWorkSound();
        this.performAdditionalWork(serverWorld, villagerEntity);
        if (villagerEntity.shouldRestock()) {
            villagerEntity.restock();
        }
    }

    protected void performAdditionalWork(ServerWorld world, VillagerEntity entity) {
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return false;
        }
        GlobalPos globalPos = optional.get();
        return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
    }

    @Override
    protected /* synthetic */ boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return this.shouldRun(world, (VillagerEntity)entity);
    }
}

