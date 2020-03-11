/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class WaterPathNodeMaker
extends PathNodeMaker {
    private final boolean canJumpOutOfWater;

    public WaterPathNodeMaker(boolean canJumpOutOfWater) {
        this.canJumpOutOfWater = canJumpOutOfWater;
    }

    @Override
    public PathNode getStart() {
        return super.getNode(MathHelper.floor(this.entity.getBoundingBox().x1), MathHelper.floor(this.entity.getBoundingBox().y1 + 0.5), MathHelper.floor(this.entity.getBoundingBox().z1));
    }

    @Override
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(super.getNode(MathHelper.floor(x - (double)(this.entity.getWidth() / 2.0f)), MathHelper.floor(y + 0.5), MathHelper.floor(z - (double)(this.entity.getWidth() / 2.0f))));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        int i = 0;
        for (Direction direction : Direction.values()) {
            PathNode pathNode = this.getPathNodeInWater(node.x + direction.getOffsetX(), node.y + direction.getOffsetY(), node.z + direction.getOffsetZ());
            if (pathNode == null || pathNode.visited) continue;
            successors[i++] = pathNode;
        }
        return i;
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        return this.getDefaultNodeType(world, x, y, z);
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        FluidState fluidState = world.getFluidState(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        if (fluidState.isEmpty() && blockState.canPathfindThrough(world, blockPos.down(), NavigationType.WATER) && blockState.isAir()) {
            return PathNodeType.BREACH;
        }
        if (!fluidState.matches(FluidTags.WATER) || !blockState.canPathfindThrough(world, blockPos, NavigationType.WATER)) {
            return PathNodeType.BLOCKED;
        }
        return PathNodeType.WATER;
    }

    @Nullable
    private PathNode getPathNodeInWater(int x, int y, int z) {
        PathNodeType pathNodeType = this.getNodeType(x, y, z);
        if (this.canJumpOutOfWater && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
            return this.getNode(x, y, z);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getNode(int x, int y, int z) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.getDefaultNodeType(this.entity.world, x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (this.cachedWorld.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                pathNode.penalty += 8.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            return pathNode;
        }
        return pathNode;
    }

    private PathNodeType getNodeType(int x, int y, int z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = x; i < x + this.entityBlockXSize; ++i) {
            for (int j = y; j < y + this.entityBlockYSize; ++j) {
                for (int k = z; k < z + this.entityBlockZSize; ++k) {
                    FluidState fluidState = this.cachedWorld.getFluidState(mutable.set(i, j, k));
                    BlockState blockState = this.cachedWorld.getBlockState(mutable.set(i, j, k));
                    if (fluidState.isEmpty() && blockState.canPathfindThrough(this.cachedWorld, (BlockPos)mutable.down(), NavigationType.WATER) && blockState.isAir()) {
                        return PathNodeType.BREACH;
                    }
                    if (fluidState.matches(FluidTags.WATER)) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        BlockState blockState2 = this.cachedWorld.getBlockState(mutable);
        if (blockState2.canPathfindThrough(this.cachedWorld, mutable, NavigationType.WATER)) {
            return PathNodeType.WATER;
        }
        return PathNodeType.BLOCKED;
    }
}

