/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
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
    private final boolean field_58;

    public WaterPathNodeMaker(boolean bl) {
        this.field_58 = bl;
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
        return this.getNodeType(world, x, y, z);
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        FluidState fluidState = world.getFluidState(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        if (fluidState.isEmpty() && blockState.canPlaceAtSide(world, blockPos.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
            return PathNodeType.BREACH;
        }
        if (!fluidState.matches(FluidTags.WATER) || !blockState.canPlaceAtSide(world, blockPos, BlockPlacementEnvironment.WATER)) {
            return PathNodeType.BLOCKED;
        }
        return PathNodeType.WATER;
    }

    @Nullable
    private PathNode getPathNodeInWater(int x, int y, int i) {
        PathNodeType pathNodeType = this.getNodeType(x, y, i);
        if (this.field_58 && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
            return this.getNode(x, y, i);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getNode(int x, int y, int z) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.getNodeType(this.entity.world, x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (this.field_20622.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                pathNode.penalty += 8.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            return pathNode;
        }
        return pathNode;
    }

    private PathNodeType getNodeType(int x, int y, int i) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = x; j < x + this.field_31; ++j) {
            for (int k = y; k < y + this.field_30; ++k) {
                for (int l = i; l < i + this.field_28; ++l) {
                    FluidState fluidState = this.field_20622.getFluidState(mutable.set(j, k, l));
                    BlockState blockState = this.field_20622.getBlockState(mutable.set(j, k, l));
                    if (fluidState.isEmpty() && blockState.canPlaceAtSide(this.field_20622, (BlockPos)mutable.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
                        return PathNodeType.BREACH;
                    }
                    if (fluidState.matches(FluidTags.WATER)) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        BlockState blockState2 = this.field_20622.getBlockState(mutable);
        if (blockState2.canPlaceAtSide(this.field_20622, mutable, BlockPlacementEnvironment.WATER)) {
            return PathNodeType.WATER;
        }
        return PathNodeType.BLOCKED;
    }
}

