/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractRailBlock
extends Block {
    protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final boolean allowCurves;

    public static boolean isRail(World world, BlockPos pos) {
        return AbstractRailBlock.isRail(world.getBlockState(pos));
    }

    public static boolean isRail(BlockState state) {
        return state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock;
    }

    protected AbstractRailBlock(boolean allowCurves, AbstractBlock.Settings settings) {
        super(settings);
        this.allowCurves = allowCurves;
    }

    public boolean canMakeCurves() {
        return this.allowCurves;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        RailShape railShape;
        RailShape railShape2 = railShape = state.isOf(this) ? state.get(this.getShapeProperty()) : null;
        if (railShape != null && railShape.isAscending()) {
            return ASCENDING_SHAPE;
        }
        return STRAIGHT_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractRailBlock.hasTopRim(world, pos.down());
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.updateCurves(state, world, pos, notify);
    }

    protected BlockState updateCurves(BlockState state, World world, BlockPos pos, boolean notify) {
        state = this.updateBlockState(world, pos, state, true);
        if (this.allowCurves) {
            state.neighborUpdate(world, pos, this, pos, notify);
        }
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.isClient) {
            return;
        }
        RailShape railShape = state.get(this.getShapeProperty());
        if (AbstractRailBlock.shouldDropRail(pos, world, railShape) && !world.isAir(pos)) {
            if (!notify) {
                AbstractRailBlock.dropStacks(state, world, pos);
            }
            world.removeBlock(pos, notify);
        } else {
            this.updateBlockState(state, world, pos, block);
        }
    }

    /**
     * Checks if this rail should be dropped.
     * 
     * <p>This method will return true if:
     * <ul><li>The rail block is ascending.</li>
     * <li>The block in the direction of ascension does not have a top rim.</li></ul>
     */
    private static boolean shouldDropRail(BlockPos pos, World world, RailShape shape) {
        if (!AbstractRailBlock.hasTopRim(world, pos.down())) {
            return true;
        }
        switch (shape) {
            case ASCENDING_EAST: {
                return !AbstractRailBlock.hasTopRim(world, pos.east());
            }
            case ASCENDING_WEST: {
                return !AbstractRailBlock.hasTopRim(world, pos.west());
            }
            case ASCENDING_NORTH: {
                return !AbstractRailBlock.hasTopRim(world, pos.north());
            }
            case ASCENDING_SOUTH: {
                return !AbstractRailBlock.hasTopRim(world, pos.south());
            }
        }
        return false;
    }

    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
    }

    protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean forceUpdate) {
        if (world.isClient) {
            return state;
        }
        RailShape railShape = state.get(this.getShapeProperty());
        return new RailPlacementHelper(world, pos, state).updateBlockState(world.isReceivingRedstonePower(pos), forceUpdate, railShape).getBlockState();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (notify) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, notify);
        if (state.get(this.getShapeProperty()).isAscending()) {
            world.updateNeighborsAlways(pos.up(), this);
        }
        if (this.allowCurves) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.down(), this);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getDefaultState();
        Direction direction = ctx.getPlayerFacing();
        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
        return (BlockState)blockState.with(this.getShapeProperty(), bl ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    public abstract Property<RailShape> getShapeProperty();
}

