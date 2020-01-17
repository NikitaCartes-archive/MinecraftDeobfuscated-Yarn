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

    public PistonBlock(boolean isSticky, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
        this.isSticky = isSticky;
    }

    @Override
    public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
        return state.get(EXTENDED) == false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(EXTENDED).booleanValue()) {
            switch (state.get(FACING)) {
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
    public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            this.tryMove(world, pos, state);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (!world.isClient) {
            this.tryMove(world, pos, state);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if (!world.isClient && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite())).with(EXTENDED, false);
    }

    private void tryMove(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        boolean bl = this.shouldExtend(world, pos, direction);
        if (bl && !state.get(EXTENDED).booleanValue()) {
            if (new PistonHandler(world, pos, direction, true).calculatePush()) {
                world.addBlockAction(pos, this, 0, direction.getId());
            }
        } else if (!bl && state.get(EXTENDED).booleanValue()) {
            PistonBlockEntity pistonBlockEntity;
            BlockEntity blockEntity;
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.getBlock() == Blocks.MOVING_PISTON && blockState.get(FACING) == direction && (blockEntity = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity).isExtending() && (pistonBlockEntity.getProgress(0.0f) < 0.5f || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInsideTick())) {
                i = 2;
            }
            world.addBlockAction(pos, this, i, direction.getId());
        }
    }

    private boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for (Direction direction : Direction.values()) {
            if (direction == pistonFace || !world.isEmittingRedstonePower(pos.offset(direction), direction)) continue;
            return true;
        }
        if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
            return true;
        }
        BlockPos blockPos = pos.up();
        for (Direction direction2 : Direction.values()) {
            if (direction2 == Direction.DOWN || !world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean onBlockAction(BlockState state, World world, BlockPos pos, int type, int data) {
        Direction direction = state.get(FACING);
        if (!world.isClient) {
            boolean bl = this.shouldExtend(world, pos, direction);
            if (bl && (type == 1 || type == 2)) {
                world.setBlockState(pos, (BlockState)state.with(EXTENDED, true), 2);
                return false;
            }
            if (!bl && type == 0) {
                return false;
            }
        }
        if (type == 0) {
            if (!this.move(world, pos, direction, true)) return false;
            world.setBlockState(pos, (BlockState)state.with(EXTENDED, true), 67);
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.25f + 0.6f);
            return true;
        } else {
            if (type != 1 && type != 2) return true;
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
            if (blockEntity instanceof PistonBlockEntity) {
                ((PistonBlockEntity)blockEntity).finish();
            }
            world.setBlockState(pos, (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, direction)).with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
            world.setBlockEntity(pos, PistonExtensionBlock.createBlockEntityPiston((BlockState)this.getDefaultState().with(FACING, Direction.byId(data & 7)), direction, false, true));
            if (this.isSticky) {
                PistonBlockEntity pistonBlockEntity;
                BlockEntity blockEntity2;
                BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                boolean bl2 = false;
                if (block == Blocks.MOVING_PISTON && (blockEntity2 = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity2).getFacing() == direction && pistonBlockEntity.isExtending()) {
                    pistonBlockEntity.finish();
                    bl2 = true;
                }
                if (!bl2) {
                    if (type == 1 && !blockState.isAir() && PistonBlock.isMovable(blockState, world, blockPos, direction.getOpposite(), false, direction) && (blockState.getPistonBehavior() == PistonBehavior.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
                        this.move(world, pos, direction, false);
                    } else {
                        world.removeBlock(pos.offset(direction), false);
                    }
                }
            } else {
                world.removeBlock(pos.offset(direction), false);
            }
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.15f + 0.6f);
        }
        return true;
    }

    public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir) {
        Block block = state.getBlock();
        if (block == Blocks.OBSIDIAN) {
            return false;
        }
        if (!world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (pos.getY() < 0 || motionDir == Direction.DOWN && pos.getY() == 0) {
            return false;
        }
        if (pos.getY() > world.getHeight() - 1 || motionDir == Direction.UP && pos.getY() == world.getHeight() - 1) {
            return false;
        }
        if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
            if (state.get(EXTENDED).booleanValue()) {
                return false;
            }
        } else {
            if (state.getHardness(world, pos) == -1.0f) {
                return false;
            }
            switch (state.getPistonBehavior()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return canBreak;
                }
                case PUSH_ONLY: {
                    return motionDir == pistonDir;
                }
            }
        }
        return !block.hasBlockEntity();
    }

    private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
        int l;
        BlockPos blockPos5;
        BlockPos blockPos3;
        int k;
        PistonHandler pistonHandler;
        BlockPos blockPos = pos.offset(dir);
        if (!retract && world.getBlockState(blockPos).getBlock() == Blocks.PISTON_HEAD) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 20);
        }
        if (!(pistonHandler = new PistonHandler(world, pos, dir, retract)).calculatePush()) {
            return false;
        }
        HashMap<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = pistonHandler.getMovedBlocks();
        ArrayList<BlockState> list2 = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            BlockPos blockPos2 = list.get(i);
            BlockState blockState = world.getBlockState(blockPos2);
            list2.add(blockState);
            map.put(blockPos2, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
        int j = list.size() + list3.size();
        BlockState[] blockStates = new BlockState[j];
        Direction direction = retract ? dir : dir.getOpposite();
        for (k = list3.size() - 1; k >= 0; --k) {
            blockPos3 = list3.get(k);
            BlockState blockState = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            PistonBlock.dropStacks(blockState, world, blockPos3, blockEntity);
            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
            blockStates[--j] = blockState;
        }
        for (k = list.size() - 1; k >= 0; --k) {
            blockPos3 = list.get(k);
            BlockState blockState = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            world.setBlockState(blockPos3, (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir), 68);
            world.setBlockEntity(blockPos3, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), dir, retract, false));
            blockStates[--j] = blockState;
        }
        if (retract) {
            PistonType pistonType = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState3 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir)).with(PistonHeadBlock.TYPE, pistonType);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir)).with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos);
            world.setBlockState(blockPos, blockState, 68);
            world.setBlockEntity(blockPos, PistonExtensionBlock.createBlockEntityPiston(blockState3, dir, true, true));
        }
        BlockState blockState4 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos2 : map.keySet()) {
            world.setBlockState(blockPos2, blockState4, 82);
        }
        for (Map.Entry entry : map.entrySet()) {
            blockPos5 = (BlockPos)entry.getKey();
            BlockState blockState5 = (BlockState)entry.getValue();
            blockState5.method_11637(world, blockPos5, 2);
            blockState4.updateNeighborStates(world, blockPos5, 2);
            blockState4.method_11637(world, blockPos5, 2);
        }
        for (l = list3.size() - 1; l >= 0; --l) {
            BlockState blockState = blockStates[j++];
            blockPos5 = list3.get(l);
            blockState.method_11637(world, blockPos5, 2);
            world.updateNeighborsAlways(blockPos5, blockState.getBlock());
        }
        for (l = list.size() - 1; l >= 0; --l) {
            world.updateNeighborsAlways(list.get(l), blockStates[j++].getBlock());
        }
        if (retract) {
            world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD);
        }
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return state.get(EXTENDED);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}

