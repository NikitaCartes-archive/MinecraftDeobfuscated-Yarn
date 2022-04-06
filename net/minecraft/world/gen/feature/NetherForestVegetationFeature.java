/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NetherForestVegetationFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class NetherForestVegetationFeature
extends Feature<NetherForestVegetationFeatureConfig> {
    public NetherForestVegetationFeature(Codec<NetherForestVegetationFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<NetherForestVegetationFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockState blockState = structureWorldAccess.getBlockState(blockPos.down());
        NetherForestVegetationFeatureConfig netherForestVegetationFeatureConfig = context.getConfig();
        AbstractRandom abstractRandom = context.getRandom();
        if (!blockState.isIn(BlockTags.NYLIUM)) {
            return false;
        }
        int i = blockPos.getY();
        if (i < structureWorldAccess.getBottomY() + 1 || i + 1 >= structureWorldAccess.getTopY()) {
            return false;
        }
        int j = 0;
        for (int k = 0; k < netherForestVegetationFeatureConfig.spreadWidth * netherForestVegetationFeatureConfig.spreadWidth; ++k) {
            BlockPos blockPos2 = blockPos.add(abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadWidth) - abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadWidth), abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadHeight) - abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadHeight), abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadWidth) - abstractRandom.nextInt(netherForestVegetationFeatureConfig.spreadWidth));
            BlockState blockState2 = netherForestVegetationFeatureConfig.stateProvider.getBlockState(abstractRandom, blockPos2);
            if (!structureWorldAccess.isAir(blockPos2) || blockPos2.getY() <= structureWorldAccess.getBottomY() || !blockState2.canPlaceAt(structureWorldAccess, blockPos2)) continue;
            structureWorldAccess.setBlockState(blockPos2, blockState2, Block.NOTIFY_LISTENERS);
            ++j;
        }
        return j > 0;
    }
}

