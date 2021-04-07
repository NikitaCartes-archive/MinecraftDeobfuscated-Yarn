/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class WoodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public WoodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        int o = i & 0xF;
        int p = j & 0xF;
        BlockState blockState3 = WHITE_TERRACOTTA;
        SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
        BlockState blockState4 = surfaceConfig.getUnderMaterial();
        BlockState blockState5 = surfaceConfig.getTopMaterial();
        BlockState blockState6 = blockState4;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
        int r = -1;
        boolean bl2 = false;
        int s = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int t = k; t >= m; --t) {
            if (s >= 15) continue;
            mutable.set(o, t, p);
            BlockState blockState7 = chunk.getBlockState(mutable);
            if (blockState7.isAir()) {
                r = -1;
                continue;
            }
            if (!blockState7.isOf(blockState.getBlock())) continue;
            if (r == -1) {
                bl2 = false;
                if (q <= 0) {
                    blockState3 = Blocks.AIR.getDefaultState();
                    blockState6 = blockState;
                } else if (t >= l - 4 && t <= l + 1) {
                    blockState3 = WHITE_TERRACOTTA;
                    blockState6 = blockState4;
                }
                if (t < l && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                r = q + Math.max(0, t - l);
                if (t >= l - 1) {
                    if (t > 86 + q * 2) {
                        if (bl) {
                            chunk.setBlockState(mutable, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                            chunk.setBlockState(mutable, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                    } else if (t > l + 3 + q) {
                        BlockState blockState8 = t < 64 || t > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.calculateLayerBlockState(i, t, j));
                        chunk.setBlockState(mutable, blockState8, false);
                    } else {
                        chunk.setBlockState(mutable, blockState5, false);
                        bl2 = true;
                    }
                } else {
                    chunk.setBlockState(mutable, blockState6, false);
                    if (blockState6 == WHITE_TERRACOTTA) {
                        chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (r > 0) {
                --r;
                if (bl2) {
                    chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                } else {
                    chunk.setBlockState(mutable, this.calculateLayerBlockState(i, t, j), false);
                }
            }
            ++s;
        }
    }
}

