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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BirdPathNodeMaker
extends LandPathNodeMaker {
    @Override
    public void init(BlockView blockView, MobEntity mobEntity) {
        super.init(blockView, mobEntity);
        this.waterPathNodeTypeWeight = mobEntity.getPathNodeTypeWeight(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathNodeTypeWeight(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos blockPos;
        PathNodeType pathNodeType;
        int i;
        if (this.canSwim() && this.entity.isInsideWater()) {
            i = (int)this.entity.getBoundingBox().minY;
            BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.entity.x), i, MathHelper.floor(this.entity.z));
            Block block = this.blockView.getBlockState(mutable).getBlock();
            while (block == Blocks.WATER) {
                mutable.set(MathHelper.floor(this.entity.x), ++i, MathHelper.floor(this.entity.z));
                block = this.blockView.getBlockState(mutable).getBlock();
            }
        } else {
            i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
        }
        if (this.entity.getPathNodeTypeWeight(pathNodeType = this.method_9(this.entity, (blockPos = new BlockPos(this.entity)).getX(), i, blockPos.getZ())) < 0.0f) {
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));
            for (BlockPos blockPos2 : set) {
                PathNodeType pathNodeType2 = this.method_10(this.entity, blockPos2);
                if (!(this.entity.getPathNodeTypeWeight(pathNodeType2) >= 0.0f)) continue;
                return super.getPathNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return super.getPathNode(blockPos.getX(), i, blockPos.getZ());
    }

    @Override
    public PathNode getPathNode(double d, double e, double f) {
        return super.getPathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
    }

    @Override
    public int getPathNodes(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
        PathNode pathNode9;
        boolean bl6;
        int i = 0;
        PathNode pathNode3 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z + 1);
        PathNode pathNode4 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z);
        PathNode pathNode5 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z);
        PathNode pathNode6 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z - 1);
        PathNode pathNode7 = this.getPathNode(pathNode.x, pathNode.y + 1, pathNode.z);
        PathNode pathNode8 = this.getPathNode(pathNode.x, pathNode.y - 1, pathNode.z);
        if (pathNode3 != null && !pathNode3.field_42 && pathNode3.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode3;
        }
        if (pathNode4 != null && !pathNode4.field_42 && pathNode4.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode4;
        }
        if (pathNode5 != null && !pathNode5.field_42 && pathNode5.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode5;
        }
        if (pathNode6 != null && !pathNode6.field_42 && pathNode6.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode6;
        }
        if (pathNode7 != null && !pathNode7.field_42 && pathNode7.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode7;
        }
        if (pathNode8 != null && !pathNode8.field_42 && pathNode8.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode8;
        }
        boolean bl = pathNode6 == null || pathNode6.field_43 != 0.0f;
        boolean bl2 = pathNode3 == null || pathNode3.field_43 != 0.0f;
        boolean bl3 = pathNode5 == null || pathNode5.field_43 != 0.0f;
        boolean bl4 = pathNode4 == null || pathNode4.field_43 != 0.0f;
        boolean bl5 = pathNode7 == null || pathNode7.field_43 != 0.0f;
        boolean bl7 = bl6 = pathNode8 == null || pathNode8.field_43 != 0.0f;
        if (bl && bl4 && (pathNode9 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z - 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl && bl3 && (pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z - 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl2 && bl4 && (pathNode9 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z + 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl2 && bl3 && (pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z + 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl && bl5 && (pathNode9 = this.getPathNode(pathNode.x, pathNode.y + 1, pathNode.z - 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl2 && bl5 && (pathNode9 = this.getPathNode(pathNode.x, pathNode.y + 1, pathNode.z + 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl3 && bl5 && (pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y + 1, pathNode.z)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl4 && bl5 && (pathNode9 = this.getPathNode(pathNode.x - 1, pathNode.y + 1, pathNode.z)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl && bl6 && (pathNode9 = this.getPathNode(pathNode.x, pathNode.y - 1, pathNode.z - 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl2 && bl6 && (pathNode9 = this.getPathNode(pathNode.x, pathNode.y - 1, pathNode.z + 1)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl3 && bl6 && (pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y - 1, pathNode.z)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        if (bl4 && bl6 && (pathNode9 = this.getPathNode(pathNode.x - 1, pathNode.y - 1, pathNode.z)) != null && !pathNode9.field_42 && pathNode9.distance(pathNode2) < f) {
            pathNodes[i++] = pathNode9;
        }
        return i;
    }

    @Override
    @Nullable
    protected PathNode getPathNode(int i, int j, int k) {
        PathNode pathNode = null;
        PathNodeType pathNodeType = this.method_9(this.entity, i, j, k);
        float f = this.entity.getPathNodeTypeWeight(pathNodeType);
        if (f >= 0.0f) {
            pathNode = super.getPathNode(i, j, k);
            pathNode.type = pathNodeType;
            pathNode.field_43 = Math.max(pathNode.field_43, f);
            if (pathNodeType == PathNodeType.WALKABLE) {
                pathNode.field_43 += 1.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN || pathNodeType == PathNodeType.WALKABLE) {
            return pathNode;
        }
        return pathNode;
    }

    @Override
    public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = new BlockPos(mobEntity);
        pathNodeType = this.method_64(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (mobEntity.getPathNodeTypeWeight(pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(mobEntity.getPathNodeTypeWeight(pathNodeType3) >= mobEntity.getPathNodeTypeWeight(pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && mobEntity.getPathNodeTypeWeight(pathNodeType2) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    @Override
    public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
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
        return this.getPathNodeType(this.blockView, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canPathThroughDoors(), this.canEnterOpenDoors());
    }
}

