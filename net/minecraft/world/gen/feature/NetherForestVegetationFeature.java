/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class NetherForestVegetationFeature
extends Feature<BlockPileFeatureConfig> {
    public NetherForestVegetationFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig) {
        return NetherForestVegetationFeature.method_26264(serverWorldAccess, random, blockPos, blockPileFeatureConfig, 8, 4);
    }

    public static boolean method_26264(WorldAccess worldAccess, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig, int i, int j) {
        Block block = worldAccess.getBlockState(blockPos.down()).getBlock();
        while (!block.isIn(BlockTags.NYLIUM) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
            block = worldAccess.getBlockState(blockPos).getBlock();
        }
        int k = blockPos.getY();
        if (k < 1 || k + 1 >= 256) {
            return false;
        }
        int l = 0;
        for (int m = 0; m < i * i; ++m) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            BlockState blockState = blockPileFeatureConfig.stateProvider.getBlockState(random, blockPos2);
            if (!worldAccess.isAir(blockPos2) || blockPos2.getY() <= 0 || !blockState.canPlaceAt(worldAccess, blockPos2)) continue;
            worldAccess.setBlockState(blockPos2, blockState, 2);
            ++l;
        }
        return l > 0;
    }
}

