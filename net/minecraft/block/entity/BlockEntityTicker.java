/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
    /**
     * Runs this action on the given block entity. The world, block position, and block state are passed
     * as context.
     */
    public void tick(World var1, BlockPos var2, BlockState var3, T var4);
}

