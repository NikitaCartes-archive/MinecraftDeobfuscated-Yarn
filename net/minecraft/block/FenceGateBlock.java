/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock
extends HorizontalFacingBlock {
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty IN_WALL = Properties.IN_WALL;
    protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    protected static final VoxelShape IN_WALL_Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
    protected static final VoxelShape IN_WALL_X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
    protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
    protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
    protected static final VoxelShape field_11018 = VoxelShapes.union(Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
    protected static final VoxelShape field_11023 = VoxelShapes.union(Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
    protected static final VoxelShape field_11020 = VoxelShapes.union(Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
    protected static final VoxelShape field_11027 = VoxelShapes.union(Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));

    public FenceGateBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(OPEN, false)).with(POWERED, false)).with(IN_WALL, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (blockState.get(IN_WALL).booleanValue()) {
            return blockState.get(FACING).getAxis() == Direction.Axis.X ? IN_WALL_X_AXIS_SHAPE : IN_WALL_Z_AXIS_SHAPE;
        }
        return blockState.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        Direction.Axis axis = direction.getAxis();
        if (blockState.get(FACING).rotateYClockwise().getAxis() == axis) {
            boolean bl = this.isWall(blockState2) || this.isWall(iWorld.getBlockState(blockPos.offset(direction.getOpposite())));
            return (BlockState)blockState.with(IN_WALL, bl);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (blockState.get(OPEN).booleanValue()) {
            return VoxelShapes.empty();
        }
        return blockState.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
    }

    @Override
    public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        if (blockState.get(IN_WALL).booleanValue()) {
            return blockState.get(FACING).getAxis() == Direction.Axis.X ? field_11027 : field_11020;
        }
        return blockState.get(FACING).getAxis() == Direction.Axis.X ? field_11023 : field_11018;
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        switch (blockPlacementEnvironment) {
            case LAND: {
                return blockState.get(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return blockState.get(OPEN);
            }
        }
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        World world = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        boolean bl = world.isReceivingRedstonePower(blockPos);
        Direction direction = itemPlacementContext.getPlayerFacing();
        Direction.Axis axis = direction.getAxis();
        boolean bl2 = axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockPos.west())) || this.isWall(world.getBlockState(blockPos.east()))) || axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockPos.north())) || this.isWall(world.getBlockState(blockPos.south())));
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, direction)).with(OPEN, bl)).with(POWERED, bl)).with(IN_WALL, bl2);
    }

    private boolean isWall(BlockState blockState) {
        return blockState.getBlock().matches(BlockTags.WALLS);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (blockState.get(OPEN).booleanValue()) {
            blockState = (BlockState)blockState.with(OPEN, false);
            world.setBlockState(blockPos, blockState, 10);
        } else {
            Direction direction = playerEntity.getHorizontalFacing();
            if (blockState.get(FACING) == direction.getOpposite()) {
                blockState = (BlockState)blockState.with(FACING, direction);
            }
            blockState = (BlockState)blockState.with(OPEN, true);
            world.setBlockState(blockPos, blockState, 10);
        }
        world.playLevelEvent(playerEntity, blockState.get(OPEN) != false ? 1008 : 1014, blockPos, 0);
        return true;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isClient) {
            return;
        }
        boolean bl2 = world.isReceivingRedstonePower(blockPos);
        if (blockState.get(POWERED) != bl2) {
            world.setBlockState(blockPos, (BlockState)((BlockState)blockState.with(POWERED, bl2)).with(OPEN, bl2), 2);
            if (blockState.get(OPEN) != bl2) {
                world.playLevelEvent(null, bl2 ? 1008 : 1014, blockPos, 0);
            }
        }
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, IN_WALL);
    }

    public static boolean canWallConnect(BlockState blockState, Direction direction) {
        return blockState.get(FACING).getAxis() == direction.rotateYClockwise().getAxis();
    }
}

