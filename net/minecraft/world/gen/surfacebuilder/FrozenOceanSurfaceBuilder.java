/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.WorldGenRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class FrozenOceanSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
    protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState ICE = Blocks.ICE.getDefaultState();
    private OctaveSimplexNoiseSampler icebergNoise;
    private OctaveSimplexNoiseSampler icebergCutoffNoise;
    private long seed;

    public FrozenOceanSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        double e = 0.0;
        double f = 0.0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        float g = biome.getTemperature(mutable.set(i, 63, j));
        double h = Math.min(Math.abs(d), this.icebergNoise.sample((double)i * 0.1, (double)j * 0.1, false) * 15.0);
        if (h > 1.8) {
            double o = 0.09765625;
            e = h * h * 1.2;
            double p = Math.abs(this.icebergCutoffNoise.sample((double)i * 0.09765625, (double)j * 0.09765625, false));
            double q = Math.ceil(p * 40.0) + 14.0;
            if (e > q) {
                e = q;
            }
            if (g > 0.1f) {
                e -= 2.0;
            }
            if (e > 2.0) {
                f = (double)l - e - 7.0;
                e += (double)l;
            } else {
                e = 0.0;
            }
        }
        int r = i & 0xF;
        int s = j & 0xF;
        SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
        BlockState blockState3 = surfaceConfig.getUnderMaterial();
        BlockState blockState4 = surfaceConfig.getTopMaterial();
        BlockState blockState5 = blockState3;
        BlockState blockState6 = blockState4;
        int t = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int u = -1;
        int v = 0;
        int w = 2 + random.nextInt(4);
        int x = l + 18 + random.nextInt(10);
        for (int y = Math.max(k, (int)e + 1); y >= m; --y) {
            mutable.set(r, y, s);
            if (chunk.getBlockState(mutable).isAir() && y < (int)e && random.nextDouble() > 0.01) {
                chunk.setBlockState(mutable, PACKED_ICE, false);
            } else if (chunk.getBlockState(mutable).getMaterial() == Material.WATER && y > (int)f && y < l && f != 0.0 && random.nextDouble() > 0.15) {
                chunk.setBlockState(mutable, PACKED_ICE, false);
            }
            BlockState blockState7 = chunk.getBlockState(mutable);
            if (blockState7.isAir()) {
                u = -1;
                continue;
            }
            if (blockState7.isOf(blockState.getBlock())) {
                if (u == -1) {
                    if (t <= 0) {
                        blockState6 = AIR;
                        blockState5 = blockState;
                    } else if (y >= l - 4 && y <= l + 1) {
                        blockState6 = blockState4;
                        blockState5 = blockState3;
                    }
                    if (y < l && (blockState6 == null || blockState6.isAir())) {
                        blockState6 = biome.getTemperature(mutable.set(i, y, j)) < 0.15f ? ICE : blockState2;
                    }
                    u = t;
                    if (y >= l - 1) {
                        chunk.setBlockState(mutable, blockState6, false);
                        continue;
                    }
                    if (y < l - 7 - t) {
                        blockState6 = AIR;
                        blockState5 = blockState;
                        chunk.setBlockState(mutable, GRAVEL, false);
                        continue;
                    }
                    chunk.setBlockState(mutable, blockState5, false);
                    continue;
                }
                if (u <= 0) continue;
                chunk.setBlockState(mutable, blockState5, false);
                if (--u != 0 || !blockState5.isOf(Blocks.SAND) || t <= 1) continue;
                u = random.nextInt(4) + Math.max(0, y - 63);
                blockState5 = blockState5.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                continue;
            }
            if (!blockState7.isOf(Blocks.PACKED_ICE) || v > w || y <= x) continue;
            chunk.setBlockState(mutable, SNOW_BLOCK, false);
            ++v;
        }
    }

    @Override
    public void initSeed(long seed) {
        if (this.seed != seed || this.icebergNoise == null || this.icebergCutoffNoise == null) {
            ChunkRandom chunkRandom = new ChunkRandom(seed);
            this.icebergNoise = new OctaveSimplexNoiseSampler((WorldGenRandom)chunkRandom, IntStream.rangeClosed(-3, 0));
            this.icebergCutoffNoise = new OctaveSimplexNoiseSampler((WorldGenRandom)chunkRandom, ImmutableList.of(Integer.valueOf(0)));
        }
        this.seed = seed;
    }
}

