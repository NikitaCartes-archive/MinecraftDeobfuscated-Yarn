/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

public interface BlockPointer
extends Position {
    @Override
    public double getX();

    @Override
    public double getY();

    @Override
    public double getZ();

    public BlockPos getBlockPos();

    public BlockState getBlockState();

    public <T extends BlockEntity> T getBlockEntity();

    public ServerWorld getWorld();
}

