/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class VillagerWalkTowardsTask
extends Task<VillagerEntity> {
    private final MemoryModuleType<GlobalPos> field_18382;
    private final float field_18383;
    private final int field_18384;
    private final int field_18385;

    public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j) {
        this.field_18382 = memoryModuleType;
        this.field_18383 = f;
        this.field_18384 = i;
        this.field_18385 = j;
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(this.field_18382, MemoryModuleState.VALUE_PRESENT));
    }

    protected void method_19509(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        brain.getOptionalMemory(this.field_18382).ifPresent(globalPos -> {
            if (this.method_19597(serverWorld, villagerEntity, (GlobalPos)globalPos)) {
                villagerEntity.releaseTicketFor(this.field_18382);
                brain.forget(this.field_18382);
            } else if (!this.method_19988(serverWorld, villagerEntity, (GlobalPos)globalPos)) {
                brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), this.field_18383, this.field_18384));
            }
        });
    }

    private boolean method_19597(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
        return globalPos.getDimension() != serverWorld.getDimension().getType() || globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) > this.field_18385;
    }

    private boolean method_19988(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
        return globalPos.getDimension() == serverWorld.getDimension().getType() && globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) <= this.field_18384;
    }
}

