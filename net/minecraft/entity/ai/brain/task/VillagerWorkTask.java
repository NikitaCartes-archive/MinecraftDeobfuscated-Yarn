/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class VillagerWorkTask
extends Task<VillagerEntity> {
    private int ticks;
    private boolean field_18403;

    protected boolean method_19037(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return this.method_19036(serverWorld.getTimeOfDay() % 24000L, villagerEntity.getLastRestock());
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT), Pair.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), Pair.of(MemoryModuleType.GOLEM_SPAWN_CONDITIONS, MemoryModuleState.REGISTERED));
    }

    protected void method_19614(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        this.field_18403 = false;
        this.ticks = 0;
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    protected void method_19039(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        VillagerEntity.GolemSpawnCondition golemSpawnCondition = brain.getOptionalMemory(MemoryModuleType.GOLEM_SPAWN_CONDITIONS).orElseGet(VillagerEntity.GolemSpawnCondition::new);
        golemSpawnCondition.setLastWorked(l);
        brain.putMemory(MemoryModuleType.GOLEM_SPAWN_CONDITIONS, golemSpawnCondition);
        if (!this.field_18403) {
            villagerEntity.restock();
            this.field_18403 = true;
            villagerEntity.playWorkSound();
            brain.getOptionalMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos -> brain.putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
        }
        ++this.ticks;
    }

    protected boolean method_19040(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (!optional.isPresent()) {
            return false;
        }
        GlobalPos globalPos = optional.get();
        return this.ticks < 100 && Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType()) && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
    }

    private boolean method_19036(long l, long m) {
        return m == 0L || l < m || l > m + 3500L;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.method_19040(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19614(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19039(serverWorld, (VillagerEntity)livingEntity, l);
    }
}

