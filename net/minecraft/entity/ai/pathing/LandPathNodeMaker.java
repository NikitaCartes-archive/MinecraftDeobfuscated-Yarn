/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class LandPathNodeMaker
extends PathNodeMaker {
    protected float waterPathNodeTypeWeight;

    @Override
    public void init(CollisionView collisionView, MobEntity mobEntity) {
        super.init(collisionView, mobEntity);
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
        int i;
        if (this.canSwim() && this.entity.isTouchingWater()) {
            i = MathHelper.floor(this.entity.getBoundingBox().y1);
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);
            BlockState blockState = this.blockView.getBlockState(mutable);
            while (blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getStill(false)) {
                mutable.set(this.entity.x, (double)(++i), this.entity.z);
                blockState = this.blockView.getBlockState(mutable);
            }
            --i;
        } else if (this.entity.onGround) {
            i = MathHelper.floor(this.entity.getBoundingBox().y1 + 0.5);
        } else {
            blockPos = new BlockPos(this.entity);
            while ((this.blockView.getBlockState(blockPos).isAir() || this.blockView.getBlockState(blockPos).canPlaceAtSide(this.blockView, blockPos, BlockPlacementEnvironment.LAND)) && blockPos.getY() > 0) {
                blockPos = blockPos.down();
            }
            i = blockPos.up().getY();
        }
        blockPos = new BlockPos(this.entity);
        PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
        if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0f) {
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z1));
            set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z2));
            set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z1));
            set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z2));
            for (BlockPos blockPos2 : set) {
                PathNodeType pathNodeType2 = this.getNodeType(this.entity, blockPos2);
                if (!(this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0f)) continue;
                return this.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return this.getNode(blockPos.getX(), i, blockPos.getZ());
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(this.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
    }

    @Override
    public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
        PathNode pathNode9;
        PathNode pathNode8;
        PathNode pathNode7;
        PathNode pathNode6;
        PathNode pathNode5;
        PathNode pathNode4;
        PathNode pathNode3;
        double d;
        PathNode pathNode2;
        int i = 0;
        int j = 0;
        PathNodeType pathNodeType = this.getNodeType(this.entity, pathNode.x, pathNode.y + 1, pathNode.z);
        if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f) {
            j = MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        if ((pathNode2 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z + 1, j, d = LandPathNodeMaker.method_60(this.blockView, new BlockPos(pathNode.x, pathNode.y, pathNode.z)), Direction.SOUTH)) != null && !pathNode2.field_42 && pathNode2.field_43 >= 0.0f) {
            pathNodes[i++] = pathNode2;
        }
        if ((pathNode3 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z, j, d, Direction.WEST)) != null && !pathNode3.field_42 && pathNode3.field_43 >= 0.0f) {
            pathNodes[i++] = pathNode3;
        }
        if ((pathNode4 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z, j, d, Direction.EAST)) != null && !pathNode4.field_42 && pathNode4.field_43 >= 0.0f) {
            pathNodes[i++] = pathNode4;
        }
        if ((pathNode5 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH)) != null && !pathNode5.field_42 && pathNode5.field_43 >= 0.0f) {
            pathNodes[i++] = pathNode5;
        }
        if (this.method_20536(pathNode, pathNode3, pathNode5, pathNode6 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH))) {
            pathNodes[i++] = pathNode6;
        }
        if (this.method_20536(pathNode, pathNode4, pathNode5, pathNode7 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH))) {
            pathNodes[i++] = pathNode7;
        }
        if (this.method_20536(pathNode, pathNode3, pathNode2, pathNode8 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH))) {
            pathNodes[i++] = pathNode8;
        }
        if (this.method_20536(pathNode, pathNode4, pathNode2, pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH))) {
            pathNodes[i++] = pathNode9;
        }
        return i;
    }

    private boolean method_20536(PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3, @Nullable PathNode pathNode4) {
        if (pathNode4 == null || pathNode3 == null || pathNode2 == null) {
            return false;
        }
        if (pathNode4.field_42) {
            return false;
        }
        if (pathNode3.y > pathNode.y || pathNode2.y > pathNode.y) {
            return false;
        }
        return pathNode4.field_43 >= 0.0f && (pathNode3.y < pathNode.y || pathNode3.field_43 >= 0.0f) && (pathNode2.y < pathNode.y || pathNode2.field_43 >= 0.0f);
    }

    public static double method_60(BlockView blockView, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.down();
        VoxelShape voxelShape = blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2);
        return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
    }

    @Nullable
    private PathNode getPathNode(int i, int j, int k, int l, double d, Direction direction) {
        double m;
        double h;
        Box box;
        PathNode pathNode = null;
        BlockPos blockPos = new BlockPos(i, j, k);
        double e = LandPathNodeMaker.method_60(this.blockView, blockPos);
        if (e - d > 1.125) {
            return null;
        }
        PathNodeType pathNodeType = this.getNodeType(this.entity, i, j, k);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        double g = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            pathNode = this.getNode(i, j, k);
            pathNode.type = pathNodeType;
            pathNode.field_43 = Math.max(pathNode.field_43, f);
        }
        if (pathNodeType == PathNodeType.WALKABLE) {
            return pathNode;
        }
        if (!(pathNode != null && !(pathNode.field_43 < 0.0f) || l <= 0 || pathNodeType == PathNodeType.FENCE || pathNodeType == PathNodeType.TRAPDOOR || (pathNode = this.getPathNode(i, j + 1, k, l - 1, d, direction)) == null || pathNode.type != PathNodeType.OPEN && pathNode.type != PathNodeType.WALKABLE || !(this.entity.getWidth() < 1.0f) || this.blockView.doesNotCollide(this.entity, box = new Box((h = (double)(i - direction.getOffsetX()) + 0.5) - g, LandPathNodeMaker.method_60(this.blockView, new BlockPos(h, (double)(j + 1), m = (double)(k - direction.getOffsetZ()) + 0.5)) + 0.001, m - g, h + g, (double)this.entity.getHeight() + LandPathNodeMaker.method_60(this.blockView, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002, m + g)))) {
            pathNode = null;
        }
        if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
            if (this.getNodeType(this.entity, i, j - 1, k) != PathNodeType.WATER) {
                return pathNode;
            }
            while (j > 0) {
                if ((pathNodeType = this.getNodeType(this.entity, i, --j, k)) == PathNodeType.WATER) {
                    pathNode = this.getNode(i, j, k);
                    pathNode.type = pathNodeType;
                    pathNode.field_43 = Math.max(pathNode.field_43, this.entity.getPathfindingPenalty(pathNodeType));
                    continue;
                }
                return pathNode;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            PathNodeType pathNodeType2;
            Box box2 = new Box((double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (float)j + this.entity.getHeight(), (double)k + g + 0.5);
            if (!this.blockView.doesNotCollide(this.entity, box2)) {
                return null;
            }
            if (this.entity.getWidth() >= 1.0f && (pathNodeType2 = this.getNodeType(this.entity, i, j - 1, k)) == PathNodeType.BLOCKED) {
                pathNode = this.getNode(i, j, k);
                pathNode.type = PathNodeType.WALKABLE;
                pathNode.field_43 = Math.max(pathNode.field_43, f);
                return pathNode;
            }
            int n = 0;
            int o = j;
            while (pathNodeType == PathNodeType.OPEN) {
                PathNode pathNode2;
                if (--j < 0) {
                    pathNode2 = this.getNode(i, o, k);
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.field_43 = -1.0f;
                    return pathNode2;
                }
                pathNode2 = this.getNode(i, j, k);
                if (n++ >= this.entity.getSafeFallDistance()) {
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.field_43 = -1.0f;
                    return pathNode2;
                }
                pathNodeType = this.getNodeType(this.entity, i, j, k);
                f = this.entity.getPathfindingPenalty(pathNodeType);
                if (pathNodeType != PathNodeType.OPEN && f >= 0.0f) {
                    pathNode = pathNode2;
                    pathNode.type = pathNodeType;
                    pathNode.field_43 = Math.max(pathNode.field_43, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                pathNode2.type = PathNodeType.BLOCKED;
                pathNode2.field_43 = -1.0f;
                return pathNode2;
            }
        }
        return pathNode;
    }

    @Override
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        double d = (double)mobEntity.getWidth() / 2.0;
        BlockPos blockPos = new BlockPos(mobEntity);
        pathNodeType = this.method_64(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
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

    public PathNodeType method_64(BlockView blockView, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, EnumSet<PathNodeType> enumSet, PathNodeType pathNodeType, BlockPos blockPos) {
        for (int o = 0; o < l; ++o) {
            for (int p = 0; p < m; ++p) {
                for (int q = 0; q < n; ++q) {
                    int r = o + i;
                    int s = p + j;
                    int t = q + k;
                    PathNodeType pathNodeType2 = this.getNodeType(blockView, r, s, t);
                    pathNodeType2 = this.method_61(blockView, bl, bl2, blockPos, pathNodeType2);
                    if (o == 0 && p == 0 && q == 0) {
                        pathNodeType = pathNodeType2;
                    }
                    enumSet.add(pathNodeType2);
                }
            }
        }
        return pathNodeType;
    }

    protected PathNodeType method_61(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
        if (pathNodeType == PathNodeType.DOOR_WOOD_CLOSED && bl && bl2) {
            pathNodeType = PathNodeType.WALKABLE;
        }
        if (pathNodeType == PathNodeType.DOOR_OPEN && !bl2) {
            pathNodeType = PathNodeType.BLOCKED;
        }
        if (pathNodeType == PathNodeType.RAIL && !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock) && !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
            pathNodeType = PathNodeType.FENCE;
        }
        if (pathNodeType == PathNodeType.LEAVES) {
            pathNodeType = PathNodeType.BLOCKED;
        }
        return pathNodeType;
    }

    private PathNodeType getNodeType(MobEntity mobEntity, BlockPos blockPos) {
        return this.getNodeType(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private PathNodeType getNodeType(MobEntity mobEntity, int i, int j, int k) {
        return this.getNodeType(this.blockView, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
    }

    @Override
    public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
        PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
        if (pathNodeType == PathNodeType.OPEN && j >= 1) {
            Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
            PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i, j - 1, k);
            PathNodeType pathNodeType3 = pathNodeType = pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER || pathNodeType2 == PathNodeType.LAVA ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
                pathNodeType = PathNodeType.DAMAGE_FIRE;
            }
            if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
                pathNodeType = PathNodeType.DAMAGE_CACTUS;
            }
            if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
                pathNodeType = PathNodeType.DAMAGE_OTHER;
            }
        }
        pathNodeType = this.method_59(blockView, i, j, k, pathNodeType);
        return pathNodeType;
    }

    public PathNodeType method_59(BlockView blockView, int i, int j, int k, PathNodeType pathNodeType) {
        if (pathNodeType == PathNodeType.WALKABLE) {
            try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
                for (int l = -1; l <= 1; ++l) {
                    for (int m = -1; m <= 1; ++m) {
                        if (l == 0 && m == 0) continue;
                        Block block = blockView.getBlockState(pooledMutable.set(l + i, j, m + k)).getBlock();
                        if (block == Blocks.CACTUS) {
                            pathNodeType = PathNodeType.DANGER_CACTUS;
                            continue;
                        }
                        if (block == Blocks.FIRE) {
                            pathNodeType = PathNodeType.DANGER_FIRE;
                            continue;
                        }
                        if (block != Blocks.SWEET_BERRY_BUSH) continue;
                        pathNodeType = PathNodeType.DANGER_OTHER;
                    }
                }
            }
        }
        return pathNodeType;
    }

    protected PathNodeType getBasicPathNodeType(BlockView blockView, int i, int j, int k) {
        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = blockView.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        if (blockState.isAir()) {
            return PathNodeType.OPEN;
        }
        if (block.matches(BlockTags.TRAPDOORS) || block == Blocks.LILY_PAD) {
            return PathNodeType.TRAPDOOR;
        }
        if (block == Blocks.FIRE) {
            return PathNodeType.DAMAGE_FIRE;
        }
        if (block == Blocks.CACTUS) {
            return PathNodeType.DAMAGE_CACTUS;
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return PathNodeType.DAMAGE_OTHER;
        }
        if (block instanceof DoorBlock && material == Material.WOOD && !blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_WOOD_CLOSED;
        }
        if (block instanceof DoorBlock && material == Material.METAL && !blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_IRON_CLOSED;
        }
        if (block instanceof DoorBlock && blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_OPEN;
        }
        if (block instanceof AbstractRailBlock) {
            return PathNodeType.RAIL;
        }
        if (block instanceof LeavesBlock) {
            return PathNodeType.LEAVES;
        }
        if (block.matches(BlockTags.FENCES) || block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock && !blockState.get(FenceGateBlock.OPEN).booleanValue()) {
            return PathNodeType.FENCE;
        }
        FluidState fluidState = blockView.getFluidState(blockPos);
        if (fluidState.matches(FluidTags.WATER)) {
            return PathNodeType.WATER;
        }
        if (fluidState.matches(FluidTags.LAVA)) {
            return PathNodeType.LAVA;
        }
        if (blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.LAND)) {
            return PathNodeType.OPEN;
        }
        return PathNodeType.BLOCKED;
    }
}

