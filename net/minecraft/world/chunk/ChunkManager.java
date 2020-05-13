/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkManager
implements ChunkProvider,
AutoCloseable {
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ, boolean create) {
        return (WorldChunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, create);
    }

    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        return this.getWorldChunk(chunkX, chunkZ, false);
    }

    @Override
    @Nullable
    public BlockView getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.getChunk(x, z, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract String getDebugString();

    @Override
    public void close() throws IOException {
    }

    public abstract LightingProvider getLightingProvider();

    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
    }

    public void setChunkForced(ChunkPos pos, boolean forced) {
    }

    public boolean shouldTickEntity(Entity entity) {
        return true;
    }

    public boolean shouldTickChunk(ChunkPos pos) {
        return true;
    }

    public boolean shouldTickBlock(BlockPos pos) {
        return true;
    }
}

