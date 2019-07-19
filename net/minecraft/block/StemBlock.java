/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StemBlock
extends PlantBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_7;
    protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
    private final GourdBlock gourdBlock;

    protected StemBlock(GourdBlock gourdBlock, Block.Settings settings) {
        super(settings);
        this.gourdBlock = gourdBlock;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        return AGE_TO_SHAPE[blockState.get(AGE)];
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        super.onScheduledTick(blockState, world, blockPos, random);
        if (world.getLightLevel(blockPos, 0) < 9) {
            return;
        }
        float f = CropBlock.getAvailableMoisture(this, world, blockPos);
        if (random.nextInt((int)(25.0f / f) + 1) == 0) {
            int i = blockState.get(AGE);
            if (i < 7) {
                blockState = (BlockState)blockState.with(AGE, i + 1);
                world.setBlockState(blockPos, blockState, 2);
            } else {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                BlockPos blockPos2 = blockPos.offset(direction);
                Block block = world.getBlockState(blockPos2.down()).getBlock();
                if (world.getBlockState(blockPos2).isAir() && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                    world.setBlockState(blockPos2, this.gourdBlock.getDefaultState());
                    world.setBlockState(blockPos, (BlockState)this.gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                }
            }
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    protected Item getPickItem() {
        if (this.gourdBlock == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        if (this.gourdBlock == Blocks.MELON) {
            return Items.MELON_SEEDS;
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        Item item = this.getPickItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
        return blockState.get(AGE) != 7;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        int i = Math.min(7, blockState.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
        BlockState blockState2 = (BlockState)blockState.with(AGE, i);
        world.setBlockState(blockPos, blockState2, 2);
        if (i == 7) {
            blockState2.scheduledTick(world, blockPos, world.random);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public GourdBlock getGourdBlock() {
        return this.gourdBlock;
    }
}

