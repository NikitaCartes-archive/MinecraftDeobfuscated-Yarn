/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

public class GoToVillageGoal
extends Goal {
    private final MobEntityWithAi mob;
    private final int searchRange;
    @Nullable
    private BlockPos targetPosition;

    public GoToVillageGoal(MobEntityWithAi mob, int searchRange) {
        this.mob = mob;
        this.searchRange = searchRange;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.hasPassengers()) {
            return false;
        }
        if (this.mob.world.isDay()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(this.searchRange) != 0) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos2 = this.mob.getBlockPos();
        if (!serverWorld.isNearOccupiedPointOfInterest(blockPos2, 6)) {
            return false;
        }
        Vec3d vec3d = TargetFinder.findGroundTarget(this.mob, 15, 7, blockPos -> -serverWorld.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(blockPos)));
        this.targetPosition = vec3d == null ? null : new BlockPos(vec3d);
        return this.targetPosition != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPosition != null && !this.mob.getNavigation().isIdle() && this.mob.getNavigation().getTargetPos().equals(this.targetPosition);
    }

    @Override
    public void tick() {
        if (this.targetPosition == null) {
            return;
        }
        EntityNavigation entityNavigation = this.mob.getNavigation();
        if (entityNavigation.isIdle() && !this.targetPosition.isWithinDistance(this.mob.getPos(), 10.0)) {
            Vec3d vec3d = Vec3d.method_24955(this.targetPosition);
            Vec3d vec3d2 = this.mob.getPos();
            Vec3d vec3d3 = vec3d2.subtract(vec3d);
            vec3d = vec3d3.multiply(0.4).add(vec3d);
            Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
            BlockPos blockPos = new BlockPos(vec3d4);
            if (!entityNavigation.startMovingTo((blockPos = this.mob.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos)).getX(), blockPos.getY(), blockPos.getZ(), 1.0)) {
                this.findOtherWaypoint();
            }
        }
    }

    private void findOtherWaypoint() {
        Random random = this.mob.getRandom();
        BlockPos blockPos = this.mob.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
    }
}

