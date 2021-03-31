/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LakeFeature
extends Feature<SingleStateFeatureConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public LakeFeature(Codec<SingleStateFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SingleStateFeatureConfig> context) {
        int t;
        int j;
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        SingleStateFeatureConfig singleStateFeatureConfig = context.getConfig();
        while (blockPos.getY() > structureWorldAccess.getBottomY() + 5 && structureWorldAccess.isAir(blockPos)) {
            blockPos = blockPos.down();
        }
        if (blockPos.getY() <= structureWorldAccess.getBottomY() + 4) {
            return false;
        }
        if (structureWorldAccess.getStructures(ChunkSectionPos.from(blockPos = blockPos.down(4)), StructureFeature.VILLAGE).findAny().isPresent()) {
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
                    Material material = structureWorldAccess.getBlockState(blockPos.add(j, t, s)).getMaterial();
                    if (t >= 4 && material.isLiquid()) {
                        return false;
                    }
                    if (t >= 4 || material.isSolid() || structureWorldAccess.getBlockState(blockPos.add(j, t, s)) == singleStateFeatureConfig.state) continue;
                    return false;
                }
            }
        }
        for (j = 0; j < 16; ++j) {
            for (int s = 0; s < 16; ++s) {
                for (t = 0; t < 8; ++t) {
                    if (!bls[(j * 16 + s) * 8 + t]) continue;
                    structureWorldAccess.setBlockState(blockPos.add(j, t, s), t >= 4 ? CAVE_AIR : singleStateFeatureConfig.state, Block.NOTIFY_LISTENERS);
                }
            }
        }
        for (j = 0; j < 16; ++j) {
            for (int s = 0; s < 16; ++s) {
                for (t = 4; t < 8; ++t) {
                    BlockPos blockPos2;
                    if (!bls[(j * 16 + s) * 8 + t] || !LakeFeature.isSoil(structureWorldAccess.getBlockState(blockPos2 = blockPos.add(j, t - 1, s))) || structureWorldAccess.getLightLevel(LightType.SKY, blockPos.add(j, t, s)) <= 0) continue;
                    Biome biome = structureWorldAccess.getBiome(blockPos2);
                    if (biome.getGenerationSettings().getSurfaceConfig().getTopMaterial().isOf(Blocks.MYCELIUM)) {
                        structureWorldAccess.setBlockState(blockPos2, Blocks.MYCELIUM.getDefaultState(), Block.NOTIFY_LISTENERS);
                        continue;
                    }
                    structureWorldAccess.setBlockState(blockPos2, Blocks.GRASS_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
        if (singleStateFeatureConfig.state.getMaterial() == Material.LAVA) {
            for (j = 0; j < 16; ++j) {
                for (int s = 0; s < 16; ++s) {
                    for (t = 0; t < 8; ++t) {
                        boolean bl;
                        boolean bl3 = bl = !bls[(j * 16 + s) * 8 + t] && (j < 15 && bls[((j + 1) * 16 + s) * 8 + t] || j > 0 && bls[((j - 1) * 16 + s) * 8 + t] || s < 15 && bls[(j * 16 + s + 1) * 8 + t] || s > 0 && bls[(j * 16 + (s - 1)) * 8 + t] || t < 7 && bls[(j * 16 + s) * 8 + t + 1] || t > 0 && bls[(j * 16 + s) * 8 + (t - 1)]);
                        if (!bl || t >= 4 && random.nextInt(2) == 0 || !structureWorldAccess.getBlockState(blockPos.add(j, t, s)).getMaterial().isSolid()) continue;
                        structureWorldAccess.setBlockState(blockPos.add(j, t, s), Blocks.STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
        if (singleStateFeatureConfig.state.getMaterial() == Material.WATER) {
            for (j = 0; j < 16; ++j) {
                for (int s = 0; s < 16; ++s) {
                    t = 4;
                    BlockPos blockPos2 = blockPos.add(j, 4, s);
                    if (!structureWorldAccess.getBiome(blockPos2).canSetIce(structureWorldAccess, blockPos2, false)) continue;
                    structureWorldAccess.setBlockState(blockPos2, Blocks.ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
        return true;
    }
}

