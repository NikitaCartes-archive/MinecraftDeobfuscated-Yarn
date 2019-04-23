/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class FloatingIslandsChunkGeneratorConfig
extends ChunkGeneratorConfig {
    private BlockPos center;

    public FloatingIslandsChunkGeneratorConfig withCenter(BlockPos blockPos) {
        this.center = blockPos;
        return this;
    }

    public BlockPos getCenter() {
        return this.center;
    }
}

