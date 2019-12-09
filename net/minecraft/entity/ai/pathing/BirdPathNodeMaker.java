/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class BirdPathNodeMaker
extends LandPathNodeMaker {
    @Override
    public void init(ChunkCache chunkCache, MobEntity mobEntity) {
        super.init(chunkCache, mobEntity);
        this.waterPathNodeTypeWeight = mobEntity.getPathfindingPenalty(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos blockPos;
        PathNodeType pathNodeType;
        int i;
        if (this.canSwim() && this.entity.isInsideWater()) {
            i = MathHelper.floor(this.entity.getY());
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());
            Block block = this.field_20622.getBlockState(mutable).getBlock();
            while (block == Blocks.WATER) {
                mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
                block = this.field_20622.getBlockState(mutable).getBlock();
            }
        } else {
            i = MathHelper.floor(this.entity.getY() + 0.5);
        }
        if (this.entity.getPathfindingPenalty(pathNodeType = this.method_9(this.entity, (blockPos = new BlockPos(this.entity)).getX(), i, blockPos.getZ())) < 0.0f) {
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z1));
            set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z2));
            set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z1));
            set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z2));
            for (BlockPos blockPos2 : set) {
                PathNodeType pathNodeType2 = this.method_10(this.entity, blockPos2);
                if (!(this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0f)) continue;
                return super.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return super.getNode(blockPos.getX(), i, blockPos.getZ());
    }

    @Override
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(super.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        PathNode pathNode26;
        PathNode pathNode25;
        PathNode pathNode24;
        PathNode pathNode23;
        PathNode pathNode22;
        PathNode pathNode21;
        PathNode pathNode20;
        PathNode pathNode19;
        PathNode pathNode18;
        PathNode pathNode17;
        PathNode pathNode16;
        PathNode pathNode15;
        PathNode pathNode14;
        PathNode pathNode13;
        PathNode pathNode12;
        PathNode pathNode11;
        PathNode pathNode10;
        PathNode pathNode9;
        PathNode pathNode8;
        PathNode pathNode7;
        PathNode pathNode6;
        PathNode pathNode5;
        PathNode pathNode4;
        PathNode pathNode3;
        PathNode pathNode2;
        int i = 0;
        PathNode pathNode = this.getNode(node.x, node.y, node.z + 1);
        if (this.method_22878(pathNode)) {
            successors[i++] = pathNode;
        }
        if (this.method_22878(pathNode2 = this.getNode(node.x - 1, node.y, node.z))) {
            successors[i++] = pathNode2;
        }
        if (this.method_22878(pathNode3 = this.getNode(node.x + 1, node.y, node.z))) {
            successors[i++] = pathNode3;
        }
        if (this.method_22878(pathNode4 = this.getNode(node.x, node.y, node.z - 1))) {
            successors[i++] = pathNode4;
        }
        if (this.method_22878(pathNode5 = this.getNode(node.x, node.y + 1, node.z))) {
            successors[i++] = pathNode5;
        }
        if (this.method_22878(pathNode6 = this.getNode(node.x, node.y - 1, node.z))) {
            successors[i++] = pathNode6;
        }
        if (this.method_22878(pathNode7 = this.getNode(node.x, node.y + 1, node.z + 1)) && this.method_22877(pathNode) && this.method_22877(pathNode5)) {
            successors[i++] = pathNode7;
        }
        if (this.method_22878(pathNode8 = this.getNode(node.x - 1, node.y + 1, node.z)) && this.method_22877(pathNode2) && this.method_22877(pathNode5)) {
            successors[i++] = pathNode8;
        }
        if (this.method_22878(pathNode9 = this.getNode(node.x + 1, node.y + 1, node.z)) && this.method_22877(pathNode3) && this.method_22877(pathNode5)) {
            successors[i++] = pathNode9;
        }
        if (this.method_22878(pathNode10 = this.getNode(node.x, node.y + 1, node.z - 1)) && this.method_22877(pathNode4) && this.method_22877(pathNode5)) {
            successors[i++] = pathNode10;
        }
        if (this.method_22878(pathNode11 = this.getNode(node.x, node.y - 1, node.z + 1)) && this.method_22877(pathNode) && this.method_22877(pathNode6)) {
            successors[i++] = pathNode11;
        }
        if (this.method_22878(pathNode12 = this.getNode(node.x - 1, node.y - 1, node.z)) && this.method_22877(pathNode2) && this.method_22877(pathNode6)) {
            successors[i++] = pathNode12;
        }
        if (this.method_22878(pathNode13 = this.getNode(node.x + 1, node.y - 1, node.z)) && this.method_22877(pathNode3) && this.method_22877(pathNode6)) {
            successors[i++] = pathNode13;
        }
        if (this.method_22878(pathNode14 = this.getNode(node.x, node.y - 1, node.z - 1)) && this.method_22877(pathNode4) && this.method_22877(pathNode6)) {
            successors[i++] = pathNode14;
        }
        if (this.method_22878(pathNode15 = this.getNode(node.x + 1, node.y, node.z - 1)) && this.method_22877(pathNode4) && this.method_22877(pathNode3)) {
            successors[i++] = pathNode15;
        }
        if (this.method_22878(pathNode16 = this.getNode(node.x + 1, node.y, node.z + 1)) && this.method_22877(pathNode) && this.method_22877(pathNode3)) {
            successors[i++] = pathNode16;
        }
        if (this.method_22878(pathNode17 = this.getNode(node.x - 1, node.y, node.z - 1)) && this.method_22877(pathNode4) && this.method_22877(pathNode2)) {
            successors[i++] = pathNode17;
        }
        if (this.method_22878(pathNode18 = this.getNode(node.x - 1, node.y, node.z + 1)) && this.method_22877(pathNode) && this.method_22877(pathNode2)) {
            successors[i++] = pathNode18;
        }
        if (this.method_22878(pathNode19 = this.getNode(node.x + 1, node.y + 1, node.z - 1)) && this.method_22877(pathNode15) && this.method_22877(pathNode10) && this.method_22877(pathNode9)) {
            successors[i++] = pathNode19;
        }
        if (this.method_22878(pathNode20 = this.getNode(node.x + 1, node.y + 1, node.z + 1)) && this.method_22877(pathNode16) && this.method_22877(pathNode7) && this.method_22877(pathNode9)) {
            successors[i++] = pathNode20;
        }
        if (this.method_22878(pathNode21 = this.getNode(node.x - 1, node.y + 1, node.z - 1)) && this.method_22877(pathNode17) && this.method_22877(pathNode10) && this.method_22877(pathNode8)) {
            successors[i++] = pathNode21;
        }
        if (this.method_22878(pathNode22 = this.getNode(node.x - 1, node.y + 1, node.z + 1)) && this.method_22877(pathNode18) && this.method_22877(pathNode7) && this.method_22877(pathNode8)) {
            successors[i++] = pathNode22;
        }
        if (this.method_22878(pathNode23 = this.getNode(node.x + 1, node.y - 1, node.z - 1)) && this.method_22877(pathNode15) && this.method_22877(pathNode14) && this.method_22877(pathNode13)) {
            successors[i++] = pathNode23;
        }
        if (this.method_22878(pathNode24 = this.getNode(node.x + 1, node.y - 1, node.z + 1)) && this.method_22877(pathNode16) && this.method_22877(pathNode11) && this.method_22877(pathNode13)) {
            successors[i++] = pathNode24;
        }
        if (this.method_22878(pathNode25 = this.getNode(node.x - 1, node.y - 1, node.z - 1)) && this.method_22877(pathNode17) && this.method_22877(pathNode14) && this.method_22877(pathNode12)) {
            successors[i++] = pathNode25;
        }
        if (this.method_22878(pathNode26 = this.getNode(node.x - 1, node.y - 1, node.z + 1)) && this.method_22877(pathNode18) && this.method_22877(pathNode11) && this.method_22877(pathNode12)) {
            successors[i++] = pathNode26;
        }
        return i;
    }

    private boolean method_22877(@Nullable PathNode pathNode) {
        return pathNode != null && pathNode.penalty >= 0.0f;
    }

    private boolean method_22878(@Nullable PathNode pathNode) {
        return pathNode != null && !pathNode.visited;
    }

    @Override
    @Nullable
    protected PathNode getNode(int x, int y, int z) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.method_9(this.entity, x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (pathNodeType == PathNodeType.WALKABLE) {
                pathNode.penalty += 1.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN || pathNodeType == PathNodeType.WALKABLE) {
            return pathNode;
        }
        return pathNode;
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = new BlockPos(mob);
        pathNodeType = this.getNodeType(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (mob.getPathfindingPenalty(pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(mob.getPathfindingPenalty(pathNodeType3) >= mob.getPathfindingPenalty(pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && mob.getPathfindingPenalty(pathNodeType2) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z) {
        PathNodeType pathNodeType = BirdPathNodeMaker.getBasicPathNodeType(world, x, y, z);
        if (pathNodeType == PathNodeType.OPEN && y >= 1) {
            Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathNodeType2 = BirdPathNodeMaker.getBasicPathNodeType(world, x, y - 1, z);
            if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || pathNodeType2 == PathNodeType.LAVA || block == Blocks.CAMPFIRE) {
                pathNodeType = PathNodeType.DAMAGE_FIRE;
            } else if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
                pathNodeType = PathNodeType.DAMAGE_CACTUS;
            } else if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
                pathNodeType = PathNodeType.DAMAGE_OTHER;
            } else if (pathNodeType2 == PathNodeType.COCOA) {
                pathNodeType = PathNodeType.COCOA;
            } else if (pathNodeType2 == PathNodeType.FENCE) {
                pathNodeType = PathNodeType.FENCE;
            } else {
                PathNodeType pathNodeType3 = pathNodeType = pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            }
        }
        if (pathNodeType == PathNodeType.WALKABLE || pathNodeType == PathNodeType.OPEN) {
            pathNodeType = BirdPathNodeMaker.method_59(world, x, y, z, pathNodeType);
        }
        return pathNodeType;
    }

    private PathNodeType method_10(MobEntity mobEntity, BlockPos blockPos) {
        return this.method_9(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private PathNodeType method_9(MobEntity mobEntity, int i, int j, int k) {
        return this.getNodeType(this.field_20622, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
    }
}

