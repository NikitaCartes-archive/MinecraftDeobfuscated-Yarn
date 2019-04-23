/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import org.jetbrains.annotations.Nullable;

public class GoToSecondaryPositionTask
extends Task<VillagerEntity> {
    private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
    private final MemoryModuleType<GlobalPos> primaryPosition;
    private final float speed;
    private final int completionRange;
    private final int primaryPositionActivationDistance;
    private long nextRunTime;
    @Nullable
    private GlobalPos chosenPosition;

    public GoToSecondaryPositionTask(MemoryModuleType<List<GlobalPos>> memoryModuleType, float f, int i, int j, MemoryModuleType<GlobalPos> memoryModuleType2) {
        this.secondaryPositions = memoryModuleType;
        this.speed = f;
        this.completionRange = i;
        this.primaryPositionActivationDistance = j;
        this.primaryPosition = memoryModuleType2;
    }

    protected boolean method_19609(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        List<GlobalPos> list;
        Optional<List<GlobalPos>> optional = villagerEntity.getBrain().getOptionalMemory(this.secondaryPositions);
        Optional<GlobalPos> optional2 = villagerEntity.getBrain().getOptionalMemory(this.primaryPosition);
        if (optional.isPresent() && optional2.isPresent() && !(list = optional.get()).isEmpty()) {
            this.chosenPosition = list.get(serverWorld.getRandom().nextInt(list.size()));
            return this.chosenPosition != null && Objects.equals(serverWorld.getDimension().getType(), this.chosenPosition.getDimension()) && optional2.get().getPos().isWithinDistance(villagerEntity.getPos(), (double)this.primaryPositionActivationDistance);
        }
        return false;
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED), Pair.of(this.secondaryPositions, MemoryModuleState.VALUE_PRESENT), Pair.of(this.primaryPosition, MemoryModuleState.VALUE_PRESENT));
    }

    protected void method_19610(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.nextRunTime && this.chosenPosition != null) {
            villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
            this.nextRunTime = l + 100L;
        }
    }
}

