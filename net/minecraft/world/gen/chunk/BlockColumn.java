/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;

public interface BlockColumn {
    public BlockState getState(int var1);

    public void setState(int var1, BlockState var2);
}

