/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface Waterloggable
extends FluidDrainable,
FluidFillable {
    @Override
    default public boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return blockState.get(Properties.WATERLOGGED) == false && fluid == Fluids.WATER;
    }

    @Override
    default public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!blockState.get(Properties.WATERLOGGED).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            if (!iWorld.isClient()) {
                iWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.WATERLOGGED, true), 3);
                iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
            }
            return true;
        }
        return false;
    }

    @Override
    default public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        if (blockState.get(Properties.WATERLOGGED).booleanValue()) {
            iWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.WATERLOGGED, false), 3);
            return Fluids.WATER;
        }
        return Fluids.EMPTY;
    }
}

