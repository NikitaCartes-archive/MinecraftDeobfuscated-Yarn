/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CavesChunkGeneratorConfig
extends ChunkGeneratorConfig {
    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return 127;
    }
}

