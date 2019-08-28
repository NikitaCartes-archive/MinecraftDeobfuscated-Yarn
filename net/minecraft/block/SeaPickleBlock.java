/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SeaPickleBlock
extends PlantBlock
implements Fertilizable,
Waterloggable {
    public static final IntProperty PICKLES = Properties.PICKLES;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape ONE_PICKLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape TWO_PICKLES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
    protected static final VoxelShape THREE_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    protected static final VoxelShape FOUR_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

    protected SeaPickleBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(PICKLES, 1)).with(WATERLOGGED, true));
    }

    @Override
    public int getLuminance(BlockState blockState) {
        return this.isDry(blockState) ? 0 : super.getLuminance(blockState) + 3 * blockState.get(PICKLES);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
        if (blockState.getBlock() == this) {
            return (BlockState)blockState.with(PICKLES, Math.min(4, blockState.get(PICKLES) + 1));
        }
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        boolean bl = fluidState.matches(FluidTags.WATER) && fluidState.getLevel() == 8;
        return (BlockState)super.getPlacementState(itemPlacementContext).with(WATERLOGGED, bl);
    }

    private boolean isDry(BlockState blockState) {
        return blockState.get(WATERLOGGED) == false;
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return !blockState.getCollisionShape(blockView, blockPos).getFace(Direction.UP).isEmpty();
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.down();
        return this.canPlantOnTop(arg.getBlockState(blockPos2), arg, blockPos2);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (blockState.get(WATERLOGGED).booleanValue()) {
            iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        if (itemPlacementContext.getStack().getItem() == this.asItem() && blockState.get(PICKLES) < 4) {
            return true;
        }
        return super.canReplace(blockState, itemPlacementContext);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        switch (blockState.get(PICKLES)) {
            default: {
                return ONE_PICKLE_SHAPE;
            }
            case 2: {
                return TWO_PICKLES_SHAPE;
            }
            case 3: {
                return THREE_PICKLES_SHAPE;
            }
            case 4: 
        }
        return FOUR_PICKLES_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
        if (!this.isDry(blockState) && serverWorld.getBlockState(blockPos.down()).matches(BlockTags.CORAL_BLOCKS)) {
            int i = 5;
            int j = 1;
            int k = 2;
            int l = 0;
            int m = blockPos.getX() - 2;
            int n = 0;
            for (int o = 0; o < 5; ++o) {
                for (int p = 0; p < j; ++p) {
                    int q = 2 + blockPos.getY() - 1;
                    for (int r = q - 2; r < q; ++r) {
                        BlockState blockState2;
                        BlockPos blockPos2 = new BlockPos(m + o, r, blockPos.getZ() - n + p);
                        if (blockPos2 == blockPos || random.nextInt(6) != 0 || serverWorld.getBlockState(blockPos2).getBlock() != Blocks.WATER || !(blockState2 = serverWorld.getBlockState(blockPos2.down())).matches(BlockTags.CORAL_BLOCKS)) continue;
                        serverWorld.setBlockState(blockPos2, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, random.nextInt(4) + 1), 3);
                    }
                }
                if (l < 2) {
                    j += 2;
                    ++n;
                } else {
                    j -= 2;
                    --n;
                }
                ++l;
            }
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(PICKLES, 4), 2);
        }
    }
}

