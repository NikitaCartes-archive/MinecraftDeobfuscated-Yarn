/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public interface WorldGenerationProgressListener {
    public void start(ChunkPos var1);

    public void setChunkStatus(ChunkPos var1, @Nullable ChunkStatus var2);

    public void start();

    public void stop();
}

