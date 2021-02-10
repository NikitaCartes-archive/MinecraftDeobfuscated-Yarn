/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.ChunkDataList;

public interface ChunkDataAccess<T>
extends AutoCloseable {
    public CompletableFuture<ChunkDataList<T>> readChunkData(ChunkPos var1);

    public void writeChunkData(ChunkDataList<T> var1);

    public void awaitAll();

    @Override
    default public void close() throws IOException {
    }
}

