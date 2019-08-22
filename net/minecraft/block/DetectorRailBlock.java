/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.container.Container;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DetectorRailBlock
extends AbstractRailBlock {
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public DetectorRailBlock(Block.Settings settings) {
        super(true, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(POWERED, false)).with(SHAPE, RailShape.NORTH_SOUTH));
    }

    @Override
    public int getTickRate(ViewableWorld viewableWorld) {
        return 20;
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (blockState.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(world, blockPos, blockState);
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (world.isClient || !blockState.get(POWERED).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(world, blockPos, blockState);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        return blockState.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (!blockState.get(POWERED).booleanValue()) {
            return 0;
        }
        return direction == Direction.UP ? 15 : 0;
    }

    private void updatePoweredStatus(World world, BlockPos blockPos, BlockState blockState) {
        BlockState blockState2;
        boolean bl = blockState.get(POWERED);
        boolean bl2 = false;
        List<AbstractMinecartEntity> list = this.getCarts(world, blockPos, AbstractMinecartEntity.class, null);
        if (!list.isEmpty()) {
            bl2 = true;
        }
        if (bl2 && !bl) {
            blockState2 = (BlockState)blockState.with(POWERED, true);
            world.setBlockState(blockPos, blockState2, 3);
            this.updateNearbyRails(world, blockPos, blockState2, true);
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.down(), this);
            world.scheduleBlockRender(blockPos, blockState, blockState2);
        }
        if (!bl2 && bl) {
            blockState2 = (BlockState)blockState.with(POWERED, false);
            world.setBlockState(blockPos, blockState2, 3);
            this.updateNearbyRails(world, blockPos, blockState2, false);
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.down(), this);
            world.scheduleBlockRender(blockPos, blockState, blockState2);
        }
        if (bl2) {
            world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
        }
        world.updateHorizontalAdjacent(blockPos, this);
    }

    protected void updateNearbyRails(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
        RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, blockPos, blockState);
        List<BlockPos> list = railPlacementHelper.getNeighbors();
        for (BlockPos blockPos2 : list) {
            BlockState blockState2 = world.getBlockState(blockPos2);
            blockState2.neighborUpdate(world, blockPos2, blockState2.getBlock(), blockPos, false);
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
        this.updatePoweredStatus(world, blockPos, blockState);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        if (blockState.get(POWERED).booleanValue()) {
            List<CommandBlockMinecartEntity> list = this.getCarts(world, blockPos, CommandBlockMinecartEntity.class, null);
            if (!list.isEmpty()) {
                return list.get(0).getCommandExecutor().getSuccessCount();
            }
            List<AbstractMinecartEntity> list2 = this.getCarts(world, blockPos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
            if (!list2.isEmpty()) {
                return Container.calculateComparatorOutput((Inventory)((Object)list2.get(0)));
            }
        }
        return 0;
    }

    protected <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos blockPos, Class<T> class_, @Nullable Predicate<Entity> predicate) {
        return world.getEntities(class_, this.getCartDetectionBox(blockPos), predicate);
    }

    private Box getCartDetectionBox(BlockPos blockPos) {
        float f = 0.2f;
        return new Box((float)blockPos.getX() + 0.2f, blockPos.getY(), (float)blockPos.getZ() + 0.2f, (float)(blockPos.getX() + 1) - 0.2f, (float)(blockPos.getY() + 1) - 0.2f, (float)(blockPos.getZ() + 1) - 0.2f);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        switch (blockRotation) {
            case CLOCKWISE_180: {
                switch (blockState.get(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (blockState.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (blockState.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        RailShape railShape = blockState.get(SHAPE);
        switch (blockMirror) {
            case LEFT_RIGHT: {
                switch (railShape) {
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch (railShape) {
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror(blockState, blockMirror);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED);
    }
}

