/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public interface FluidFillable {
    public boolean canFillWithFluid(BlockView var1, BlockPos var2, BlockState var3, Fluid var4);

    public boolean tryFillWithFluid(IWorld var1, BlockPos var2, BlockState var3, FluidState var4);
}

