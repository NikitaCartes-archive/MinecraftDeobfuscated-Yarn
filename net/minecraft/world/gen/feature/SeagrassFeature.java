/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;

public class SeagrassFeature
extends Feature<SeagrassFeatureConfig> {
    public SeagrassFeature(Codec<SeagrassFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SeagrassFeatureConfig seagrassFeatureConfig) {
        int i = 0;
        for (int j = 0; j < seagrassFeatureConfig.count; ++j) {
            BlockState blockState;
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
            if (!serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) continue;
            boolean bl = random.nextDouble() < seagrassFeatureConfig.tallSeagrassProbability;
            BlockState blockState2 = blockState = bl ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (!blockState.canPlaceAt(serverWorldAccess, blockPos2)) continue;
            if (bl) {
                BlockState blockState22 = (BlockState)blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
                BlockPos blockPos3 = blockPos2.up();
                if (serverWorldAccess.getBlockState(blockPos3).isOf(Blocks.WATER)) {
                    serverWorldAccess.setBlockState(blockPos2, blockState, 2);
                    serverWorldAccess.setBlockState(blockPos3, blockState22, 2);
                }
            } else {
                serverWorldAccess.setBlockState(blockPos2, blockState, 2);
            }
            ++i;
        }
        return i > 0;
    }
}

