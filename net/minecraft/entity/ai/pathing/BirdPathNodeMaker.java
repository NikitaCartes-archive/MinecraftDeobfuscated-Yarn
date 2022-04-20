/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import net.minecraft.block.BlockState;
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
    private final Long2ObjectMap<PathNodeType> pathNodes = new Long2ObjectOpenHashMap<PathNodeType>();

    @Override
    public void init(ChunkCache cachedWorld, MobEntity entity) {
        super.init(cachedWorld, entity);
        this.pathNodes.clear();
        this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        this.pathNodes.clear();
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos blockPos;
        PathNodeType pathNodeType;
        int i;
        if (this.canSwim() && this.entity.isTouchingWater()) {
            i = this.entity.getBlockY();
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());
            BlockState blockState = this.cachedWorld.getBlockState(mutable);
            while (blockState.isOf(Blocks.WATER)) {
                mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
                blockState = this.cachedWorld.getBlockState(mutable);
            }
        } else {
            i = MathHelper.floor(this.entity.getY() + 0.5);
        }
        if (this.entity.getPathfindingPenalty(pathNodeType = this.getNodeType((blockPos = this.entity.getBlockPos()).getX(), i, blockPos.getZ())) < 0.0f) {
            ImmutableSet<BlockPos> set = ImmutableSet.of(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ), new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ), new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ), new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));
            for (BlockPos blockPos2 : set) {
                PathNodeType pathNodeType2 = this.getNodeType(blockPos.getX(), i, blockPos.getZ());
                if (!(this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0f)) continue;
                return super.method_43415(blockPos2);
            }
        }
        return super.method_43415(new BlockPos(blockPos.getX(), i, blockPos.getZ()));
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
        if (this.unvisited(pathNode)) {
            successors[i++] = pathNode;
        }
        if (this.unvisited(pathNode2 = this.getNode(node.x - 1, node.y, node.z))) {
            successors[i++] = pathNode2;
        }
        if (this.unvisited(pathNode3 = this.getNode(node.x + 1, node.y, node.z))) {
            successors[i++] = pathNode3;
        }
        if (this.unvisited(pathNode4 = this.getNode(node.x, node.y, node.z - 1))) {
            successors[i++] = pathNode4;
        }
        if (this.unvisited(pathNode5 = this.getNode(node.x, node.y + 1, node.z))) {
            successors[i++] = pathNode5;
        }
        if (this.unvisited(pathNode6 = this.getNode(node.x, node.y - 1, node.z))) {
            successors[i++] = pathNode6;
        }
        if (this.unvisited(pathNode7 = this.getNode(node.x, node.y + 1, node.z + 1)) && this.isPassable(pathNode) && this.isPassable(pathNode5)) {
            successors[i++] = pathNode7;
        }
        if (this.unvisited(pathNode8 = this.getNode(node.x - 1, node.y + 1, node.z)) && this.isPassable(pathNode2) && this.isPassable(pathNode5)) {
            successors[i++] = pathNode8;
        }
        if (this.unvisited(pathNode9 = this.getNode(node.x + 1, node.y + 1, node.z)) && this.isPassable(pathNode3) && this.isPassable(pathNode5)) {
            successors[i++] = pathNode9;
        }
        if (this.unvisited(pathNode10 = this.getNode(node.x, node.y + 1, node.z - 1)) && this.isPassable(pathNode4) && this.isPassable(pathNode5)) {
            successors[i++] = pathNode10;
        }
        if (this.unvisited(pathNode11 = this.getNode(node.x, node.y - 1, node.z + 1)) && this.isPassable(pathNode) && this.isPassable(pathNode6)) {
            successors[i++] = pathNode11;
        }
        if (this.unvisited(pathNode12 = this.getNode(node.x - 1, node.y - 1, node.z)) && this.isPassable(pathNode2) && this.isPassable(pathNode6)) {
            successors[i++] = pathNode12;
        }
        if (this.unvisited(pathNode13 = this.getNode(node.x + 1, node.y - 1, node.z)) && this.isPassable(pathNode3) && this.isPassable(pathNode6)) {
            successors[i++] = pathNode13;
        }
        if (this.unvisited(pathNode14 = this.getNode(node.x, node.y - 1, node.z - 1)) && this.isPassable(pathNode4) && this.isPassable(pathNode6)) {
            successors[i++] = pathNode14;
        }
        if (this.unvisited(pathNode15 = this.getNode(node.x + 1, node.y, node.z - 1)) && this.isPassable(pathNode4) && this.isPassable(pathNode3)) {
            successors[i++] = pathNode15;
        }
        if (this.unvisited(pathNode16 = this.getNode(node.x + 1, node.y, node.z + 1)) && this.isPassable(pathNode) && this.isPassable(pathNode3)) {
            successors[i++] = pathNode16;
        }
        if (this.unvisited(pathNode17 = this.getNode(node.x - 1, node.y, node.z - 1)) && this.isPassable(pathNode4) && this.isPassable(pathNode2)) {
            successors[i++] = pathNode17;
        }
        if (this.unvisited(pathNode18 = this.getNode(node.x - 1, node.y, node.z + 1)) && this.isPassable(pathNode) && this.isPassable(pathNode2)) {
            successors[i++] = pathNode18;
        }
        if (this.unvisited(pathNode19 = this.getNode(node.x + 1, node.y + 1, node.z - 1)) && this.isPassable(pathNode15) && this.isPassable(pathNode4) && this.isPassable(pathNode3) && this.isPassable(pathNode5) && this.isPassable(pathNode10) && this.isPassable(pathNode9)) {
            successors[i++] = pathNode19;
        }
        if (this.unvisited(pathNode20 = this.getNode(node.x + 1, node.y + 1, node.z + 1)) && this.isPassable(pathNode16) && this.isPassable(pathNode) && this.isPassable(pathNode3) && this.isPassable(pathNode5) && this.isPassable(pathNode7) && this.isPassable(pathNode9)) {
            successors[i++] = pathNode20;
        }
        if (this.unvisited(pathNode21 = this.getNode(node.x - 1, node.y + 1, node.z - 1)) && this.isPassable(pathNode17) && this.isPassable(pathNode4) && this.isPassable(pathNode2) && this.isPassable(pathNode5) && this.isPassable(pathNode10) && this.isPassable(pathNode8)) {
            successors[i++] = pathNode21;
        }
        if (this.unvisited(pathNode22 = this.getNode(node.x - 1, node.y + 1, node.z + 1)) && this.isPassable(pathNode18) && this.isPassable(pathNode) && this.isPassable(pathNode2) && this.isPassable(pathNode5) && this.isPassable(pathNode7) && this.isPassable(pathNode8)) {
            successors[i++] = pathNode22;
        }
        if (this.unvisited(pathNode23 = this.getNode(node.x + 1, node.y - 1, node.z - 1)) && this.isPassable(pathNode15) && this.isPassable(pathNode4) && this.isPassable(pathNode3) && this.isPassable(pathNode6) && this.isPassable(pathNode14) && this.isPassable(pathNode13)) {
            successors[i++] = pathNode23;
        }
        if (this.unvisited(pathNode24 = this.getNode(node.x + 1, node.y - 1, node.z + 1)) && this.isPassable(pathNode16) && this.isPassable(pathNode) && this.isPassable(pathNode3) && this.isPassable(pathNode6) && this.isPassable(pathNode11) && this.isPassable(pathNode13)) {
            successors[i++] = pathNode24;
        }
        if (this.unvisited(pathNode25 = this.getNode(node.x - 1, node.y - 1, node.z - 1)) && this.isPassable(pathNode17) && this.isPassable(pathNode4) && this.isPassable(pathNode2) && this.isPassable(pathNode6) && this.isPassable(pathNode14) && this.isPassable(pathNode12)) {
            successors[i++] = pathNode25;
        }
        if (this.unvisited(pathNode26 = this.getNode(node.x - 1, node.y - 1, node.z + 1)) && this.isPassable(pathNode18) && this.isPassable(pathNode) && this.isPassable(pathNode2) && this.isPassable(pathNode6) && this.isPassable(pathNode11) && this.isPassable(pathNode12)) {
            successors[i++] = pathNode26;
        }
        return i;
    }

    private boolean isPassable(@Nullable PathNode node) {
        return node != null && node.penalty >= 0.0f;
    }

    private boolean unvisited(@Nullable PathNode node) {
        return node != null && !node.visited;
    }

    @Override
    @Nullable
    protected PathNode getNode(int x, int y, int z) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.getNodeType(x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (pathNodeType == PathNodeType.WALKABLE) {
                pathNode.penalty += 1.0f;
            }
        }
        return pathNode;
    }

    private PathNodeType getNodeType(int x, int y, int z) {
        return this.pathNodes.computeIfAbsent(BlockPos.asLong(x, y, z), l -> this.getNodeType(this.cachedWorld, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors()));
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = mob.getBlockPos();
        pathNodeType = super.findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
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
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        PathNodeType pathNodeType = BirdPathNodeMaker.getCommonNodeType(world, mutable.set(x, y, z));
        if (pathNodeType == PathNodeType.OPEN && y >= world.getBottomY() + 1) {
            PathNodeType pathNodeType2 = BirdPathNodeMaker.getCommonNodeType(world, mutable.set(x, y - 1, z));
            if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || pathNodeType2 == PathNodeType.LAVA) {
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
            pathNodeType = BirdPathNodeMaker.getNodeTypeFromNeighbors(world, mutable.set(x, y, z), pathNodeType);
        }
        return pathNodeType;
    }
}

