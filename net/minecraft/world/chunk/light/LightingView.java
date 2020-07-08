/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
    default public void setSectionStatus(BlockPos pos, boolean notReady) {
        this.setSectionStatus(ChunkSectionPos.from(pos), notReady);
    }

    public void setSectionStatus(ChunkSectionPos var1, boolean var2);
}

