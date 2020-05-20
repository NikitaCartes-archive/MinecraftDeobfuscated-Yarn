/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class GlowstoneBlobFeature
extends Feature<DefaultFeatureConfig> {
    public GlowstoneBlobFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        if (!serverWorldAccess.isAir(blockPos)) {
            return false;
        }
        BlockState blockState = serverWorldAccess.getBlockState(blockPos.up());
        if (!(blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.BASALT) || blockState.isOf(Blocks.BLACKSTONE))) {
            return false;
        }
        serverWorldAccess.setBlockState(blockPos, Blocks.GLOWSTONE.getDefaultState(), 2);
        for (int i = 0; i < 1500; ++i) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if (!serverWorldAccess.getBlockState(blockPos2).isAir()) continue;
            int j = 0;
            for (Direction direction : Direction.values()) {
                if (serverWorldAccess.getBlockState(blockPos2.offset(direction)).isOf(Blocks.GLOWSTONE)) {
                    ++j;
                }
                if (j > 1) break;
            }
            if (j != true) continue;
            serverWorldAccess.setBlockState(blockPos2, Blocks.GLOWSTONE.getDefaultState(), 2);
        }
        return true;
    }
}

