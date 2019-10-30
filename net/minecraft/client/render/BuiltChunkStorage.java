/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BuiltChunkStorage {
    protected final WorldRenderer worldRenderer;
    protected final World world;
    protected int sizeY;
    protected int sizeX;
    protected int sizeZ;
    public ChunkBuilder.BuiltChunk[] chunks;

    public BuiltChunkStorage(ChunkBuilder chunkBuilder, World world, int i, WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.world = world;
        this.setViewDistance(i);
        this.createChunks(chunkBuilder);
    }

    protected void createChunks(ChunkBuilder chunkBuilder) {
        int i = this.sizeX * this.sizeY * this.sizeZ;
        this.chunks = new ChunkBuilder.BuiltChunk[i];
        for (int j = 0; j < this.sizeX; ++j) {
            for (int k = 0; k < this.sizeY; ++k) {
                for (int l = 0; l < this.sizeZ; ++l) {
                    int m = this.getChunkIndex(j, k, l);
                    this.chunks[m] = new ChunkBuilder.BuiltChunk(chunkBuilder);
                    this.chunks[m].setOrigin(j * 16, k * 16, l * 16);
                }
            }
        }
    }

    public void clear() {
        for (ChunkBuilder.BuiltChunk builtChunk : this.chunks) {
            builtChunk.delete();
        }
    }

    private int getChunkIndex(int i, int j, int k) {
        return (k * this.sizeY + j) * this.sizeX + i;
    }

    protected void setViewDistance(int i) {
        int j;
        this.sizeX = j = i * 2 + 1;
        this.sizeY = 16;
        this.sizeZ = j;
    }

    public void updateCameraPosition(double d, double e) {
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        for (int k = 0; k < this.sizeX; ++k) {
            int l = this.sizeX * 16;
            int m = i - 8 - l / 2;
            int n = m + Math.floorMod(k * 16 - m, l);
            for (int o = 0; o < this.sizeZ; ++o) {
                int p = this.sizeZ * 16;
                int q = j - 8 - p / 2;
                int r = q + Math.floorMod(o * 16 - q, p);
                for (int s = 0; s < this.sizeY; ++s) {
                    int t = s * 16;
                    ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(k, s, o)];
                    builtChunk.setOrigin(n, t, r);
                }
            }
        }
    }

    public void scheduleRebuild(int i, int j, int k, boolean bl) {
        int l = Math.floorMod(i, this.sizeX);
        int m = Math.floorMod(j, this.sizeY);
        int n = Math.floorMod(k, this.sizeZ);
        ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(l, m, n)];
        builtChunk.scheduleRebuild(bl);
    }

    @Nullable
    protected ChunkBuilder.BuiltChunk getRenderedChunk(BlockPos blockPos) {
        int i = MathHelper.floorDiv(blockPos.getX(), 16);
        int j = MathHelper.floorDiv(blockPos.getY(), 16);
        int k = MathHelper.floorDiv(blockPos.getZ(), 16);
        if (j < 0 || j >= this.sizeY) {
            return null;
        }
        i = MathHelper.floorMod(i, this.sizeX);
        k = MathHelper.floorMod(k, this.sizeZ);
        return this.chunks[this.getChunkIndex(i, j, k)];
    }
}

