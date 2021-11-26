/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegionBuilder {
    private final Long2ObjectMap<ClientChunk> chunks = new Long2ObjectOpenHashMap<ClientChunk>();

    @Nullable
    public ChunkRendererRegion build(World world, BlockPos startPos, BlockPos endPos, int offset) {
        int n;
        int i = ChunkSectionPos.getSectionCoord(startPos.getX() - offset);
        int j = ChunkSectionPos.getSectionCoord(startPos.getZ() - offset);
        int k = ChunkSectionPos.getSectionCoord(endPos.getX() + offset);
        int l = ChunkSectionPos.getSectionCoord(endPos.getZ() + offset);
        ClientChunk[][] clientChunks = new ClientChunk[k - i + 1][l - j + 1];
        for (int m = i; m <= k; ++m) {
            for (n = j; n <= l; ++n) {
                clientChunks[m - i][n - j] = this.chunks.computeIfAbsent(ChunkPos.toLong(m, n), pos -> new ClientChunk(world.getChunk(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos))));
            }
        }
        if (ChunkRendererRegionBuilder.isEmptyBetween(startPos, endPos, i, j, clientChunks)) {
            return null;
        }
        RenderedChunk[][] renderedChunks = new RenderedChunk[k - i + 1][l - j + 1];
        for (n = i; n <= k; ++n) {
            for (int o = j; o <= l; ++o) {
                renderedChunks[n - i][o - j] = clientChunks[n - i][o - j].getRenderedChunk();
            }
        }
        return new ChunkRendererRegion(world, i, j, renderedChunks);
    }

    private static boolean isEmptyBetween(BlockPos startPos, BlockPos endPos, int offsetX, int offsetZ, ClientChunk[][] chunks) {
        int i = ChunkSectionPos.getSectionCoord(startPos.getX());
        int j = ChunkSectionPos.getSectionCoord(startPos.getZ());
        int k = ChunkSectionPos.getSectionCoord(endPos.getX());
        int l = ChunkSectionPos.getSectionCoord(endPos.getZ());
        for (int m = i; m <= k; ++m) {
            for (int n = j; n <= l; ++n) {
                WorldChunk worldChunk = chunks[m - offsetX][n - offsetZ].getChunk();
                if (worldChunk.areSectionsEmptyBetween(startPos.getY(), endPos.getY())) continue;
                return false;
            }
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static final class ClientChunk {
        private final WorldChunk chunk;
        @Nullable
        private RenderedChunk renderedChunk;

        ClientChunk(WorldChunk chunk) {
            this.chunk = chunk;
        }

        public WorldChunk getChunk() {
            return this.chunk;
        }

        public RenderedChunk getRenderedChunk() {
            if (this.renderedChunk == null) {
                this.renderedChunk = new RenderedChunk(this.chunk);
            }
            return this.renderedChunk;
        }
    }
}

