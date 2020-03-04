/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OverworldChunkGeneratorConfig
extends ChunkGeneratorConfig {
    private final int field_13224 = 4;
    private final int field_13223 = 4;
    private final int forcedBiome = -1;
    private final int field_13221 = 63;

    public int getBiomeSize() {
        return 4;
    }

    public int getRiverSize() {
        return 4;
    }

    public int getForcedBiome() {
        return -1;
    }

    @Override
    public int getBedrockFloorY() {
        return 0;
    }
}

