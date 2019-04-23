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
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class ChunkCache
implements ExtendedBlockView {
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
        this.empty = true;
        for (k = this.minX; k <= i; ++k) {
            for (l = this.minZ; l <= j; ++l) {
                this.chunks[k - this.minX][l - this.minZ] = world.getChunk(k, l, ChunkStatus.FULL, false);
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

    @Nullable
    private Chunk method_18474(BlockPos blockPos) {
        int i = (blockPos.getX() >> 4) - this.minX;
        int j = (blockPos.getZ() >> 4) - this.minZ;
        if (i < 0 || i >= this.chunks.length || j < 0 || j >= this.chunks[i].length) {
            return null;
        }
        return this.chunks[i][j];
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        Chunk chunk = this.method_18474(blockPos);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        Chunk chunk = this.method_18474(blockPos);
        if (chunk != null) {
            return chunk.getBlockState(blockPos);
        }
        return Blocks.BEDROCK.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        if (World.isHeightInvalid(blockPos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        Chunk chunk = this.method_18474(blockPos);
        if (chunk != null) {
            return chunk.getFluidState(blockPos);
        }
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public Biome getBiome(BlockPos blockPos) {
        Chunk chunk = this.method_18474(blockPos);
        if (chunk == null) {
            return Biomes.PLAINS;
        }
        return chunk.getBiome(blockPos);
    }

    @Override
    public int getLightLevel(LightType lightType, BlockPos blockPos) {
        return this.world.getLightLevel(lightType, blockPos);
    }
}

