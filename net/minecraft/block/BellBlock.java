/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BellBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
    private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
    private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    private static final VoxelShape BELL_WAIST_SHAPE = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
    private static final VoxelShape BELL_LIP_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
    private static final VoxelShape BELL_SHAPE = VoxelShapes.union(BELL_LIP_SHAPE, BELL_WAIST_SHAPE);
    private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
    private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
    private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
    private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
    private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

    public BellBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.NORTH)).with(ATTACHMENT, Attachment.FLOOR));
    }

    @Override
    public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
        if (entity instanceof ProjectileEntity) {
            Entity entity2 = ((ProjectileEntity)entity).getOwner();
            PlayerEntity playerEntity = entity2 instanceof PlayerEntity ? (PlayerEntity)entity2 : null;
            this.ring(world, blockState, world.getBlockEntity(blockHitResult.getBlockPos()), blockHitResult, playerEntity, true);
        }
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        return this.ring(world, blockState, world.getBlockEntity(blockPos), blockHitResult, playerEntity, true);
    }

    public boolean ring(World world, BlockState blockState, @Nullable BlockEntity blockEntity, BlockHitResult blockHitResult, @Nullable PlayerEntity playerEntity, boolean bl) {
        boolean bl2;
        Direction direction = blockHitResult.getSide();
        BlockPos blockPos = blockHitResult.getBlockPos();
        boolean bl3 = bl2 = !bl || this.isPointOnBell(blockState, direction, blockHitResult.getPos().y - (double)blockPos.getY());
        if (!world.isClient && blockEntity instanceof BellBlockEntity && bl2) {
            ((BellBlockEntity)blockEntity).activate(direction);
            this.ring(world, blockPos);
            if (playerEntity != null) {
                playerEntity.incrementStat(Stats.BELL_RING);
            }
            return true;
        }
        return true;
    }

    private boolean isPointOnBell(BlockState blockState, Direction direction, double d) {
        if (direction.getAxis() == Direction.Axis.Y || d > (double)0.8124f) {
            return false;
        }
        Direction direction2 = blockState.get(FACING);
        Attachment attachment = blockState.get(ATTACHMENT);
        switch (attachment) {
            case FLOOR: {
                return direction2.getAxis() == direction.getAxis();
            }
            case SINGLE_WALL: 
            case DOUBLE_WALL: {
                return direction2.getAxis() != direction.getAxis();
            }
            case CEILING: {
                return true;
            }
        }
        return false;
    }

    private void ring(World world, BlockPos blockPos) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 1.0f);
    }

    private VoxelShape getShape(BlockState blockState) {
        Direction direction = blockState.get(FACING);
        Attachment attachment = blockState.get(ATTACHMENT);
        if (attachment == Attachment.FLOOR) {
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                return NORTH_SOUTH_SHAPE;
            }
            return EAST_WEST_SHAPE;
        }
        if (attachment == Attachment.CEILING) {
            return HANGING_SHAPE;
        }
        if (attachment == Attachment.DOUBLE_WALL) {
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                return NORTH_SOUTH_WALLS_SHAPE;
            }
            return EAST_WEST_WALLS_SHAPE;
        }
        if (direction == Direction.NORTH) {
            return NORTH_WALL_SHAPE;
        }
        if (direction == Direction.SOUTH) {
            return SOUTH_WALL_SHAPE;
        }
        if (direction == Direction.EAST) {
            return EAST_WALL_SHAPE;
        }
        return WEST_WALL_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return this.getShape(blockState);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return this.getShape(blockState);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        Direction direction = itemPlacementContext.getSide();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        Direction.Axis axis = direction.getAxis();
        if (axis == Direction.Axis.Y) {
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(ATTACHMENT, direction == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR)).with(FACING, itemPlacementContext.getPlayerFacing());
            if (blockState.canPlaceAt(itemPlacementContext.getWorld(), blockPos)) {
                return blockState;
            }
        } else {
            boolean bl = axis == Direction.Axis.X && BellBlock.isSolidFullSquare(world.getBlockState(blockPos.west()), world, blockPos.west(), Direction.EAST) && BellBlock.isSolidFullSquare(world.getBlockState(blockPos.east()), world, blockPos.east(), Direction.WEST) || axis == Direction.Axis.Z && BellBlock.isSolidFullSquare(world.getBlockState(blockPos.north()), world, blockPos.north(), Direction.SOUTH) && BellBlock.isSolidFullSquare(world.getBlockState(blockPos.south()), world, blockPos.south(), Direction.NORTH);
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getOpposite())).with(ATTACHMENT, bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
            if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
                return blockState;
            }
            boolean bl2 = BellBlock.isSolidFullSquare(world.getBlockState(blockPos.down()), world, blockPos.down(), Direction.UP);
            if ((blockState = (BlockState)blockState.with(ATTACHMENT, bl2 ? Attachment.FLOOR : Attachment.CEILING)).canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
                return blockState;
            }
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        Attachment attachment = blockState.get(ATTACHMENT);
        Direction direction2 = BellBlock.getPlacementSide(blockState).getOpposite();
        if (direction2 == direction && !blockState.canPlaceAt(iWorld, blockPos) && attachment != Attachment.DOUBLE_WALL) {
            return Blocks.AIR.getDefaultState();
        }
        if (direction.getAxis() == blockState.get(FACING).getAxis()) {
            if (attachment == Attachment.DOUBLE_WALL && !BellBlock.isSolidFullSquare(blockState2, iWorld, blockPos2, direction)) {
                return (BlockState)((BlockState)blockState.with(ATTACHMENT, Attachment.SINGLE_WALL)).with(FACING, direction.getOpposite());
            }
            if (attachment == Attachment.SINGLE_WALL && direction2.getOpposite() == direction && BellBlock.isSolidFullSquare(blockState2, iWorld, blockPos2, blockState.get(FACING))) {
                return (BlockState)blockState.with(ATTACHMENT, Attachment.DOUBLE_WALL);
            }
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
        return WallMountedBlock.canPlaceAt(viewableWorld, blockPos, BellBlock.getPlacementSide(blockState).getOpposite());
    }

    private static Direction getPlacementSide(BlockState blockState) {
        switch (blockState.get(ATTACHMENT)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return blockState.get(FACING).getOpposite();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.DESTROY;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHMENT);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BellBlockEntity();
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }
}

