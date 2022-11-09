/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BambooBlock
extends Block
implements Fertilizable {
    protected static final float field_30997 = 3.0f;
    protected static final float field_30998 = 5.0f;
    protected static final float field_30999 = 1.5f;
    protected static final VoxelShape SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntProperty AGE = Properties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
    public static final IntProperty STAGE = Properties.STAGE;
    public static final int field_31000 = 16;
    public static final int field_31001 = 0;
    public static final int field_31002 = 1;
    public static final int field_31003 = 0;
    public static final int field_31004 = 1;

    public BambooBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0)).with(LEAVES, BambooLeaves.NONE)).with(STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, LEAVES, STAGE);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = state.get(LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
        Vec3d vec3d = state.getModelOffset(world, pos);
        return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return NO_LEAVES_SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (!fluidState.isEmpty()) {
            return null;
        }
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        if (blockState.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockState.isOf(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.getDefaultState().with(AGE, 0);
            }
            if (blockState.isOf(Blocks.BAMBOO)) {
                int i = blockState.get(AGE) > 0 ? 1 : 0;
                return (BlockState)this.getDefaultState().with(AGE, i);
            }
            BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
            if (blockState2.isOf(Blocks.BAMBOO)) {
                return (BlockState)this.getDefaultState().with(AGE, blockState2.get(AGE));
            }
            return Blocks.BAMBOO_SAPLING.getDefaultState();
        }
        return null;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(STAGE) == 0;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i;
        if (state.get(STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9 && (i = this.countBambooBelow(world, pos) + 1) < 16) {
            this.updateLeaves(state, world, pos, random, i);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        if (direction == Direction.UP && neighborState.isOf(Blocks.BAMBOO) && neighborState.get(AGE) > state.get(AGE)) {
            world.setBlockState(pos, (BlockState)state.cycle(AGE), Block.NOTIFY_LISTENERS);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        int j;
        int i = this.countBambooAbove(world, pos);
        return i + (j = this.countBambooBelow(world, pos)) + 1 < 16 && world.getBlockState(pos.up(i)).get(STAGE) != 1;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = this.countBambooAbove(world, pos);
        int j = this.countBambooBelow(world, pos);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);
        for (int m = 0; m < l; ++m) {
            BlockPos blockPos = pos.up(i);
            BlockState blockState = world.getBlockState(blockPos);
            if (k >= 16 || blockState.get(STAGE) == 1 || !world.isAir(blockPos.up())) {
                return;
            }
            this.updateLeaves(blockState, world, blockPos, random, k);
            ++i;
            ++k;
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    protected void updateLeaves(BlockState state, World world, BlockPos pos, Random random, int height) {
        BlockState blockState = world.getBlockState(pos.down());
        BlockPos blockPos = pos.down(2);
        BlockState blockState2 = world.getBlockState(blockPos);
        BambooLeaves bambooLeaves = BambooLeaves.NONE;
        if (height >= 1) {
            if (!blockState.isOf(Blocks.BAMBOO) || blockState.get(LEAVES) == BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.SMALL;
            } else if (blockState.isOf(Blocks.BAMBOO) && blockState.get(LEAVES) != BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.LARGE;
                if (blockState2.isOf(Blocks.BAMBOO)) {
                    world.setBlockState(pos.down(), (BlockState)blockState.with(LEAVES, BambooLeaves.SMALL), Block.NOTIFY_ALL);
                    world.setBlockState(blockPos, (BlockState)blockState2.with(LEAVES, BambooLeaves.NONE), Block.NOTIFY_ALL);
                }
            }
        }
        int i = state.get(AGE) == 1 || blockState2.isOf(Blocks.BAMBOO) ? 1 : 0;
        int j = height >= 11 && random.nextFloat() < 0.25f || height == 15 ? 1 : 0;
        world.setBlockState(pos.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(AGE, i)).with(LEAVES, bambooLeaves)).with(STAGE, j), Block.NOTIFY_ALL);
    }

    protected int countBambooAbove(BlockView world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.up(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }

    protected int countBambooBelow(BlockView world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.down(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }
}

