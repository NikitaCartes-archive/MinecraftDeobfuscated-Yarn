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
            i = MathHelper.floor(this.entity.getBoundingBox().minY);
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);
            Block block = this.field_20622.getBlockState(mutable).getBlock();
            while (block == Blocks.WATER) {
                mutable.set(this.entity.x, (double)(++i), this.entity.z);
                block = this.field_20622.getBlockState(mutable).getBlock();
            }
        } else {
            i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
        }
        if (this.entity.getPathfindingPenalty(pathNodeType = this.method_9(this.entity, (blockPos = new BlockPos(this.entity)).getX(), i, blockPos.getZ())) < 0.0f) {
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));
            for (BlockPos blockPos2 : set) {
                PathNodeType pathNodeType2 = this.method_10(this.entity, blockPos2);
                if (!(this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0f)) continue;
                return super.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return super.getNode(blockPos.getX(), i, blockPos.getZ());
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(super.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
    }

    @Override
    public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
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
        int i = 0;
        PathNode pathNode2 = this.getNode(pathNode.x, pathNode.y, pathNode.z + 1);
        if (pathNode2 != null && !pathNode2.visited) {
            pathNodes[i++] = pathNode2;
        }
        if ((pathNode3 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z)) != null && !pathNode3.visited) {
            pathNodes[i++] = pathNode3;
        }
        if ((pathNode4 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z)) != null && !pathNode4.visited) {
            pathNodes[i++] = pathNode4;
        }
        if ((pathNode5 = this.getNode(pathNode.x, pathNode.y, pathNode.z - 1)) != null && !pathNode5.visited) {
            pathNodes[i++] = pathNode5;
        }
        if ((pathNode6 = this.getNode(pathNode.x, pathNode.y + 1, pathNode.z)) != null && !pathNode6.visited) {
            pathNodes[i++] = pathNode6;
        }
        if ((pathNode7 = this.getNode(pathNode.x, pathNode.y - 1, pathNode.z)) != null && !pathNode7.visited) {
            pathNodes[i++] = pathNode7;
        }
        if ((pathNode8 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z - 1)) != null && !pathNode8.visited && pathNode5 != null && pathNode5.penalty >= 0.0f && pathNode4 != null && pathNode4.penalty >= 0.0f) {
            pathNodes[i++] = pathNode8;
        }
        if ((pathNode9 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z + 1)) != null && !pathNode9.visited && pathNode2 != null && pathNode2.penalty >= 0.0f && pathNode4 != null && pathNode4.penalty >= 0.0f) {
            pathNodes[i++] = pathNode9;
        }
        if ((pathNode10 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z - 1)) != null && !pathNode10.visited && pathNode5 != null && pathNode5.penalty >= 0.0f && pathNode3 != null && pathNode3.penalty >= 0.0f) {
            pathNodes[i++] = pathNode10;
        }
        if ((pathNode11 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z + 1)) != null && !pathNode11.visited && pathNode2 != null && pathNode2.penalty >= 0.0f && pathNode3 != null && pathNode3.penalty >= 0.0f) {
            pathNodes[i++] = pathNode11;
        }
        if ((pathNode12 = this.getNode(pathNode.x + 1, pathNode.y + 1, pathNode.z - 1)) != null && !pathNode12.visited && pathNode8 != null && pathNode8.penalty >= 0.0f && pathNode6 != null && pathNode6.penalty >= 0.0f) {
            pathNodes[i++] = pathNode12;
        }
        if ((pathNode13 = this.getNode(pathNode.x + 1, pathNode.y + 1, pathNode.z + 1)) != null && !pathNode13.visited && pathNode9 != null && pathNode9.penalty >= 0.0f && pathNode6 != null && pathNode6.penalty >= 0.0f) {
            pathNodes[i++] = pathNode13;
        }
        if ((pathNode14 = this.getNode(pathNode.x - 1, pathNode.y + 1, pathNode.z - 1)) != null && !pathNode14.visited && pathNode10 != null && pathNode10.penalty >= 0.0f && pathNode6 != null && pathNode6.penalty >= 0.0f) {
            pathNodes[i++] = pathNode14;
        }
        if ((pathNode15 = this.getNode(pathNode.x - 1, pathNode.y + 1, pathNode.z + 1)) != null && !pathNode15.visited && pathNode11 != null && pathNode11.penalty >= 0.0f && pathNode6 != null && pathNode6.penalty >= 0.0f) {
            pathNodes[i++] = pathNode15;
        }
        if ((pathNode16 = this.getNode(pathNode.x + 1, pathNode.y - 1, pathNode.z - 1)) != null && !pathNode16.visited && pathNode8 != null && pathNode8.penalty >= 0.0f && pathNode7 != null && pathNode7.penalty >= 0.0f) {
            pathNodes[i++] = pathNode16;
        }
        if ((pathNode17 = this.getNode(pathNode.x + 1, pathNode.y - 1, pathNode.z + 1)) != null && !pathNode17.visited && pathNode9 != null && pathNode9.penalty >= 0.0f && pathNode7 != null && pathNode7.penalty >= 0.0f) {
            pathNodes[i++] = pathNode17;
        }
        if ((pathNode18 = this.getNode(pathNode.x - 1, pathNode.y - 1, pathNode.z - 1)) != null && !pathNode18.visited && pathNode10 != null && pathNode10.penalty >= 0.0f && pathNode7 != null && pathNode7.penalty >= 0.0f) {
            pathNodes[i++] = pathNode18;
        }
        if ((pathNode19 = this.getNode(pathNode.x - 1, pathNode.y - 1, pathNode.z + 1)) != null && !pathNode19.visited && pathNode11 != null && pathNode11.penalty >= 0.0f && pathNode7 != null && pathNode7.penalty >= 0.0f) {
            pathNodes[i++] = pathNode19;
        }
        return i;
    }

    @Override
    @Nullable
    protected PathNode getNode(int i, int j, int k) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.method_9(this.entity, i, j, k);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getNode(i, j, k);
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
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = new BlockPos(mobEntity);
        pathNodeType = this.getNodeType(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (mobEntity.getPathfindingPenalty(pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(mobEntity.getPathfindingPenalty(pathNodeType3) >= mobEntity.getPathfindingPenalty(pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && mobEntity.getPathfindingPenalty(pathNodeType2) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    @Override
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
        PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
        if (pathNodeType == PathNodeType.OPEN && j >= 1) {
            Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
            PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i, j - 1, k);
            pathNodeType = pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || pathNodeType2 == PathNodeType.LAVA || block == Blocks.CAMPFIRE ? PathNodeType.DAMAGE_FIRE : (pathNodeType2 == PathNodeType.DAMAGE_CACTUS ? PathNodeType.DAMAGE_CACTUS : (pathNodeType2 == PathNodeType.DAMAGE_OTHER ? PathNodeType.DAMAGE_OTHER : (pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER ? PathNodeType.OPEN : PathNodeType.WALKABLE)));
        }
        pathNodeType = this.method_59(blockView, i, j, k, pathNodeType);
        return pathNodeType;
    }

    private PathNodeType method_10(MobEntity mobEntity, BlockPos blockPos) {
        return this.method_9(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private PathNodeType method_9(MobEntity mobEntity, int i, int j, int k) {
        return this.getNodeType(this.field_20622, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
    }
}

