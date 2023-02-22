/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class AmphibiousPathNodeMaker
extends LandPathNodeMaker {
    private final boolean penalizeDeepWater;
    private float oldWalkablePenalty;
    private float oldWaterBorderPenalty;

    public AmphibiousPathNodeMaker(boolean penalizeDeepWater) {
        this.penalizeDeepWater = penalizeDeepWater;
    }

    @Override
    public void init(ChunkCache cachedWorld, MobEntity entity) {
        super.init(cachedWorld, entity);
        entity.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.oldWalkablePenalty = entity.getPathfindingPenalty(PathNodeType.WALKABLE);
        entity.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0f);
        this.oldWaterBorderPenalty = entity.getPathfindingPenalty(PathNodeType.WATER_BORDER);
        entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0f);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WALKABLE, this.oldWalkablePenalty);
        this.entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.oldWaterBorderPenalty);
        super.clear();
    }

    @Override
    public PathNode getStart() {
        if (!this.entity.isTouchingWater()) {
            return super.getStart();
        }
        return this.getStart(new BlockPos(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ)));
    }

    @Override
    public TargetPathNode getNode(double x, double y, double z) {
        return this.asTargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y + 0.5), MathHelper.floor(z)));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        int i = super.getSuccessors(successors, node);
        PathNodeType pathNodeType = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
        PathNodeType pathNodeType2 = this.getNodeType(this.entity, node.x, node.y, node.z);
        int j = this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f && pathNodeType2 != PathNodeType.STICKY_HONEY ? MathHelper.floor(Math.max(1.0f, this.entity.getStepHeight())) : 0;
        double d = this.getFeetY(new BlockPos(node.x, node.y, node.z));
        PathNode pathNode = this.getPathNode(node.x, node.y + 1, node.z, Math.max(0, j - 1), d, Direction.UP, pathNodeType2);
        PathNode pathNode2 = this.getPathNode(node.x, node.y - 1, node.z, j, d, Direction.DOWN, pathNodeType2);
        if (this.isValidAquaticAdjacentSuccessor(pathNode, node)) {
            successors[i++] = pathNode;
        }
        if (this.isValidAquaticAdjacentSuccessor(pathNode2, node) && pathNodeType2 != PathNodeType.TRAPDOOR) {
            successors[i++] = pathNode2;
        }
        for (int k = 0; k < i; ++k) {
            PathNode pathNode3 = successors[k];
            if (pathNode3.type != PathNodeType.WATER || !this.penalizeDeepWater || pathNode3.y >= this.entity.world.getSeaLevel() - 10) continue;
            pathNode3.penalty += 1.0f;
        }
        return i;
    }

    private boolean isValidAquaticAdjacentSuccessor(@Nullable PathNode node, PathNode successor) {
        return this.isValidAdjacentSuccessor(node, successor) && node.type == PathNodeType.WATER;
    }

    @Override
    protected boolean isAmphibious() {
        return true;
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        PathNodeType pathNodeType = AmphibiousPathNodeMaker.getCommonNodeType(world, mutable.set(x, y, z));
        if (pathNodeType == PathNodeType.WATER) {
            for (Direction direction : Direction.values()) {
                PathNodeType pathNodeType2 = AmphibiousPathNodeMaker.getCommonNodeType(world, mutable.set(x, y, z).move(direction));
                if (pathNodeType2 != PathNodeType.BLOCKED) continue;
                return PathNodeType.WATER_BORDER;
            }
            return PathNodeType.WATER;
        }
        return AmphibiousPathNodeMaker.getLandNodeType(world, mutable);
    }
}

