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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
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
    private final boolean sticky;

    public PistonBlock(boolean sticky, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
        this.sticky = sticky;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            this.tryMove(world, pos, state);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            this.tryMove(world, pos, state);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
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
                world.addSyncedBlockEvent(pos, this, 0, direction.getId());
            }
        } else if (!bl && state.get(EXTENDED).booleanValue()) {
            PistonBlockEntity pistonBlockEntity;
            BlockEntity blockEntity;
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.isOf(Blocks.MOVING_PISTON) && blockState.get(FACING) == direction && (blockEntity = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity).isExtending() && (pistonBlockEntity.getProgress(0.0f) < 0.5f || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInBlockTick())) {
                i = 2;
            }
            world.addSyncedBlockEvent(pos, this, i, direction.getId());
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
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
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
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, direction)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            world.setBlockState(pos, blockState, 20);
            world.setBlockEntity(pos, PistonExtensionBlock.createBlockEntityPiston((BlockState)this.getDefaultState().with(FACING, Direction.byId(data & 7)), direction, false, true));
            world.updateNeighbors(pos, blockState.getBlock());
            blockState.updateNeighbors(world, pos, 2);
            if (this.sticky) {
                PistonBlockEntity pistonBlockEntity;
                BlockEntity blockEntity2;
                BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
                BlockState blockState2 = world.getBlockState(blockPos);
                boolean bl2 = false;
                if (blockState2.isOf(Blocks.MOVING_PISTON) && (blockEntity2 = world.getBlockEntity(blockPos)) instanceof PistonBlockEntity && (pistonBlockEntity = (PistonBlockEntity)blockEntity2).getFacing() == direction && pistonBlockEntity.isExtending()) {
                    pistonBlockEntity.finish();
                    bl2 = true;
                }
                if (!bl2) {
                    if (type == 1 && !blockState2.isAir() && PistonBlock.isMovable(blockState2, world, blockPos, direction.getOpposite(), false, direction) && (blockState2.getPistonBehavior() == PistonBehavior.NORMAL || blockState2.isOf(Blocks.PISTON) || blockState2.isOf(Blocks.STICKY_PISTON))) {
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
        if (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.CRYING_OBSIDIAN) || state.isOf(Blocks.RESPAWN_ANCHOR)) {
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
        if (state.isOf(Blocks.PISTON) || state.isOf(Blocks.STICKY_PISTON)) {
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
        return !state.getBlock().hasBlockEntity();
    }

    private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
        int l;
        BlockPos blockPos5;
        BlockPos blockPos3;
        int k;
        PistonHandler pistonHandler;
        BlockPos blockPos = pos.offset(dir);
        if (!retract && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
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
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = retract ? dir : dir.getOpposite();
        int j = 0;
        for (k = list3.size() - 1; k >= 0; --k) {
            blockPos3 = list3.get(k);
            BlockState blockState = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            PistonBlock.dropStacks(blockState, world, blockPos3, blockEntity);
            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
            blockStates[j++] = blockState;
        }
        for (k = list.size() - 1; k >= 0; --k) {
            blockPos3 = list.get(k);
            BlockState blockState = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            world.setBlockState(blockPos3, (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir), 68);
            world.setBlockEntity(blockPos3, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), dir, retract, false));
            blockStates[j++] = blockState;
        }
        if (retract) {
            PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState3 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir)).with(PistonHeadBlock.TYPE, pistonType);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
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
            blockState5.prepare(world, blockPos5, 2);
            blockState4.updateNeighbors(world, blockPos5, 2);
            blockState4.prepare(world, blockPos5, 2);
        }
        j = 0;
        for (l = list3.size() - 1; l >= 0; --l) {
            BlockState blockState = blockStates[j++];
            blockPos5 = list3.get(l);
            blockState.prepare(world, blockPos5, 2);
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
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

