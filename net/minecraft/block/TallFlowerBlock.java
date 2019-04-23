/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TallFlowerBlock
extends TallPlantBlock
implements Fertilizable {
    public TallFlowerBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        return false;
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
    public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        TallFlowerBlock.dropStack(world, blockPos, new ItemStack(this));
    }
}

