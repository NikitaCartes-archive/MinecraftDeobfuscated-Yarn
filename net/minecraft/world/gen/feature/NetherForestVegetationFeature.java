/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
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
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig) {
        return NetherForestVegetationFeature.generate(structureWorldAccess, random, blockPos, blockPileFeatureConfig, 8, 4);
    }

    public static boolean generate(WorldAccess world, Random random, BlockPos pos, BlockPileFeatureConfig config, int i, int j) {
        BlockState blockState = world.getBlockState(pos.down());
        if (!blockState.isIn(BlockTags.NYLIUM)) {
            return false;
        }
        int k = pos.getY();
        if (k < world.getBottomHeightLimit() + 1 || k + 1 >= world.getTopHeightLimit()) {
            return false;
        }
        int l = 0;
        for (int m = 0; m < i * i; ++m) {
            BlockPos blockPos = pos.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            BlockState blockState2 = config.stateProvider.getBlockState(random, blockPos);
            if (!world.isAir(blockPos) || blockPos.getY() <= world.getBottomHeightLimit() || !blockState2.canPlaceAt(world, blockPos)) continue;
            world.setBlockState(blockPos, blockState2, 2);
            ++l;
        }
        return l > 0;
    }
}

