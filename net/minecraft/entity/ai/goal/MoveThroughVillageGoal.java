/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.DoorInteractGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class MoveThroughVillageGoal
extends Goal {
    protected final MobEntityWithAi mob;
    private final double speed;
    private Path targetPath;
    private BlockPos target;
    private final boolean requiresNighttime;
    private final List<BlockPos> field_18413 = Lists.newArrayList();
    private final int field_18414;
    private final BooleanSupplier field_18415;

    public MoveThroughVillageGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl, int i, BooleanSupplier booleanSupplier) {
        this.mob = mobEntityWithAi;
        this.speed = d;
        this.requiresNighttime = bl;
        this.field_18414 = i;
        this.field_18415 = booleanSupplier;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        if (!(mobEntityWithAi.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canStart() {
        this.method_6297();
        if (this.requiresNighttime && this.mob.world.isDay()) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = new BlockPos(this.mob);
        if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
            return false;
        }
        Vec3d vec3d = TargetFinder.findGroundTarget(this.mob, 15, 7, blockPos2 -> {
            if (!serverWorld.isNearOccupiedPointOfInterest((BlockPos)blockPos2)) {
                return Double.NEGATIVE_INFINITY;
            }
            Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::method_19052, (BlockPos)blockPos2, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            if (!optional.isPresent()) {
                return Double.NEGATIVE_INFINITY;
            }
            return -optional.get().getSquaredDistance(blockPos);
        });
        if (vec3d == null) {
            return false;
        }
        Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::method_19052, new BlockPos(vec3d), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
        if (!optional.isPresent()) {
            return false;
        }
        this.target = optional.get().toImmutable();
        MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
        boolean bl = mobNavigation.canEnterOpenDoors();
        mobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
        this.targetPath = mobNavigation.findPathTo(this.target, 0);
        mobNavigation.setCanPathThroughDoors(bl);
        if (this.targetPath == null) {
            Vec3d vec3d2 = TargetFinder.method_6373(this.mob, 10, 7, new Vec3d(this.target.getX(), this.target.getY(), this.target.getZ()));
            if (vec3d2 == null) {
                return false;
            }
            mobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
            this.targetPath = this.mob.getNavigation().findPathTo(vec3d2.x, vec3d2.y, vec3d2.z, 0);
            mobNavigation.setCanPathThroughDoors(bl);
            if (this.targetPath == null) {
                return false;
            }
        }
        for (int i = 0; i < this.targetPath.getLength(); ++i) {
            PathNode pathNode = this.targetPath.getNode(i);
            BlockPos blockPos22 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
            if (!DoorInteractGoal.isWoodenDoor(this.mob.world, blockPos22)) continue;
            this.targetPath = this.mob.getNavigation().findPathTo(pathNode.x, pathNode.y, pathNode.z, 0);
            break;
        }
        return this.targetPath != null;
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getNavigation().isIdle()) {
            return false;
        }
        return !this.target.isWithinDistance(this.mob.getPos(), (double)(this.mob.getWidth() + (float)this.field_18414));
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingAlong(this.targetPath, this.speed);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isIdle() || this.target.isWithinDistance(this.mob.getPos(), (double)this.field_18414)) {
            this.field_18413.add(this.target);
        }
    }

    private boolean method_19052(BlockPos blockPos) {
        for (BlockPos blockPos2 : this.field_18413) {
            if (!Objects.equals(blockPos, blockPos2)) continue;
            return false;
        }
        return true;
    }

    private void method_6297() {
        if (this.field_18413.size() > 15) {
            this.field_18413.remove(0);
        }
    }
}

