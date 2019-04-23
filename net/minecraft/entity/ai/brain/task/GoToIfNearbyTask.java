/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask
extends Task<MobEntityWithAi> {
    private final MemoryModuleType<GlobalPos> target;
    private long nextUpdateTime;
    private final int maxDistance;

    public GoToIfNearbyTask(MemoryModuleType<GlobalPos> memoryModuleType, int i) {
        this.target = memoryModuleType;
        this.maxDistance = i;
    }

    protected boolean method_18993(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.target);
        return optional.isPresent() && Objects.equals(serverWorld.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
    }

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED), Pair.of(this.target, MemoryModuleState.VALUE_PRESENT));
    }

    protected void method_18994(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        if (l > this.nextUpdateTime) {
            Optional<Vec3d> optional = Optional.ofNullable(PathfindingUtil.findTargetStraight(mobEntityWithAi, 8, 6));
            mobEntityWithAi.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget((Vec3d)vec3d, 0.4f, 1)));
            this.nextUpdateTime = l + 180L;
        }
    }
}

