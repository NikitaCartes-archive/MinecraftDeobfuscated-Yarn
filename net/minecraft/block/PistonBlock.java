/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock
extends FacingBlock {
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    private final boolean isSticky;

    public PistonBlock(boolean bl, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
        this.isSticky = bl;
    }

    @Override
    public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.get(EXTENDED) == false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        if (blockState.get(EXTENDED).booleanValue()) {
            switch (blockState.get(FACING)) {
                case DOWN: {
                    return EXTENDED_DOWN_SHAPE;
                }
                default: {
                    return EXTENDED_UP_SHAPE;
                }
                case NORTH: {
                    return EXTENDED_NORTH_SHAPE;
                }
                case SOUTH: {
                    return EXTENDED_SOUTH_SHAPE;
                }
                case WEST: {
                    return EXTENDED_WEST_SHAPE;
                }
                case EAST: 
            }
            return EXTENDED_EAST_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        if (!world.isClient) {
            this.tryMove(world, blockPos, blockState);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (!world.isClient) {
            this.tryMove(world, blockPos, blockState);
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        if (!world.isClient && world.getBlockEntity(blockPos) == null) {
            this.tryMove(world, blockPos, blockState);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, itemPlacementContext.getPlayerLookDirection().getOpposite())).with(EXTENDED, false);
    }

    private void tryMove(World world, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.get(FACING);
        boolean bl = this.shouldExtend(world, blockPos, direction);
        if (bl && !blockState.get(EXTENDED).booleanValue()) {
            if (new PistonHandler(world, blockPos, direction, true).calculatePush()) {
                world.addBlockAction(blockPos, this, 0, direction.getId());
            }
        } else if (!bl && blockState.get(EXTENDED).booleanValue()) {
            PistonBlockEntity pistonBlockEntity;
            BlockEntity blockEntity;
            BlockPos blockPos2 = blockPos.method_10079(direction, 2);
            BlockState blockState2 = world.getBlockState(blockPos2);
            int i = 1;
            if (blockState2.getBlock() == Blocks.MOVING_PISTON && blockState2.get(FACING) == direction && (blockEntity = world.getBlockEntity(blockPos2)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity).isExtending() && (pistonBlockEntity.getProgress(0.0f) < 0.5f || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInsideTick())) {
                i = 2;
            }
            world.addBlockAction(blockPos, this, i, direction.getId());
        }
    }

    private boolean shouldExtend(World world, BlockPos blockPos, Direction direction) {
        for (Direction direction2 : Direction.values()) {
            if (direction2 == direction || !world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) continue;
            return true;
        }
        if (world.isEmittingRedstonePower(blockPos, Direction.DOWN)) {
            return true;
        }
        BlockPos blockPos2 = blockPos.up();
        for (Direction direction3 : Direction.values()) {
            if (direction3 == Direction.DOWN || !world.isEmittingRedstonePower(blockPos2.offset(direction3), direction3)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
        Direction direction = blockState.get(FACING);
        if (!world.isClient) {
            boolean bl = this.shouldExtend(world, blockPos, direction);
            if (bl && (i == 1 || i == 2)) {
                world.setBlockState(blockPos, (BlockState)blockState.with(EXTENDED, true), 2);
                return false;
            }
            if (!bl && i == 0) {
                return false;
            }
        }
        if (i == 0) {
            if (!this.move(world, blockPos, direction, true)) return false;
            world.setBlockState(blockPos, (BlockState)blockState.with(EXTENDED, true), 67);
            world.playSound(null, blockPos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.25f + 0.6f);
            return true;
        } else {
            if (i != 1 && i != 2) return true;
            BlockEntity blockEntity = world.getBlockEntity(blockPos.offset(direction));
            if (blockEntity instanceof PistonBlockEntity) {
                ((PistonBlockEntity)blockEntity).finish();
            }
            world.setBlockState(blockPos, (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, direction)).with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
            world.setBlockEntity(blockPos, PistonExtensionBlock.createBlockEntityPiston((BlockState)this.getDefaultState().with(FACING, Direction.byId(j & 7)), direction, false, true));
            if (this.isSticky) {
                PistonBlockEntity pistonBlockEntity;
                BlockEntity blockEntity2;
                BlockPos blockPos2 = blockPos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
                BlockState blockState2 = world.getBlockState(blockPos2);
                Block block = blockState2.getBlock();
                boolean bl2 = false;
                if (block == Blocks.MOVING_PISTON && (blockEntity2 = world.getBlockEntity(blockPos2)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity2).getFacing() == direction && pistonBlockEntity.isExtending()) {
                    pistonBlockEntity.finish();
                    bl2 = true;
                }
                if (!bl2) {
                    if (i == 1 && !blockState2.isAir() && PistonBlock.isMovable(blockState2, world, blockPos2, direction.getOpposite(), false, direction) && (blockState2.getPistonBehavior() == PistonBehavior.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
                        this.move(world, blockPos, direction, false);
                    } else {
                        world.removeBlock(blockPos.offset(direction), false);
                    }
                }
            } else {
                world.removeBlock(blockPos.offset(direction), false);
            }
            world.playSound(null, blockPos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.15f + 0.6f);
        }
        return true;
    }

    public static boolean isMovable(BlockState blockState, World world, BlockPos blockPos, Direction direction, boolean bl, Direction direction2) {
        Block block = blockState.getBlock();
        if (block == Blocks.OBSIDIAN) {
            return false;
        }
        if (!world.getWorldBorder().contains(blockPos)) {
            return false;
        }
        if (blockPos.getY() < 0 || direction == Direction.DOWN && blockPos.getY() == 0) {
            return false;
        }
        if (blockPos.getY() > world.getHeight() - 1 || direction == Direction.UP && blockPos.getY() == world.getHeight() - 1) {
            return false;
        }
        if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
            if (blockState.get(EXTENDED).booleanValue()) {
                return false;
            }
        } else {
            if (blockState.getHardness(world, blockPos) == -1.0f) {
                return false;
            }
            switch (blockState.getPistonBehavior()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return bl;
                }
                case PUSH_ONLY: {
                    return direction == direction2;
                }
            }
        }
        return !block.hasBlockEntity();
    }

    private boolean move(World world, BlockPos blockPos, Direction direction, boolean bl) {
        int l;
        BlockPos blockPos6;
        BlockPos blockPos4;
        int k;
        PistonHandler pistonHandler;
        BlockPos blockPos2 = blockPos.offset(direction);
        if (!bl && world.getBlockState(blockPos2).getBlock() == Blocks.PISTON_HEAD) {
            world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 20);
        }
        if (!(pistonHandler = new PistonHandler(world, blockPos, direction, bl)).calculatePush()) {
            return false;
        }
        HashMap<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = pistonHandler.getMovedBlocks();
        ArrayList<BlockState> list2 = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            BlockPos blockPos3 = list.get(i);
            BlockState blockState = world.getBlockState(blockPos3);
            list2.add(blockState);
            map.put(blockPos3, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
        int j = list.size() + list3.size();
        BlockState[] blockStates = new BlockState[j];
        Direction direction2 = bl ? direction : direction.getOpposite();
        for (k = list3.size() - 1; k >= 0; --k) {
            blockPos4 = list3.get(k);
            BlockState blockState = world.getBlockState(blockPos4);
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos4) : null;
            PistonBlock.dropStacks(blockState, world, blockPos4, blockEntity);
            world.setBlockState(blockPos4, Blocks.AIR.getDefaultState(), 18);
            blockStates[--j] = blockState;
        }
        for (k = list.size() - 1; k >= 0; --k) {
            blockPos4 = list.get(k);
            BlockState blockState = world.getBlockState(blockPos4);
            blockPos4 = blockPos4.offset(direction2);
            map.remove(blockPos4);
            world.setBlockState(blockPos4, (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, direction), 68);
            world.setBlockEntity(blockPos4, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), direction, bl, false));
            blockStates[--j] = blockState;
        }
        if (bl) {
            PistonType pistonType = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState3 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, direction)).with(PistonHeadBlock.TYPE, pistonType);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, direction)).with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos2);
            world.setBlockState(blockPos2, blockState, 68);
            world.setBlockEntity(blockPos2, PistonExtensionBlock.createBlockEntityPiston(blockState3, direction, true, true));
        }
        BlockState blockState4 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos3 : map.keySet()) {
            world.setBlockState(blockPos3, blockState4, 82);
        }
        for (Map.Entry entry : map.entrySet()) {
            blockPos6 = (BlockPos)entry.getKey();
            BlockState blockState5 = (BlockState)entry.getValue();
            blockState5.method_11637(world, blockPos6, 2);
            blockState4.updateNeighborStates(world, blockPos6, 2);
            blockState4.method_11637(world, blockPos6, 2);
        }
        for (l = list3.size() - 1; l >= 0; --l) {
            BlockState blockState = blockStates[j++];
            blockPos6 = list3.get(l);
            blockState.method_11637(world, blockPos6, 2);
            world.updateNeighborsAlways(blockPos6, blockState.getBlock());
        }
        for (l = list.size() - 1; l >= 0; --l) {
            world.updateNeighborsAlways(list.get(l), blockStates[j++].getBlock());
        }
        if (bl) {
            world.updateNeighborsAlways(blockPos2, Blocks.PISTON_HEAD);
        }
        return true;
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    @Override
    public boolean hasSidedTransparency(BlockState blockState) {
        return blockState.get(EXTENDED);
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }
}

