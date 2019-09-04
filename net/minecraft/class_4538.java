/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.class_4543;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import org.jetbrains.annotations.Nullable;

public interface class_4538
extends ExtendedBlockView,
ViewableWorld,
class_4543.class_4544 {
    @Nullable
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean isChunkLoaded(int var1, int var2);

    public int getLightLevel(Heightmap.Type var1, int var2, int var3);

    public int getAmbientDarkness();

    @Override
    default public Biome getBiome(int i, int j, int k) {
        Chunk chunk = this.getChunk(i >> 2, k >> 2, ChunkStatus.BIOMES, false);
        if (chunk != null && chunk.getBiomeArray() != null) {
            return chunk.getBiomeArray().getBiome(i, j, k);
        }
        return this.method_22387(i, j, k);
    }

    public Biome method_22387(int var1, int var2, int var3);

    public boolean isClient();

    public int getSeaLevel();

    public Dimension getDimension();

    default public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getLightLevel(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }

    default public boolean method_22347(BlockPos blockPos) {
        return this.getBlockState(blockPos).isAir();
    }

    default public boolean method_22348(BlockPos blockPos) {
        if (blockPos.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(blockPos);
        }
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        if (!this.isSkyVisible(blockPos2)) {
            return false;
        }
        blockPos2 = blockPos2.down();
        while (blockPos2.getY() > blockPos.getY()) {
            BlockState blockState = this.getBlockState(blockPos2);
            if (blockState.getLightSubtracted(this, blockPos2) > 0 && !blockState.getMaterial().isLiquid()) {
                return false;
            }
            blockPos2 = blockPos2.down();
        }
        return true;
    }

    default public float method_22349(BlockPos blockPos) {
        return this.getDimension().getLightLevelToBrightness()[this.method_22339(blockPos)];
    }

    default public int method_22344(BlockPos blockPos, Direction direction) {
        return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
    }

    default public Chunk method_22350(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    default public Chunk getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, true);
    }

    default public Chunk method_22342(int i, int j, ChunkStatus chunkStatus) {
        return this.getChunk(i, j, chunkStatus, true);
    }

    @Override
    @Nullable
    default public BlockView method_22338(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    default public boolean method_22351(BlockPos blockPos) {
        return this.getFluidState(blockPos).matches(FluidTags.WATER);
    }

    default public boolean method_22345(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        BlockState blockState = this.getBlockState(pooledMutable.method_10113(o, p, q));
                        if (blockState.getFluidState().isEmpty()) continue;
                        boolean bl = true;
                        return bl;
                    }
                }
            }
        }
        return false;
    }

    default public int method_22339(BlockPos blockPos) {
        return this.method_22346(blockPos, this.getAmbientDarkness());
    }

    default public int method_22346(BlockPos blockPos, int i) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.method_22335(blockPos, i);
    }

    @Deprecated
    default public boolean method_22340(BlockPos blockPos) {
        return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Deprecated
    default public boolean method_22343(BlockPos blockPos, BlockPos blockPos2) {
        return this.method_22341(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }

    @Deprecated
    default public boolean method_22341(int i, int j, int k, int l, int m, int n) {
        if (m < 0 || j >= 256) {
            return false;
        }
        k >>= 4;
        l >>= 4;
        n >>= 4;
        for (int o = i >>= 4; o <= l; ++o) {
            for (int p = k; p <= n; ++p) {
                if (this.isChunkLoaded(o, p)) continue;
                return false;
            }
        }
        return true;
    }
}

