/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface FluidDrainable {
    public Fluid tryDrainFluid(IWorld var1, BlockPos var2, BlockState var3);
}

