/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class SeaPickleFeature
extends Feature<CountConfig> {
    public SeaPickleFeature(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, CountConfig countConfig) {
        int i = 0;
        int j = countConfig.method_30396().method_30321(random);
        for (int k = 0; k < j; ++k) {
            int l = random.nextInt(8) - random.nextInt(8);
            int m = random.nextInt(8) - random.nextInt(8);
            int n = serverWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + l, blockPos.getZ() + m);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + l, n, blockPos.getZ() + m);
            BlockState blockState = (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (!serverWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER) || !blockState.canPlaceAt(serverWorldAccess, blockPos2)) continue;
            serverWorldAccess.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }
}

