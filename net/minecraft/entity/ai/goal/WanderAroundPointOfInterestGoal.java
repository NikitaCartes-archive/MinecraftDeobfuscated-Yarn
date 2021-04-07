/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundPointOfInterestGoal
extends WanderAroundGoal {
    private static final int HORIZONTAL_RANGE = 10;
    private static final int VERTICAL_RANGE = 7;

    public WanderAroundPointOfInterestGoal(PathAwareEntity entity, double speed, boolean canDespawn) {
        super(entity, speed, 10, canDespawn);
    }

    @Override
    public boolean canStart() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = this.mob.getBlockPos();
        if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
            return false;
        }
        return super.canStart();
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = this.mob.getBlockPos();
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
        ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
        if (chunkSectionPos2 != chunkSectionPos) {
            return NoPenaltyTargeting.find(this.mob, 10, 7, Vec3d.ofBottomCenter(chunkSectionPos2.getCenterPos()), 1.5707963705062866);
        }
        return null;
    }
}

