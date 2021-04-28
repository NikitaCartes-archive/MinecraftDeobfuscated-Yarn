/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public abstract class DoorInteractGoal
extends Goal {
    protected MobEntity mob;
    protected BlockPos doorPos = BlockPos.ORIGIN;
    protected boolean doorValid;
    private boolean shouldStop;
    private float offsetX;
    private float offsetZ;

    public DoorInteractGoal(MobEntity mob) {
        this.mob = mob;
        if (!NavigationConditions.hasMobNavigation(mob)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean isDoorOpen() {
        if (!this.doorValid) {
            return false;
        }
        BlockState blockState = this.mob.world.getBlockState(this.doorPos);
        if (!(blockState.getBlock() instanceof DoorBlock)) {
            this.doorValid = false;
            return false;
        }
        return blockState.get(DoorBlock.OPEN);
    }

    protected void setDoorOpen(boolean open) {
        BlockState blockState;
        if (this.doorValid && (blockState = this.mob.world.getBlockState(this.doorPos)).getBlock() instanceof DoorBlock) {
            ((DoorBlock)blockState.getBlock()).setOpen(this.mob, this.mob.world, blockState, this.doorPos, open);
        }
    }

    @Override
    public boolean canStart() {
        if (!NavigationConditions.hasMobNavigation(this.mob)) {
            return false;
        }
        if (!this.mob.horizontalCollision) {
            return false;
        }
        MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
        Path path = mobNavigation.getCurrentPath();
        if (path == null || path.isFinished() || !mobNavigation.canEnterOpenDoors()) {
            return false;
        }
        for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); ++i) {
            PathNode pathNode = path.getNode(i);
            this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
            if (this.mob.squaredDistanceTo(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25) continue;
            this.doorValid = DoorBlock.isWoodenDoor(this.mob.world, this.doorPos);
            if (!this.doorValid) continue;
            return true;
        }
        this.doorPos = this.mob.getBlockPos().up();
        this.doorValid = DoorBlock.isWoodenDoor(this.mob.world, this.doorPos);
        return this.doorValid;
    }

    @Override
    public boolean shouldContinue() {
        return !this.shouldStop;
    }

    @Override
    public void start() {
        this.shouldStop = false;
        this.offsetX = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        this.offsetZ = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ());
    }

    @Override
    public void tick() {
        float g;
        float f = (float)((double)this.doorPos.getX() + 0.5 - this.mob.getX());
        float h = this.offsetX * f + this.offsetZ * (g = (float)((double)this.doorPos.getZ() + 0.5 - this.mob.getZ()));
        if (h < 0.0f) {
            this.shouldStop = true;
        }
    }
}

