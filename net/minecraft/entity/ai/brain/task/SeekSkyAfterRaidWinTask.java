/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class SeekSkyAfterRaidWinTask
extends SeekSkyTask {
    public SeekSkyAfterRaidWinTask(float f) {
        super(f);
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
        Raid raid = serverWorld.getRaidAt(new BlockPos(livingEntity));
        return raid != null && raid.hasWon() && super.shouldRun(serverWorld, livingEntity);
    }
}

