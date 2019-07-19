/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkManager
implements ChunkProvider,
AutoCloseable {
    @Nullable
    public WorldChunk getWorldChunk(int i, int j, boolean bl) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL, bl);
    }

    @Nullable
    public WorldChunk method_21730(int i, int j) {
        return this.getWorldChunk(i, j, false);
    }

    @Override
    @Nullable
    public BlockView getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Environment(value=EnvType.CLIENT)
    public abstract void tick(BooleanSupplier var1);

    public abstract String getDebugString();

    public abstract ChunkGenerator<?> getChunkGenerator();

    @Override
    public void close() throws IOException {
    }

    public abstract LightingProvider getLightingProvider();

    public void setMobSpawnOptions(boolean bl, boolean bl2) {
    }

    public void setChunkForced(ChunkPos chunkPos, boolean bl) {
    }

    public boolean shouldTickEntity(Entity entity) {
        return true;
    }

    public boolean shouldTickChunk(ChunkPos chunkPos) {
        return true;
    }

    public boolean shouldTickBlock(BlockPos blockPos) {
        return true;
    }
}

