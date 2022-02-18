/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

@Deprecated
public class LakeFeature
extends Feature<Config> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public LakeFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<Config> context) {
        int t;
        int s;
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        Config config = context.getConfig();
        if (blockPos.getY() <= structureWorldAccess.getBottomY() + 4) {
            return false;
        }
        blockPos = blockPos.down(4);
        boolean[] bls = new boolean[2048];
        int i = random.nextInt(4) + 4;
        for (int j = 0; j < i; ++j) {
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
        BlockState blockState = config.fluid().getBlockState(random, blockPos);
        for (s = 0; s < 16; ++s) {
            for (t = 0; t < 16; ++t) {
                for (int u = 0; u < 8; ++u) {
                    boolean bl;
                    boolean bl2 = bl = !bls[(s * 16 + t) * 8 + u] && (s < 15 && bls[((s + 1) * 16 + t) * 8 + u] || s > 0 && bls[((s - 1) * 16 + t) * 8 + u] || t < 15 && bls[(s * 16 + t + 1) * 8 + u] || t > 0 && bls[(s * 16 + (t - 1)) * 8 + u] || u < 7 && bls[(s * 16 + t) * 8 + u + 1] || u > 0 && bls[(s * 16 + t) * 8 + (u - 1)]);
                    if (!bl) continue;
                    Material material = structureWorldAccess.getBlockState(blockPos.add(s, u, t)).getMaterial();
                    if (u >= 4 && material.isLiquid()) {
                        return false;
                    }
                    if (u >= 4 || material.isSolid() || structureWorldAccess.getBlockState(blockPos.add(s, u, t)) == blockState) continue;
                    return false;
                }
            }
        }
        for (s = 0; s < 16; ++s) {
            for (t = 0; t < 16; ++t) {
                for (int u = 0; u < 8; ++u) {
                    BlockPos blockPos2;
                    if (!bls[(s * 16 + t) * 8 + u] || !this.canReplace(structureWorldAccess.getBlockState(blockPos2 = blockPos.add(s, u, t)))) continue;
                    boolean bl2 = u >= 4;
                    structureWorldAccess.setBlockState(blockPos2, bl2 ? CAVE_AIR : blockState, Block.NOTIFY_LISTENERS);
                    if (!bl2) continue;
                    structureWorldAccess.createAndScheduleBlockTick(blockPos2, CAVE_AIR.getBlock(), 0);
                    this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos2);
                }
            }
        }
        BlockState blockState2 = config.barrier().getBlockState(random, blockPos);
        if (!blockState2.isAir()) {
            for (t = 0; t < 16; ++t) {
                for (int u = 0; u < 16; ++u) {
                    for (int v = 0; v < 8; ++v) {
                        BlockState blockState3;
                        boolean bl2;
                        boolean bl = bl2 = !bls[(t * 16 + u) * 8 + v] && (t < 15 && bls[((t + 1) * 16 + u) * 8 + v] || t > 0 && bls[((t - 1) * 16 + u) * 8 + v] || u < 15 && bls[(t * 16 + u + 1) * 8 + v] || u > 0 && bls[(t * 16 + (u - 1)) * 8 + v] || v < 7 && bls[(t * 16 + u) * 8 + v + 1] || v > 0 && bls[(t * 16 + u) * 8 + (v - 1)]);
                        if (!bl2 || v >= 4 && random.nextInt(2) == 0 || !(blockState3 = structureWorldAccess.getBlockState(blockPos.add(t, v, u))).getMaterial().isSolid() || blockState3.isIn(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) continue;
                        BlockPos blockPos3 = blockPos.add(t, v, u);
                        structureWorldAccess.setBlockState(blockPos3, blockState2, Block.NOTIFY_LISTENERS);
                        this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos3);
                    }
                }
            }
        }
        if (blockState.getFluidState().isIn(FluidTags.WATER)) {
            for (t = 0; t < 16; ++t) {
                for (int u = 0; u < 16; ++u) {
                    int v = 4;
                    BlockPos blockPos4 = blockPos.add(t, 4, u);
                    if (!structureWorldAccess.getBiome(blockPos4).value().canSetIce(structureWorldAccess, blockPos4, false) || !this.canReplace(structureWorldAccess.getBlockState(blockPos4))) continue;
                    structureWorldAccess.setBlockState(blockPos4, Blocks.ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
        return true;
    }

    private boolean canReplace(BlockState state) {
        return !state.isIn(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    public record Config(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfig
    {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("fluid")).forGetter(Config::fluid), ((MapCodec)BlockStateProvider.TYPE_CODEC.fieldOf("barrier")).forGetter(Config::barrier)).apply((Applicative<Config, ?>)instance, Config::new));
    }
}

