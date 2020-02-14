/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SnowBlock
extends Block {
    public static final IntProperty LAYERS = Properties.LAYERS;
    protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected SnowBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
    }

    @Override
    public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment env) {
        switch (env) {
            case LAND: {
                return state.get(LAYERS) < 5;
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return false;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return LAYERS_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return LAYERS_TO_SHAPE[state.get(LAYERS) - 1];
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        Block block = blockState.getBlock();
        if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BARRIER) {
            return false;
        }
        if (block == Blocks.HONEY_BLOCK || block == Blocks.SOUL_SAND) {
            return true;
        }
        return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP) || block == this && blockState.get(LAYERS) == 8;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
            SnowBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        int i = state.get(LAYERS);
        if (ctx.getStack().getItem() == this.asItem() && i < 8) {
            if (ctx.canReplaceExisting()) {
                return ctx.getSide() == Direction.UP;
            }
            return true;
        }
        return i == 1;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.getBlock() == this) {
            int i = blockState.get(LAYERS);
            return (BlockState)blockState.with(LAYERS, Math.min(8, i + 1));
        }
        return super.getPlacementState(ctx);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}

