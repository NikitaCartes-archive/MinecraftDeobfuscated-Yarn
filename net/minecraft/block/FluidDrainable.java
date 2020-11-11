/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface FluidDrainable {
    public ItemStack tryDrainFluid(WorldAccess var1, BlockPos var2, BlockState var3);

    public Optional<SoundEvent> getDrainSound();
}

