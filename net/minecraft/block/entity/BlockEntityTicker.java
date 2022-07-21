/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A functional interface that ticks a block entity. This is usually implemented
 * as a static method in the block entity's class.
 * 
 * @see net.minecraft.block.BlockEntityProvider#getTicker
 */
@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
    /**
     * Ticks the block entity.
     */
    public void tick(World var1, BlockPos var2, BlockState var3, T var4);
}

