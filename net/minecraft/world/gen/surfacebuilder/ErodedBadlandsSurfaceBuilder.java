/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ErodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public ErodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        double e = 0.0;
        double f = Math.min(Math.abs(d), this.heightCutoffNoise.sample((double)i * 0.25, (double)j * 0.25, false) * 15.0);
        if (f > 0.0) {
            double g = 0.001953125;
            e = f * f * 2.5;
            double h = Math.abs(this.heightNoise.sample((double)i * 0.001953125, (double)j * 0.001953125, false));
            double n = Math.ceil(h * 50.0) + 14.0;
            if (e > n) {
                e = n;
            }
            e += 64.0;
        }
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
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int s = Math.max(k, (int)e + 1); s >= 0; --s) {
            BlockState blockState7;
            mutable.set(o, s, p);
            if (chunk.getBlockState(mutable).isAir() && s < (int)e) {
                chunk.setBlockState(mutable, blockState, false);
            }
            if ((blockState7 = chunk.getBlockState(mutable)).isAir()) {
                r = -1;
                continue;
            }
            if (!blockState7.isOf(blockState.getBlock())) continue;
            if (r == -1) {
                bl2 = false;
                if (q <= 0) {
                    blockState3 = Blocks.AIR.getDefaultState();
                    blockState6 = blockState;
                } else if (s >= l - 4 && s <= l + 1) {
                    blockState3 = WHITE_TERRACOTTA;
                    blockState6 = blockState4;
                }
                if (s < l && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                r = q + Math.max(0, s - l);
                if (s >= l - 1) {
                    if (s > l + 3 + q) {
                        BlockState blockState8 = s < 64 || s > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.calculateLayerBlockState(i, s, j));
                        chunk.setBlockState(mutable, blockState8, false);
                        continue;
                    }
                    chunk.setBlockState(mutable, blockState5, false);
                    bl2 = true;
                    continue;
                }
                chunk.setBlockState(mutable, blockState6, false);
                Block block = blockState6.getBlock();
                if (block != Blocks.WHITE_TERRACOTTA && block != Blocks.ORANGE_TERRACOTTA && block != Blocks.MAGENTA_TERRACOTTA && block != Blocks.LIGHT_BLUE_TERRACOTTA && block != Blocks.YELLOW_TERRACOTTA && block != Blocks.LIME_TERRACOTTA && block != Blocks.PINK_TERRACOTTA && block != Blocks.GRAY_TERRACOTTA && block != Blocks.LIGHT_GRAY_TERRACOTTA && block != Blocks.CYAN_TERRACOTTA && block != Blocks.PURPLE_TERRACOTTA && block != Blocks.BLUE_TERRACOTTA && block != Blocks.BROWN_TERRACOTTA && block != Blocks.GREEN_TERRACOTTA && block != Blocks.RED_TERRACOTTA && block != Blocks.BLACK_TERRACOTTA) continue;
                chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            if (r <= 0) continue;
            --r;
            if (bl2) {
                chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            chunk.setBlockState(mutable, this.calculateLayerBlockState(i, s, j), false);
        }
    }
}

