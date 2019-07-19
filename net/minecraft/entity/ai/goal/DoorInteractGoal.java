/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DoorInteractGoal
extends Goal {
    protected MobEntity mob;
    protected BlockPos doorPos = BlockPos.ORIGIN;
    protected boolean field_6412;
    private boolean shouldStop;
    private float field_6410;
    private float field_6409;

    public DoorInteractGoal(MobEntity mobEntity) {
        this.mob = mobEntity;
        if (!(mobEntity.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean method_6256() {
        if (!this.field_6412) {
            return false;
        }
        BlockState blockState = this.mob.world.getBlockState(this.doorPos);
        if (!(blockState.getBlock() instanceof DoorBlock)) {
            this.field_6412 = false;
            return false;
        }
        return blockState.get(DoorBlock.OPEN);
    }

    protected void setDoorOpen(boolean bl) {
        BlockState blockState;
        if (this.field_6412 && (blockState = this.mob.world.getBlockState(this.doorPos)).getBlock() instanceof DoorBlock) {
            ((DoorBlock)blockState.getBlock()).setOpen(this.mob.world, this.doorPos, bl);
        }
    }

    @Override
    public boolean canStart() {
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
            if (this.mob.squaredDistanceTo(this.doorPos.getX(), this.mob.y, this.doorPos.getZ()) > 2.25) continue;
            this.field_6412 = DoorInteractGoal.isWoodenDoor(this.mob.world, this.doorPos);
            if (!this.field_6412) continue;
            return true;
        }
        this.doorPos = new BlockPos(this.mob).up();
        this.field_6412 = DoorInteractGoal.isWoodenDoor(this.mob.world, this.doorPos);
        return this.field_6412;
    }

    @Override
    public boolean shouldContinue() {
        return !this.shouldStop;
    }

    @Override
    public void start() {
        this.shouldStop = false;
        this.field_6410 = (float)((double)((float)this.doorPos.getX() + 0.5f) - this.mob.x);
        this.field_6409 = (float)((double)((float)this.doorPos.getZ() + 0.5f) - this.mob.z);
    }

    @Override
    public void tick() {
        float g;
        float f = (float)((double)((float)this.doorPos.getX() + 0.5f) - this.mob.x);
        float h = this.field_6410 * f + this.field_6409 * (g = (float)((double)((float)this.doorPos.getZ() + 0.5f) - this.mob.z));
        if (h < 0.0f) {
            this.shouldStop = true;
        }
    }

    public static boolean isWoodenDoor(World world, BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getBlock() instanceof DoorBlock && blockState.getMaterial() == Material.WOOD;
    }
}

