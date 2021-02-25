/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.jetbrains.annotations.Nullable;

public class ChunkSectionCache
implements AutoCloseable {
    private final WorldAccess world;
    private final Long2ObjectMap<ChunkSection> cache = new Long2ObjectOpenHashMap<ChunkSection>();
    @Nullable
    private ChunkSection cachedSection;
    private long sectionPos;

    public ChunkSectionCache(WorldAccess world) {
        this.world = world;
    }

    public ChunkSection getSection(BlockPos pos) {
        long l = ChunkSectionPos.toLong(pos);
        if (this.cachedSection != null && this.sectionPos == l) {
            return this.cachedSection;
        }
        this.cachedSection = this.cache.computeIfAbsent(l, cachedPos -> {
            Chunk chunk = this.world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
            ChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(pos.getY()));
            chunkSection.lock();
            return chunkSection;
        });
        this.sectionPos = l;
        return this.cachedSection;
    }

    public BlockState getBlockState(BlockPos pos) {
        ChunkSection chunkSection = this.getSection(pos);
        int i = ChunkSectionPos.getLocalCoord(pos.getX());
        int j = ChunkSectionPos.getLocalCoord(pos.getY());
        int k = ChunkSectionPos.getLocalCoord(pos.getZ());
        return chunkSection.getBlockState(i, j, k);
    }

    @Override
    public void close() {
        for (ChunkSection chunkSection : this.cache.values()) {
            chunkSection.unlock();
        }
    }
}

