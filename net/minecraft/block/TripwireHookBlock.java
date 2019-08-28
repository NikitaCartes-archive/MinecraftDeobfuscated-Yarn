/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TripwireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripwireHookBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        switch (blockState.get(FACING)) {
            default: {
                return WEST_SHAPE;
            }
            case WEST: {
                return EAST_SHAPE;
            }
            case SOUTH: {
                return NORTH_SHAPE;
            }
            case NORTH: 
        }
        return SOUTH_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        Direction direction = blockState.get(FACING);
        BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
        BlockState blockState2 = arg.getBlockState(blockPos2);
        return direction.getAxis().isHorizontal() && blockState2.isSideSolidFullSquare(arg, blockPos2, direction) && !blockState2.emitsRedstonePower();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        Direction[] directions;
        BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
        World lv = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        for (Direction direction : directions = itemPlacementContext.getPlacementDirections()) {
            Direction direction2;
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.with(FACING, direction2 = direction.getOpposite())).canPlaceAt(lv, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        this.update(world, blockPos, blockState, false, false, -1, null);
    }

    public void update(World world, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int i, @Nullable BlockState blockState2) {
        BlockPos blockPos2;
        Direction direction = blockState.get(FACING);
        boolean bl3 = blockState.get(ATTACHED);
        boolean bl4 = blockState.get(POWERED);
        boolean bl5 = !bl;
        boolean bl6 = false;
        int j = 0;
        BlockState[] blockStates = new BlockState[42];
        for (int k = 1; k < 42; ++k) {
            blockPos2 = blockPos.offset(direction, k);
            BlockState blockState3 = world.getBlockState(blockPos2);
            if (blockState3.getBlock() == Blocks.TRIPWIRE_HOOK) {
                if (blockState3.get(FACING) != direction.getOpposite()) break;
                j = k;
                break;
            }
            if (blockState3.getBlock() == Blocks.TRIPWIRE || k == i) {
                if (k == i) {
                    blockState3 = MoreObjects.firstNonNull(blockState2, blockState3);
                }
                boolean bl7 = blockState3.get(TripwireBlock.DISARMED) == false;
                boolean bl8 = blockState3.get(TripwireBlock.POWERED);
                bl6 |= bl7 && bl8;
                blockStates[k] = blockState3;
                if (k != i) continue;
                world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
                bl5 &= bl7;
                continue;
            }
            blockStates[k] = null;
            bl5 = false;
        }
        BlockState blockState4 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl5)).with(POWERED, bl6 &= (bl5 &= j > 1));
        if (j > 0) {
            blockPos2 = blockPos.offset(direction, j);
            Direction direction2 = direction.getOpposite();
            world.setBlockState(blockPos2, (BlockState)blockState4.with(FACING, direction2), 3);
            this.updateNeighborsOnAxis(world, blockPos2, direction2);
            this.playSound(world, blockPos2, bl5, bl6, bl3, bl4);
        }
        this.playSound(world, blockPos, bl5, bl6, bl3, bl4);
        if (!bl) {
            world.setBlockState(blockPos, (BlockState)blockState4.with(FACING, direction), 3);
            if (bl2) {
                this.updateNeighborsOnAxis(world, blockPos, direction);
            }
        }
        if (bl3 != bl5) {
            for (int l = 1; l < j; ++l) {
                BlockPos blockPos3 = blockPos.offset(direction, l);
                BlockState blockState5 = blockStates[l];
                if (blockState5 == null) continue;
                world.setBlockState(blockPos3, (BlockState)blockState5.with(ATTACHED, bl5), 3);
                if (world.getBlockState(blockPos3).isAir()) continue;
            }
        }
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        this.update(serverWorld, blockPos, blockState, false, true, -1, null);
    }

    private void playSound(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (bl2 && !bl4) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
        } else if (!bl2 && bl4) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
        } else if (bl && !bl3) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
        } else if (!bl && bl3) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (world.random.nextFloat() * 0.2f + 0.9f));
        }
    }

    private void updateNeighborsOnAxis(World world, BlockPos blockPos, Direction direction) {
        world.updateNeighborsAlways(blockPos, this);
        world.updateNeighborsAlways(blockPos.offset(direction.getOpposite()), this);
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.getBlock() == blockState2.getBlock()) {
            return;
        }
        boolean bl2 = blockState.get(ATTACHED);
        boolean bl3 = blockState.get(POWERED);
        if (bl2 || bl3) {
            this.update(world, blockPos, blockState, true, false, -1, null);
        }
        if (bl3) {
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.offset(blockState.get(FACING).getOpposite()), this);
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
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
        if (blockState.get(FACING) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, ATTACHED);
    }
}

