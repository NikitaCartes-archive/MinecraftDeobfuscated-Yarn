/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
        return super.getPathNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }

    @Override
    public PathNode getPathNode(double d, double e, double f) {
        return super.getPathNode(MathHelper.floor(d - (double)(this.entity.getWidth() / 2.0f)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.entity.getWidth() / 2.0f)));
    }

    @Override
    public int getPathNodes(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
        int i = 0;
        for (Direction direction : Direction.values()) {
            PathNode pathNode3 = this.getPathNodeInWater(pathNode.x + direction.getOffsetX(), pathNode.y + direction.getOffsetY(), pathNode.z + direction.getOffsetZ());
            if (pathNode3 == null || pathNode3.field_42 || !(pathNode3.distance(pathNode2) < f)) continue;
            pathNodes[i++] = pathNode3;
        }
        return i;
    }

    @Override
    public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
        return this.getPathNodeType(blockView, i, j, k);
    }

    @Override
    public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
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
        PathNodeType pathNodeType = this.getPathNodeType(i, j, k);
        if (this.field_58 && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
            return this.getPathNode(i, j, k);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getPathNode(int i, int j, int k) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.getPathNodeType(this.entity.world, i, j, k);
        float f = this.entity.getPathNodeTypeWeight(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getPathNode(i, j, k);
            pathNode.type = pathNodeType;
            pathNode.field_43 = Math.max(pathNode.field_43, f);
            if (this.blockView.getFluidState(new BlockPos(i, j, k)).isEmpty()) {
                pathNode.field_43 += 8.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            return pathNode;
        }
        return pathNode;
    }

    private PathNodeType getPathNodeType(int i, int j, int k) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = i; l < i + this.field_31; ++l) {
            for (int m = j; m < j + this.field_30; ++m) {
                for (int n = k; n < k + this.field_28; ++n) {
                    FluidState fluidState = this.blockView.getFluidState(mutable.set(l, m, n));
                    BlockState blockState = this.blockView.getBlockState(mutable.set(l, m, n));
                    if (fluidState.isEmpty() && blockState.canPlaceAtSide(this.blockView, mutable.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
                        return PathNodeType.BREACH;
                    }
                    if (fluidState.matches(FluidTags.WATER)) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        BlockState blockState2 = this.blockView.getBlockState(mutable);
        if (blockState2.canPlaceAtSide(this.blockView, mutable, BlockPlacementEnvironment.WATER)) {
            return PathNodeType.WATER;
        }
        return PathNodeType.BLOCKED;
    }
}

