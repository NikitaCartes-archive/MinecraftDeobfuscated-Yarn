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
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(super.getNode(MathHelper.floor(d - (double)(this.entity.getWidth() / 2.0f)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.entity.getWidth() / 2.0f))));
    }

    @Override
    public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
        int i = 0;
        for (Direction direction : Direction.values()) {
            PathNode pathNode2 = this.getPathNodeInWater(pathNode.x + direction.getOffsetX(), pathNode.y + direction.getOffsetY(), pathNode.z + direction.getOffsetZ());
            if (pathNode2 == null || pathNode2.visited) continue;
            pathNodes[i++] = pathNode2;
        }
        return i;
    }

    @Override
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
        return this.getNodeType(blockView, i, j, k);
    }

    @Override
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
        BlockPos blockPos = new BlockPos(i, j, k);
        FluidState fluidState = blockView.getFluidState(blockPos);
        BlockState blockState = blockView.getBlockState(blockPos);
        if (fluidState.isEmpty() && blockState.canPlaceAtSide(blockView, blockPos.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
            return PathNodeType.BREACH;
        }
        if (!fluidState.matches(FluidTags.WATER) || !blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.WATER)) {
            return PathNodeType.BLOCKED;
        }
        return PathNodeType.WATER;
    }

    @Nullable
    private PathNode getPathNodeInWater(int i, int j, int k) {
        PathNodeType pathNodeType = this.getNodeType(i, j, k);
        if (this.field_58 && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
            return this.getNode(i, j, k);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getNode(int i, int j, int k) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.getNodeType(this.entity.world, i, j, k);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(i, j, k);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (this.field_20622.getFluidState(new BlockPos(i, j, k)).isEmpty()) {
                pathNode.penalty += 8.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            return pathNode;
        }
        return pathNode;
    }

    private PathNodeType getNodeType(int i, int j, int k) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = i; l < i + this.field_31; ++l) {
            for (int m = j; m < j + this.field_30; ++m) {
                for (int n = k; n < k + this.field_28; ++n) {
                    FluidState fluidState = this.field_20622.getFluidState(mutable.set(l, m, n));
                    BlockState blockState = this.field_20622.getBlockState(mutable.set(l, m, n));
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

