/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.NavigationType;
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
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class LandPathNodeMaker
extends PathNodeMaker {
    protected float waterPathNodeTypeWeight;

    @Override
    public void init(ChunkCache cachedWorld, MobEntity entity) {
        super.init(cachedWorld, entity);
        this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
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
            i = MathHelper.floor(this.entity.getY());
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());
            BlockState blockState = this.cachedWorld.getBlockState(mutable);
            while (blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getStill(false)) {
                mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
                blockState = this.cachedWorld.getBlockState(mutable);
            }
            --i;
        } else if (this.entity.isOnGround()) {
            i = MathHelper.floor(this.entity.getY() + 0.5);
        } else {
            blockPos = this.entity.getSenseCenterPos();
            while ((this.cachedWorld.getBlockState(blockPos).isAir() || this.cachedWorld.getBlockState(blockPos).canPathfindThrough(this.cachedWorld, blockPos, NavigationType.LAND)) && blockPos.getY() > 0) {
                blockPos = blockPos.down();
            }
            i = blockPos.up().getY();
        }
        blockPos = this.entity.getSenseCenterPos();
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
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        PathNode pathNode8;
        PathNode pathNode7;
        PathNode pathNode6;
        PathNode pathNode5;
        PathNode pathNode4;
        PathNode pathNode3;
        PathNode pathNode2;
        double d;
        PathNode pathNode;
        int i = 0;
        int j = 0;
        PathNodeType pathNodeType = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
        if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f) {
            PathNodeType pathNodeType2 = this.getNodeType(this.entity, node.x, node.y, node.z);
            j = pathNodeType2 == PathNodeType.STICKY_HONEY ? 0 : MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        if ((pathNode = this.getPathNode(node.x, node.y, node.z + 1, j, d = LandPathNodeMaker.getFeetY(this.cachedWorld, new BlockPos(node.x, node.y, node.z)), Direction.SOUTH)) != null && !pathNode.visited && pathNode.penalty >= 0.0f) {
            successors[i++] = pathNode;
        }
        if ((pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, j, d, Direction.WEST)) != null && !pathNode2.visited && pathNode2.penalty >= 0.0f) {
            successors[i++] = pathNode2;
        }
        if ((pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, j, d, Direction.EAST)) != null && !pathNode3.visited && pathNode3.penalty >= 0.0f) {
            successors[i++] = pathNode3;
        }
        if ((pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, j, d, Direction.NORTH)) != null && !pathNode4.visited && pathNode4.penalty >= 0.0f) {
            successors[i++] = pathNode4;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode4, pathNode5 = this.getPathNode(node.x - 1, node.y, node.z - 1, j, d, Direction.NORTH))) {
            successors[i++] = pathNode5;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode4, pathNode6 = this.getPathNode(node.x + 1, node.y, node.z - 1, j, d, Direction.NORTH))) {
            successors[i++] = pathNode6;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode, pathNode7 = this.getPathNode(node.x - 1, node.y, node.z + 1, j, d, Direction.SOUTH))) {
            successors[i++] = pathNode7;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode, pathNode8 = this.getPathNode(node.x + 1, node.y, node.z + 1, j, d, Direction.SOUTH))) {
            successors[i++] = pathNode8;
        }
        return i;
    }

    private boolean isValidDiagonalSuccessor(PathNode node, @Nullable PathNode successor1, @Nullable PathNode successor2, @Nullable PathNode diagonalSuccessor) {
        if (diagonalSuccessor == null || successor2 == null || successor1 == null) {
            return false;
        }
        if (diagonalSuccessor.visited) {
            return false;
        }
        if (successor2.y > node.y || successor1.y > node.y) {
            return false;
        }
        return diagonalSuccessor.penalty >= 0.0f && (successor2.y < node.y || successor2.penalty >= 0.0f) && (successor1.y < node.y || successor1.penalty >= 0.0f);
    }

    public static double getFeetY(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        VoxelShape voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
        return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
    }

    @Nullable
    private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY, Direction direction) {
        double h;
        double g;
        Box box;
        PathNode pathNode = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        double d = LandPathNodeMaker.getFeetY(this.cachedWorld, blockPos);
        if (d - prevFeetY > 1.125) {
            return null;
        }
        PathNodeType pathNodeType = this.getNodeType(this.entity, x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        double e = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            pathNode = this.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
        }
        if (pathNodeType == PathNodeType.WALKABLE) {
            return pathNode;
        }
        if (!(pathNode != null && !(pathNode.penalty < 0.0f) || maxYStep <= 0 || pathNodeType == PathNodeType.FENCE || pathNodeType == PathNodeType.TRAPDOOR || (pathNode = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY, direction)) == null || pathNode.type != PathNodeType.OPEN && pathNode.type != PathNodeType.WALKABLE || !(this.entity.getWidth() < 1.0f) || this.cachedWorld.doesNotCollide(this.entity, box = new Box((g = (double)(x - direction.getOffsetX()) + 0.5) - e, LandPathNodeMaker.getFeetY(this.cachedWorld, new BlockPos(g, (double)(y + 1), h = (double)(z - direction.getOffsetZ()) + 0.5)) + 0.001, h - e, g + e, (double)this.entity.getHeight() + LandPathNodeMaker.getFeetY(this.cachedWorld, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002, h + e)))) {
            pathNode = null;
        }
        if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
            if (this.getNodeType(this.entity, x, y - 1, z) != PathNodeType.WATER) {
                return pathNode;
            }
            while (y > 0) {
                if ((pathNodeType = this.getNodeType(this.entity, x, --y, z)) == PathNodeType.WATER) {
                    pathNode = this.getNode(x, y, z);
                    pathNode.type = pathNodeType;
                    pathNode.penalty = Math.max(pathNode.penalty, this.entity.getPathfindingPenalty(pathNodeType));
                    continue;
                }
                return pathNode;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            PathNodeType pathNodeType2;
            Box box2 = new Box((double)x - e + 0.5, (double)y + 0.001, (double)z - e + 0.5, (double)x + e + 0.5, (float)y + this.entity.getHeight(), (double)z + e + 0.5);
            if (!this.cachedWorld.doesNotCollide(this.entity, box2)) {
                return null;
            }
            if (this.entity.getWidth() >= 1.0f && (pathNodeType2 = this.getNodeType(this.entity, x, y - 1, z)) == PathNodeType.BLOCKED) {
                pathNode = this.getNode(x, y, z);
                pathNode.type = PathNodeType.WALKABLE;
                pathNode.penalty = Math.max(pathNode.penalty, f);
                return pathNode;
            }
            int i = 0;
            int j = y;
            while (pathNodeType == PathNodeType.OPEN) {
                PathNode pathNode2;
                if (--y < 0) {
                    pathNode2 = this.getNode(x, j, z);
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.penalty = -1.0f;
                    return pathNode2;
                }
                pathNode2 = this.getNode(x, y, z);
                if (i++ >= this.entity.getSafeFallDistance()) {
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.penalty = -1.0f;
                    return pathNode2;
                }
                pathNodeType = this.getNodeType(this.entity, x, y, z);
                f = this.entity.getPathfindingPenalty(pathNodeType);
                if (pathNodeType != PathNodeType.OPEN && f >= 0.0f) {
                    pathNode = pathNode2;
                    pathNode.type = pathNodeType;
                    pathNode.penalty = Math.max(pathNode.penalty, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                pathNode2.type = PathNodeType.BLOCKED;
                pathNode2.penalty = -1.0f;
                return pathNode2;
            }
        }
        return pathNode;
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        double d = (double)mob.getWidth() / 2.0;
        BlockPos blockPos = mob.getSenseCenterPos();
        pathNodeType = this.findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
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

    /**
     * Adds the node types in the box with the given size to the input EnumSet.
     * @return The node type at the least coordinates of the input box.
     */
    public PathNodeType findNearbyNodeTypes(BlockView world, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors, EnumSet<PathNodeType> nearbyTypes, PathNodeType type, BlockPos pos) {
        for (int i = 0; i < sizeX; ++i) {
            for (int j = 0; j < sizeY; ++j) {
                for (int k = 0; k < sizeZ; ++k) {
                    int l = i + x;
                    int m = j + y;
                    int n = k + z;
                    PathNodeType pathNodeType = this.getDefaultNodeType(world, l, m, n);
                    pathNodeType = this.adjustNodeType(world, canOpenDoors, canEnterOpenDoors, pos, pathNodeType);
                    if (i == 0 && j == 0 && k == 0) {
                        type = pathNodeType;
                    }
                    nearbyTypes.add(pathNodeType);
                }
            }
        }
        return type;
    }

    protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
        if (type == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoors && canEnterOpenDoors) {
            type = PathNodeType.WALKABLE;
        }
        if (type == PathNodeType.DOOR_OPEN && !canEnterOpenDoors) {
            type = PathNodeType.BLOCKED;
        }
        if (type == PathNodeType.RAIL && !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock) && !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
            type = PathNodeType.FENCE;
        }
        if (type == PathNodeType.LEAVES) {
            type = PathNodeType.BLOCKED;
        }
        return type;
    }

    private PathNodeType getNodeType(MobEntity entity, BlockPos pos) {
        return this.getNodeType(entity, pos.getX(), pos.getY(), pos.getZ());
    }

    private PathNodeType getNodeType(MobEntity entity, int x, int y, int z) {
        return this.getNodeType(this.cachedWorld, x, y, z, entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors());
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        return LandPathNodeMaker.getLandNodeType(world, x, y, z);
    }

    public static PathNodeType getLandNodeType(BlockView world, int x, int y, int z) {
        PathNodeType pathNodeType = LandPathNodeMaker.getCommonNodeType(world, x, y, z);
        if (pathNodeType == PathNodeType.OPEN && y >= 1) {
            Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathNodeType2 = LandPathNodeMaker.getCommonNodeType(world, x, y - 1, z);
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
            if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
                pathNodeType = PathNodeType.STICKY_HONEY;
            }
        }
        if (pathNodeType == PathNodeType.WALKABLE) {
            pathNodeType = LandPathNodeMaker.getNodeTypeFromNeighbors(world, x, y, z, pathNodeType);
        }
        return pathNodeType;
    }

    public static PathNodeType getNodeTypeFromNeighbors(BlockView world, int x, int y, int z, PathNodeType type) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    if (i == 0 && k == 0) continue;
                    Block block = world.getBlockState(mutable.set(i + x, j + y, k + z)).getBlock();
                    if (block == Blocks.CACTUS) {
                        type = PathNodeType.DANGER_CACTUS;
                        continue;
                    }
                    if (block.isIn(BlockTags.FIRE) || block == Blocks.LAVA) {
                        type = PathNodeType.DANGER_FIRE;
                        continue;
                    }
                    if (block != Blocks.SWEET_BERRY_BUSH) continue;
                    type = PathNodeType.DANGER_OTHER;
                }
            }
        }
        return type;
    }

    protected static PathNodeType getCommonNodeType(BlockView world, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        if (blockState.isAir()) {
            return PathNodeType.OPEN;
        }
        if (block.isIn(BlockTags.TRAPDOORS) || block == Blocks.LILY_PAD) {
            return PathNodeType.TRAPDOOR;
        }
        if (blockState.matches(BlockTags.FIRE)) {
            return PathNodeType.DAMAGE_FIRE;
        }
        if (block == Blocks.CACTUS) {
            return PathNodeType.DAMAGE_CACTUS;
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return PathNodeType.DAMAGE_OTHER;
        }
        if (block == Blocks.HONEY_BLOCK) {
            return PathNodeType.STICKY_HONEY;
        }
        if (block == Blocks.COCOA) {
            return PathNodeType.COCOA;
        }
        if (DoorBlock.isWoodenDoor(blockState) && !blockState.get(DoorBlock.OPEN).booleanValue()) {
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
        if (block.isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock && !blockState.get(FenceGateBlock.OPEN).booleanValue()) {
            return PathNodeType.FENCE;
        }
        FluidState fluidState = world.getFluidState(blockPos);
        if (fluidState.matches(FluidTags.WATER)) {
            return PathNodeType.WATER;
        }
        if (fluidState.matches(FluidTags.LAVA)) {
            return PathNodeType.LAVA;
        }
        if (blockState.canPathfindThrough(world, blockPos, NavigationType.LAND)) {
            return PathNodeType.OPEN;
        }
        return PathNodeType.BLOCKED;
    }
}

