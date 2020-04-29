/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BadlandsSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private static final BlockState WHITE_TERACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERACOTTA = Blocks.TERRACOTTA.getDefaultState();
    private static final BlockState YELLOW_TERACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
    private static final BlockState BROWN_TERACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
    private static final BlockState RED_TERACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
    private static final BlockState LIGHT_GRAY_TERACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
    protected BlockState[] layerBlocks;
    protected long seed;
    protected OctaveSimplexNoiseSampler heightCutoffNoise;
    protected OctaveSimplexNoiseSampler heightNoise;
    protected OctaveSimplexNoiseSampler layerNoise;

    public BadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        int n = i & 0xF;
        int o = j & 0xF;
        BlockState blockState3 = WHITE_TERACOTTA;
        BlockState blockState4 = biome.getSurfaceConfig().getUnderMaterial();
        int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int q = -1;
        boolean bl2 = false;
        int r = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int s = k; s >= 0; --s) {
            if (r >= 15) continue;
            mutable.set(n, s, o);
            BlockState blockState5 = chunk.getBlockState(mutable);
            if (blockState5.isAir()) {
                q = -1;
                continue;
            }
            if (!blockState5.isOf(blockState.getBlock())) continue;
            if (q == -1) {
                bl2 = false;
                if (p <= 0) {
                    blockState3 = Blocks.AIR.getDefaultState();
                    blockState4 = blockState;
                } else if (s >= l - 4 && s <= l + 1) {
                    blockState3 = WHITE_TERACOTTA;
                    blockState4 = biome.getSurfaceConfig().getUnderMaterial();
                }
                if (s < l && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                q = p + Math.max(0, s - l);
                if (s >= l - 1) {
                    if (s > l + 3 + p) {
                        BlockState blockState6 = s < 64 || s > 127 ? ORANGE_TERRACOTTA : (bl ? TERACOTTA : this.calculateLayerBlockState(i, s, j));
                        chunk.setBlockState(mutable, blockState6, false);
                    } else {
                        chunk.setBlockState(mutable, biome.getSurfaceConfig().getTopMaterial(), false);
                        bl2 = true;
                    }
                } else {
                    chunk.setBlockState(mutable, blockState4, false);
                    Block block = blockState4.getBlock();
                    if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                        chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (q > 0) {
                --q;
                if (bl2) {
                    chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                } else {
                    chunk.setBlockState(mutable, this.calculateLayerBlockState(i, s, j), false);
                }
            }
            ++r;
        }
    }

    @Override
    public void initSeed(long seed) {
        if (this.seed != seed || this.layerBlocks == null) {
            this.initLayerBlocks(seed);
        }
        if (this.seed != seed || this.heightCutoffNoise == null || this.heightNoise == null) {
            ChunkRandom chunkRandom = new ChunkRandom(seed);
            this.heightCutoffNoise = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0));
            this.heightNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(Integer.valueOf(0)));
        }
        this.seed = seed;
    }

    protected void initLayerBlocks(long seed) {
        int o;
        int n;
        int m;
        int l;
        int k;
        int j;
        int i;
        this.layerBlocks = new BlockState[64];
        Arrays.fill(this.layerBlocks, TERACOTTA);
        ChunkRandom chunkRandom = new ChunkRandom(seed);
        this.layerNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(Integer.valueOf(0)));
        for (i = 0; i < 64; ++i) {
            if ((i += chunkRandom.nextInt(5) + 1) >= 64) continue;
            this.layerBlocks[i] = ORANGE_TERRACOTTA;
        }
        i = chunkRandom.nextInt(4) + 2;
        for (j = 0; j < i; ++j) {
            k = chunkRandom.nextInt(3) + 1;
            l = chunkRandom.nextInt(64);
            for (m = 0; l + m < 64 && m < k; ++m) {
                this.layerBlocks[l + m] = YELLOW_TERACOTTA;
            }
        }
        j = chunkRandom.nextInt(4) + 2;
        for (k = 0; k < j; ++k) {
            l = chunkRandom.nextInt(3) + 2;
            m = chunkRandom.nextInt(64);
            for (n = 0; m + n < 64 && n < l; ++n) {
                this.layerBlocks[m + n] = BROWN_TERACOTTA;
            }
        }
        k = chunkRandom.nextInt(4) + 2;
        for (l = 0; l < k; ++l) {
            m = chunkRandom.nextInt(3) + 1;
            n = chunkRandom.nextInt(64);
            for (o = 0; n + o < 64 && o < m; ++o) {
                this.layerBlocks[n + o] = RED_TERACOTTA;
            }
        }
        l = chunkRandom.nextInt(3) + 3;
        m = 0;
        for (n = 0; n < l; ++n) {
            o = 1;
            m += chunkRandom.nextInt(16) + 4;
            for (int p = 0; m + p < 64 && p < 1; ++p) {
                this.layerBlocks[m + p] = WHITE_TERACOTTA;
                if (m + p > 1 && chunkRandom.nextBoolean()) {
                    this.layerBlocks[m + p - 1] = LIGHT_GRAY_TERACOTTA;
                }
                if (m + p >= 63 || !chunkRandom.nextBoolean()) continue;
                this.layerBlocks[m + p + 1] = LIGHT_GRAY_TERACOTTA;
            }
        }
    }

    protected BlockState calculateLayerBlockState(int x, int y, int z) {
        int i = (int)Math.round(this.layerNoise.sample((double)x / 512.0, (double)z / 512.0, false) * 2.0);
        return this.layerBlocks[(y + i + 64) % 64];
    }
}

