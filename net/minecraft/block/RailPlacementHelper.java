/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RailPlacementHelper {
    private final World world;
    private final BlockPos pos;
    private final AbstractRailBlock block;
    private BlockState state;
    private final boolean allowCurves;
    private final List<BlockPos> neighbors = Lists.newArrayList();

    public RailPlacementHelper(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.block = (AbstractRailBlock)state.getBlock();
        RailShape railShape = state.get(this.block.getShapeProperty());
        this.allowCurves = this.block.canMakeCurves();
        this.computeNeighbors(railShape);
    }

    public List<BlockPos> getNeighbors() {
        return this.neighbors;
    }

    private void computeNeighbors(RailShape shape) {
        this.neighbors.clear();
        switch (shape) {
            case NORTH_SOUTH: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south());
                break;
            }
            case EAST_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east());
                break;
            }
            case ASCENDING_EAST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east().up());
                break;
            }
            case ASCENDING_WEST: {
                this.neighbors.add(this.pos.west().up());
                this.neighbors.add(this.pos.east());
                break;
            }
            case ASCENDING_NORTH: {
                this.neighbors.add(this.pos.north().up());
                this.neighbors.add(this.pos.south());
                break;
            }
            case ASCENDING_SOUTH: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south().up());
                break;
            }
            case SOUTH_EAST: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.south());
                break;
            }
            case SOUTH_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.south());
                break;
            }
            case NORTH_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.north());
                break;
            }
            case NORTH_EAST: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.north());
            }
        }
    }

    private void method_10467() {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            RailPlacementHelper railPlacementHelper = this.method_10458(this.neighbors.get(i));
            if (railPlacementHelper == null || !railPlacementHelper.isNeighbor(this)) {
                this.neighbors.remove(i--);
                continue;
            }
            this.neighbors.set(i, railPlacementHelper.pos);
        }
    }

    private boolean isVerticallyNearRail(BlockPos pos) {
        return AbstractRailBlock.isRail(this.world, pos) || AbstractRailBlock.isRail(this.world, pos.up()) || AbstractRailBlock.isRail(this.world, pos.down());
    }

    @Nullable
    private RailPlacementHelper method_10458(BlockPos pos) {
        BlockPos blockPos = pos;
        BlockState blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail(blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        blockPos = pos.up();
        blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail(blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        blockPos = pos.down();
        blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail(blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        return null;
    }

    private boolean isNeighbor(RailPlacementHelper other) {
        return this.isNeighbor(other.pos);
    }

    private boolean isNeighbor(BlockPos pos) {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            BlockPos blockPos = this.neighbors.get(i);
            if (blockPos.getX() != pos.getX() || blockPos.getZ() != pos.getZ()) continue;
            return true;
        }
        return false;
    }

    protected int method_10460() {
        int i = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!this.isVerticallyNearRail(this.pos.offset(direction))) continue;
            ++i;
        }
        return i;
    }

    private boolean method_10455(RailPlacementHelper placementHelper) {
        return this.isNeighbor(placementHelper) || this.neighbors.size() != 2;
    }

    private void method_10461(RailPlacementHelper placementHelper) {
        this.neighbors.add(placementHelper.pos);
        BlockPos blockPos = this.pos.north();
        BlockPos blockPos2 = this.pos.south();
        BlockPos blockPos3 = this.pos.west();
        BlockPos blockPos4 = this.pos.east();
        boolean bl = this.isNeighbor(blockPos);
        boolean bl2 = this.isNeighbor(blockPos2);
        boolean bl3 = this.isNeighbor(blockPos3);
        boolean bl4 = this.isNeighbor(blockPos4);
        RailShape railShape = null;
        if (bl || bl2) {
            railShape = RailShape.NORTH_SOUTH;
        }
        if (bl3 || bl4) {
            railShape = RailShape.EAST_WEST;
        }
        if (!this.allowCurves) {
            if (bl2 && bl4 && !bl && !bl3) {
                railShape = RailShape.SOUTH_EAST;
            }
            if (bl2 && bl3 && !bl && !bl4) {
                railShape = RailShape.SOUTH_WEST;
            }
            if (bl && bl3 && !bl2 && !bl4) {
                railShape = RailShape.NORTH_WEST;
            }
            if (bl && bl4 && !bl2 && !bl3) {
                railShape = RailShape.NORTH_EAST;
            }
        }
        if (railShape == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
                railShape = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
                railShape = RailShape.ASCENDING_SOUTH;
            }
        }
        if (railShape == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
                railShape = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
                railShape = RailShape.ASCENDING_WEST;
            }
        }
        if (railShape == null) {
            railShape = RailShape.NORTH_SOUTH;
        }
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), railShape);
        this.world.setBlockState(this.pos, this.state, 3);
    }

    private boolean method_10465(BlockPos pos) {
        RailPlacementHelper railPlacementHelper = this.method_10458(pos);
        if (railPlacementHelper == null) {
            return false;
        }
        railPlacementHelper.method_10467();
        return railPlacementHelper.method_10455(this);
    }

    public RailPlacementHelper updateBlockState(boolean powered, boolean forceUpdate, RailShape railShape) {
        boolean bl10;
        boolean bl6;
        BlockPos blockPos = this.pos.north();
        BlockPos blockPos2 = this.pos.south();
        BlockPos blockPos3 = this.pos.west();
        BlockPos blockPos4 = this.pos.east();
        boolean bl = this.method_10465(blockPos);
        boolean bl2 = this.method_10465(blockPos2);
        boolean bl3 = this.method_10465(blockPos3);
        boolean bl4 = this.method_10465(blockPos4);
        RailShape railShape2 = null;
        boolean bl5 = bl || bl2;
        boolean bl7 = bl6 = bl3 || bl4;
        if (bl5 && !bl6) {
            railShape2 = RailShape.NORTH_SOUTH;
        }
        if (bl6 && !bl5) {
            railShape2 = RailShape.EAST_WEST;
        }
        boolean bl72 = bl2 && bl4;
        boolean bl8 = bl2 && bl3;
        boolean bl9 = bl && bl4;
        boolean bl11 = bl10 = bl && bl3;
        if (!this.allowCurves) {
            if (bl72 && !bl && !bl3) {
                railShape2 = RailShape.SOUTH_EAST;
            }
            if (bl8 && !bl && !bl4) {
                railShape2 = RailShape.SOUTH_WEST;
            }
            if (bl10 && !bl2 && !bl4) {
                railShape2 = RailShape.NORTH_WEST;
            }
            if (bl9 && !bl2 && !bl3) {
                railShape2 = RailShape.NORTH_EAST;
            }
        }
        if (railShape2 == null) {
            if (bl5 && bl6) {
                railShape2 = railShape;
            } else if (bl5) {
                railShape2 = RailShape.NORTH_SOUTH;
            } else if (bl6) {
                railShape2 = RailShape.EAST_WEST;
            }
            if (!this.allowCurves) {
                if (powered) {
                    if (bl72) {
                        railShape2 = RailShape.SOUTH_EAST;
                    }
                    if (bl8) {
                        railShape2 = RailShape.SOUTH_WEST;
                    }
                    if (bl9) {
                        railShape2 = RailShape.NORTH_EAST;
                    }
                    if (bl10) {
                        railShape2 = RailShape.NORTH_WEST;
                    }
                } else {
                    if (bl10) {
                        railShape2 = RailShape.NORTH_WEST;
                    }
                    if (bl9) {
                        railShape2 = RailShape.NORTH_EAST;
                    }
                    if (bl8) {
                        railShape2 = RailShape.SOUTH_WEST;
                    }
                    if (bl72) {
                        railShape2 = RailShape.SOUTH_EAST;
                    }
                }
            }
        }
        if (railShape2 == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
                railShape2 = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
                railShape2 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (railShape2 == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
                railShape2 = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
                railShape2 = RailShape.ASCENDING_WEST;
            }
        }
        if (railShape2 == null) {
            railShape2 = railShape;
        }
        this.computeNeighbors(railShape2);
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), railShape2);
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);
            for (int i = 0; i < this.neighbors.size(); ++i) {
                RailPlacementHelper railPlacementHelper = this.method_10458(this.neighbors.get(i));
                if (railPlacementHelper == null) continue;
                railPlacementHelper.method_10467();
                if (!railPlacementHelper.method_10455(this)) continue;
                railPlacementHelper.method_10461(this);
            }
        }
        return this;
    }

    public BlockState getBlockState() {
        return this.state;
    }
}

