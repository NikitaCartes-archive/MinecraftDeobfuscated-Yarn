/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
    public void checkBlock(BlockPos var1);

    public void addLightSource(BlockPos var1, int var2);

    public boolean hasUpdates();

    public int doLightUpdates(int var1, boolean var2, boolean var3);

    default public void setSectionStatus(BlockPos pos, boolean notReady) {
        this.setSectionStatus(ChunkSectionPos.from(pos), notReady);
    }

    public void setSectionStatus(ChunkSectionPos var1, boolean var2);

    public void setColumnEnabled(ChunkPos var1, boolean var2);
}

