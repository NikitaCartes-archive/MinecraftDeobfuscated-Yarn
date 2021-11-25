/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
public class class_6850 {
    private final Long2ObjectMap<class_6851> field_36314 = new Long2ObjectOpenHashMap<class_6851>();

    @Nullable
    public ChunkRendererRegion method_39969(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
        int o;
        int j = ChunkSectionPos.getSectionCoord(blockPos.getX() - i);
        int k = ChunkSectionPos.getSectionCoord(blockPos.getZ() - i);
        int l2 = ChunkSectionPos.getSectionCoord(blockPos2.getX() + i);
        int m = ChunkSectionPos.getSectionCoord(blockPos2.getZ() + i);
        class_6851[][] lvs = new class_6851[l2 - j + 1][m - k + 1];
        for (int n = j; n <= l2; ++n) {
            for (o = k; o <= m; ++o) {
                lvs[n - j][o - k] = this.field_36314.computeIfAbsent(ChunkPos.toLong(n, o), l -> new class_6851(world.getChunk(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l))));
            }
        }
        if (class_6850.method_39970(blockPos, blockPos2, j, k, lvs)) {
            return null;
        }
        RenderedChunk[][] renderedChunks = new RenderedChunk[l2 - j + 1][m - k + 1];
        for (o = j; o <= l2; ++o) {
            for (int p = k; p <= m; ++p) {
                renderedChunks[o - j][p - k] = lvs[o - j][p - k].method_39972();
            }
        }
        return new ChunkRendererRegion(world, j, k, renderedChunks);
    }

    private static boolean method_39970(BlockPos blockPos, BlockPos blockPos2, int i, int j, class_6851[][] args) {
        int k = ChunkSectionPos.getSectionCoord(blockPos.getX());
        int l = ChunkSectionPos.getSectionCoord(blockPos.getZ());
        int m = ChunkSectionPos.getSectionCoord(blockPos2.getX());
        int n = ChunkSectionPos.getSectionCoord(blockPos2.getZ());
        for (int o = k; o <= m; ++o) {
            for (int p = l; p <= n; ++p) {
                WorldChunk worldChunk = args[o - i][p - j].method_39971();
                if (worldChunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) continue;
                return false;
            }
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static final class class_6851 {
        private final WorldChunk field_36315;
        @Nullable
        private RenderedChunk field_36316;

        class_6851(WorldChunk worldChunk) {
            this.field_36315 = worldChunk;
        }

        public WorldChunk method_39971() {
            return this.field_36315;
        }

        public RenderedChunk method_39972() {
            if (this.field_36316 == null) {
                this.field_36316 = new RenderedChunk(this.field_36315);
            }
            return this.field_36316;
        }
    }
}

