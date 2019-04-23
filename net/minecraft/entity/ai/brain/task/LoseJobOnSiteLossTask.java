/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class LoseJobOnSiteLossTask
extends Task<VillagerEntity> {
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT));
    }

    protected boolean method_20449(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        VillagerData villagerData = villagerEntity.getVillagerData();
        return villagerData.getProfession() != VillagerProfession.NONE && villagerData.getProfession() != VillagerProfession.NITWIT && villagerEntity.getExperience() == 0 && villagerData.getLevel() <= 1;
    }

    protected void method_20450(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(VillagerProfession.NONE));
        villagerEntity.reinitializeBrain(serverWorld);
    }
}

