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
import net.minecraft.server.world.ServerWorld;
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return AGE_TO_SHAPE[state.get(AGE)];
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (world.getBaseLightLevel(pos, 0) < 9) {
            return;
        }
        float f = CropBlock.getAvailableMoisture(this, world, pos);
        if (random.nextInt((int)(25.0f / f) + 1) == 0) {
            int i = state.get(AGE);
            if (i < 7) {
                state = (BlockState)state.with(AGE, i + 1);
                world.setBlockState(pos, state, 2);
            } else {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                BlockPos blockPos = pos.offset(direction);
                Block block = world.getBlockState(blockPos.down()).getBlock();
                if (world.getBlockState(blockPos).isAir() && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                    world.setBlockState(blockPos, this.gourdBlock.getDefaultState());
                    world.setBlockState(pos, (BlockState)this.gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        Item item = this.getPickItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) != 7;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(7, state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
        BlockState blockState = (BlockState)state.with(AGE, i);
        world.setBlockState(pos, blockState, 2);
        if (i == 7) {
            blockState.scheduledTick(world, pos, world.random);
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

