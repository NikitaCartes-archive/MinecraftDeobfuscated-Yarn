/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class LakeFeature
extends Feature<SingleStateFeatureConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public LakeFeature(Function<Dynamic<?>, ? extends SingleStateFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SingleStateFeatureConfig singleStateFeatureConfig) {
        int t;
        int j;
        while (blockPos.getY() > 5 && iWorld.isAir(blockPos)) {
            blockPos = blockPos.down();
        }
        if (blockPos.getY() <= 4) {
            return false;
        }
        blockPos = blockPos.down(4);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!iWorld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getName()).isEmpty()) {
            return false;
        }
        boolean[] bls = new boolean[2048];
        int i = random.nextInt(4) + 4;
        for (j = 0; j < i; ++j) {
            double d = random.nextDouble() * 6.0 + 3.0;
            double e = random.nextDouble() * 4.0 + 2.0;
            double f = random.nextDouble() * 6.0 + 3.0;
            double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
            double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
            double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;
            for (int l = 1; l < 15; ++l) {
                for (int m = 1; m < 15; ++m) {
                    for (int n = 1; n < 7; ++n) {
                        double o = ((double)l - g) / (d / 2.0);
                        double p = ((double)n - h) / (e / 2.0);
                        double q = ((double)m - k) / (f / 2.0);
                        double r = o * o + p * p + q * q;
                        if (!(r < 1.0)) continue;
                        bls[(l * 16 + m) * 8 + n] = true;
                    }
                }
            }
        }
        for (j = 0; j < 16; ++j) {
            for (int s = 0; s < 16; ++s) {
                for (t = 0; t < 8; ++t) {
                    boolean bl;
                    boolean bl2 = bl = !bls[(j * 16 + s) * 8 + t] && (j < 15 && bls[((j + 1) * 16 + s) * 8 + t] || j > 0 && bls[((j - 1) * 16 + s) * 8 + t] || s < 15 && bls[(j * 16 + s + 1) * 8 + t] || s > 0 && bls[(j * 16 + (s - 1)) * 8 + t] || t < 7 && bls[(j * 16 + s) * 8 + t + 1] || t > 0 && bls[(j * 16 + s) * 8 + (t - 1)]);
                    if (!bl) continue;
                    Material material = iWorld.getBlockState(blockPos.add(j, t, s)).getMaterial();
                    if (t >= 4 && material.isLiquid()) {
                        return false;
                    }
                    if (t >= 4 || material.isSolid() || iWorld.getBlockState(blockPos.add(j, t, s)) == singleStateFeatureConfig.state) continue;
                    return false;
                }
            }
        }
        for (j = 0; j < 16; ++j) {
            for (int s = 0; s < 16; ++s) {
                for (t = 0; t < 8; ++t) {
                    if (!bls[(j * 16 + s) * 8 + t]) continue;
                    iWorld.setBlockState(blockPos.add(j, t, s), t >= 4 ? CAVE_AIR : singleStateFeatureConfig.state, 2);
                }
            }
        }
        for (j = 0; j < 16; ++j) {
            for (int s = 0; s < 16; ++s) {
                for (t = 4; t < 8; ++t) {
                    BlockPos blockPos2;
                    if (!bls[(j * 16 + s) * 8 + t] || !LakeFeature.isDirt(iWorld.getBlockState(blockPos2 = blockPos.add(j, t - 1, s)).getBlock()) || iWorld.getLightLevel(LightType.SKY, blockPos.add(j, t, s)) <= 0) continue;
                    Biome biome = iWorld.getBiome(blockPos2);
                    if (biome.getSurfaceConfig().getTopMaterial().getBlock() == Blocks.MYCELIUM) {
                        iWorld.setBlockState(blockPos2, Blocks.MYCELIUM.getDefaultState(), 2);
                        continue;
                    }
                    iWorld.setBlockState(blockPos2, Blocks.GRASS_BLOCK.getDefaultState(), 2);
                }
            }
        }
        if (singleStateFeatureConfig.state.getMaterial() == Material.LAVA) {
            for (j = 0; j < 16; ++j) {
                for (int s = 0; s < 16; ++s) {
                    for (t = 0; t < 8; ++t) {
                        boolean bl;
                        boolean bl3 = bl = !bls[(j * 16 + s) * 8 + t] && (j < 15 && bls[((j + 1) * 16 + s) * 8 + t] || j > 0 && bls[((j - 1) * 16 + s) * 8 + t] || s < 15 && bls[(j * 16 + s + 1) * 8 + t] || s > 0 && bls[(j * 16 + (s - 1)) * 8 + t] || t < 7 && bls[(j * 16 + s) * 8 + t + 1] || t > 0 && bls[(j * 16 + s) * 8 + (t - 1)]);
                        if (!bl || t >= 4 && random.nextInt(2) == 0 || !iWorld.getBlockState(blockPos.add(j, t, s)).getMaterial().isSolid()) continue;
                        iWorld.setBlockState(blockPos.add(j, t, s), Blocks.STONE.getDefaultState(), 2);
                    }
                }
            }
        }
        if (singleStateFeatureConfig.state.getMaterial() == Material.WATER) {
            for (j = 0; j < 16; ++j) {
                for (int s = 0; s < 16; ++s) {
                    t = 4;
                    BlockPos blockPos2 = blockPos.add(j, 4, s);
                    if (!iWorld.getBiome(blockPos2).canSetIce(iWorld, blockPos2, false)) continue;
                    iWorld.setBlockState(blockPos2, Blocks.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

