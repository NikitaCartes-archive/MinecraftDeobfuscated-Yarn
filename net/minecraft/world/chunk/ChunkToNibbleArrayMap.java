/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.chunk.ChunkNibbleArray;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkToNibbleArrayMap<M extends ChunkToNibbleArrayMap<M>> {
    private final long[] cachePositions = new long[2];
    private final ChunkNibbleArray[] cacheArrays = new ChunkNibbleArray[2];
    private boolean cacheEnabled;
    protected final Long2ObjectOpenHashMap<ChunkNibbleArray> arrays;

    protected ChunkToNibbleArrayMap(Long2ObjectOpenHashMap<ChunkNibbleArray> arrays) {
        this.arrays = arrays;
        this.clearCache();
        this.cacheEnabled = true;
    }

    public abstract M copy();

    public void replaceWithCopy(long pos) {
        this.arrays.put(pos, this.arrays.get(pos).copy());
        this.clearCache();
    }

    public boolean containsKey(long chunkPos) {
        return this.arrays.containsKey(chunkPos);
    }

    @Nullable
    public ChunkNibbleArray get(long chunkPos) {
        ChunkNibbleArray chunkNibbleArray;
        if (this.cacheEnabled) {
            for (int i = 0; i < 2; ++i) {
                if (chunkPos != this.cachePositions[i]) continue;
                return this.cacheArrays[i];
            }
        }
        if ((chunkNibbleArray = this.arrays.get(chunkPos)) != null) {
            if (this.cacheEnabled) {
                for (int j = 1; j > 0; --j) {
                    this.cachePositions[j] = this.cachePositions[j - 1];
                    this.cacheArrays[j] = this.cacheArrays[j - 1];
                }
                this.cachePositions[0] = chunkPos;
                this.cacheArrays[0] = chunkNibbleArray;
            }
            return chunkNibbleArray;
        }
        return null;
    }

    @Nullable
    public ChunkNibbleArray removeChunk(long chunkPos) {
        return this.arrays.remove(chunkPos);
    }

    public void put(long pos, ChunkNibbleArray data) {
        this.arrays.put(pos, data);
    }

    public void clearCache() {
        for (int i = 0; i < 2; ++i) {
            this.cachePositions[i] = Long.MAX_VALUE;
            this.cacheArrays[i] = null;
        }
    }

    public void disableCache() {
        this.cacheEnabled = false;
    }
}

