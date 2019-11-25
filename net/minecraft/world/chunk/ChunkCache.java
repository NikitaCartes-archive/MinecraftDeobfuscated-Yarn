/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.EmptyChunk;
import org.jetbrains.annotations.Nullable;

public class ChunkCache
implements BlockView,
CollisionView {
    protected final int minX;
    protected final int minZ;
    protected final Chunk[][] chunks;
    protected boolean empty;
    protected final World world;

    public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2) {
        int l;
        int k;
        this.world = world;
        this.minX = blockPos.getX() >> 4;
        this.minZ = blockPos.getZ() >> 4;
        int i = blockPos2.getX() >> 4;
        int j = blockPos2.getZ() >> 4;
        this.chunks = new Chunk[i - this.minX + 1][j - this.minZ + 1];
        ChunkManager chunkManager = world.getChunkManager();
        this.empty = true;
        for (k = this.minX; k <= i; ++k) {
            for (l = this.minZ; l <= j; ++l) {
                this.chunks[k - this.minX][l - this.minZ] = chunkManager.getWorldChunk(k, l);
            }
        }
        for (k = blockPos.getX() >> 4; k <= blockPos2.getX() >> 4; ++k) {
            for (l = blockPos.getZ() >> 4; l <= blockPos2.getZ() >> 4; ++l) {
                Chunk chunk = this.chunks[k - this.minX][l - this.minZ];
                if (chunk == null || chunk.method_12228(blockPos.getY(), blockPos2.getY())) continue;
                this.empty = false;
                return;
            }
        }
    }

    private Chunk method_22354(BlockPos blockPos) {
        return this.method_22353(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    private Chunk method_22353(int i, int j) {
        int k = i - this.minX;
        int l = j - this.minZ;
        if (k < 0 || k >= this.chunks.length || l < 0 || l >= this.chunks[k].length) {
            return new EmptyChunk(this.world, new ChunkPos(i, j));
        }
        Chunk chunk = this.chunks[k][l];
        return chunk != null ? chunk : new EmptyChunk(this.world, new ChunkPos(i, j));
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public BlockView getExistingChunk(int i, int j) {
        return this.method_22353(i, j);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        Chunk chunk = this.method_22354(blockPos);
        return chunk.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        Chunk chunk = this.method_22354(blockPos);
        return chunk.getBlockState(blockPos);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        Chunk chunk = this.method_22354(blockPos);
        return chunk.getFluidState(blockPos);
    }
}

