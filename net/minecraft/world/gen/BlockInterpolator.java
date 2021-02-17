/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public interface BlockInterpolator {
    public BlockState sample(int var1, int var2, int var3, ChunkGeneratorSettings var4);
}

