/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class HideInHomeTask
extends Task<LivingEntity> {
    private final float walkSpeed;
    private final int maxDistance;
    private final int preferredDistance;
    private Optional<BlockPos> homePosition = Optional.empty();

    public HideInHomeTask(int i, float f, int j) {
        this.maxDistance = i;
        this.walkSpeed = f;
        this.preferredDistance = j;
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.HOME, MemoryModuleState.REGISTERED), Pair.of(MemoryModuleType.HIDING_PLACE, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
        Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME, blockPos -> true, new BlockPos(livingEntity), this.preferredDistance + 1, PointOfInterestStorage.OccupationStatus.ANY);
        this.homePosition = optional.isPresent() && optional.get().isWithinDistance(livingEntity.getPos(), (double)this.preferredDistance) ? optional : Optional.empty();
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        Optional<GlobalPos> optional2;
        Brain<Object> brain = livingEntity.getBrain();
        Optional<BlockPos> optional = this.homePosition;
        if (!optional.isPresent() && !(optional = serverWorld.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME, blockPos -> true, PointOfInterestStorage.OccupationStatus.ANY, new BlockPos(livingEntity), this.maxDistance, livingEntity.getRand())).isPresent() && (optional2 = brain.getOptionalMemory(MemoryModuleType.HOME)).isPresent()) {
            optional = Optional.of(optional2.get().getPos());
        }
        if (optional.isPresent()) {
            brain.forget(MemoryModuleType.PATH);
            brain.forget(MemoryModuleType.LOOK_TARGET);
            brain.forget(MemoryModuleType.BREED_TARGET);
            brain.forget(MemoryModuleType.INTERACTION_TARGET);
            brain.putMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.create(serverWorld.getDimension().getType(), optional.get()));
            if (!optional.get().isWithinDistance(livingEntity.getPos(), (double)this.preferredDistance)) {
                brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(optional.get(), this.walkSpeed, this.preferredDistance));
            }
        }
    }
}

