/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;

public interface WorldView
extends BlockRenderView,
CollisionView,
BiomeAccess.Storage {
    @Nullable
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean isChunkLoaded(int var1, int var2);

    public int getTopY(Heightmap.Type var1, int var2, int var3);

    public int getAmbientDarkness();

    public BiomeAccess getBiomeAccess();

    default public Biome method_23753(BlockPos blockPos) {
        return this.getBiomeAccess().getBiome(blockPos);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    default public int method_23752(BlockPos blockPos, ColorResolver colorResolver) {
        return colorResolver.getColor(this.method_23753(blockPos), blockPos.getX(), blockPos.getZ());
    }

    @Override
    default public Biome getStoredBiome(int i, int j, int k) {
        Chunk chunk = this.getChunk(i >> 2, k >> 2, ChunkStatus.BIOMES, false);
        if (chunk != null && chunk.getBiomeArray() != null) {
            return chunk.getBiomeArray().getStoredBiome(i, j, k);
        }
        return this.getGeneratorStoredBiome(i, j, k);
    }

    public Biome getGeneratorStoredBiome(int var1, int var2, int var3);

    public boolean isClient();

    public int getSeaLevel();

    public Dimension getDimension();

    default public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getTopY(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }

    default public boolean isAir(BlockPos blockPos) {
        return this.getBlockState(blockPos).isAir();
    }

    default public boolean isSkyVisibleAllowingSea(BlockPos blockPos) {
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
            if (blockState.getOpacity(this, blockPos2) > 0 && !blockState.getMaterial().isLiquid()) {
                return false;
            }
            blockPos2 = blockPos2.down();
        }
        return true;
    }

    @Deprecated
    default public float getBrightness(BlockPos blockPos) {
        return this.getDimension().method_23759(this.getLightLevel(blockPos));
    }

    default public int getStrongRedstonePower(BlockPos blockPos, Direction direction) {
        return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
    }

    default public Chunk getChunk(BlockPos blockPos) {
        return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    default public Chunk getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, true);
    }

    default public Chunk getChunk(int i, int j, ChunkStatus chunkStatus) {
        return this.getChunk(i, j, chunkStatus, true);
    }

    @Override
    @Nullable
    default public BlockView getExistingChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    default public boolean isWater(BlockPos blockPos) {
        return this.getFluidState(blockPos).matches(FluidTags.WATER);
    }

    default public boolean containsFluid(Box box) {
        int i = MathHelper.floor(box.x1);
        int j = MathHelper.ceil(box.x2);
        int k = MathHelper.floor(box.y1);
        int l = MathHelper.ceil(box.y2);
        int m = MathHelper.floor(box.z1);
        int n = MathHelper.ceil(box.z2);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
                        if (blockState.getFluidState().isEmpty()) continue;
                        boolean bl = true;
                        return bl;
                    }
                }
            }
        }
        return false;
    }

    default public int getLightLevel(BlockPos blockPos) {
        return this.getLightLevel(blockPos, this.getAmbientDarkness());
    }

    default public int getLightLevel(BlockPos blockPos, int i) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.getBaseLightLevel(blockPos, i);
    }

    @Deprecated
    default public boolean isChunkLoaded(BlockPos blockPos) {
        return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Deprecated
    default public boolean isRegionLoaded(BlockPos blockPos, BlockPos blockPos2) {
        return this.isRegionLoaded(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }

    @Deprecated
    default public boolean isRegionLoaded(int i, int j, int k, int l, int m, int n) {
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

