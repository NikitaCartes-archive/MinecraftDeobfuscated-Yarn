/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.util.math.BlockPos;

public interface BlockEntityTickInvoker {
    public void tick();

    public boolean isRemoved();

    public BlockPos getPos();

    public String getName();
}

