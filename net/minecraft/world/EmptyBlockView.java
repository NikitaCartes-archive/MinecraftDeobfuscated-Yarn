/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public enum EmptyBlockView implements BlockView
{
    INSTANCE;


    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.getDefaultState();
    }
}

