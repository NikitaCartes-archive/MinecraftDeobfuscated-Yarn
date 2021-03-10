/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleBlockFeature
extends Feature<SimpleBlockFeatureConfig> {
    public SimpleBlockFeature(Codec<SimpleBlockFeatureConfig> codec) {
        super(codec);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean generate(FeatureContext<SimpleBlockFeatureConfig> context) {
        BlockState blockState;
        SimpleBlockFeatureConfig simpleBlockFeatureConfig = context.getConfig();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        if (!simpleBlockFeatureConfig.placeOn.isEmpty() && !simpleBlockFeatureConfig.placeOn.contains(structureWorldAccess.getBlockState(blockPos.down())) || !simpleBlockFeatureConfig.placeIn.isEmpty() && !simpleBlockFeatureConfig.placeIn.contains(structureWorldAccess.getBlockState(blockPos)) || !simpleBlockFeatureConfig.placeUnder.isEmpty() && !simpleBlockFeatureConfig.placeUnder.contains(structureWorldAccess.getBlockState(blockPos.up())) || !(blockState = simpleBlockFeatureConfig.toPlace.getBlockState(context.getRandom(), blockPos)).canPlaceAt(structureWorldAccess, blockPos)) return false;
        if (blockState.getBlock() instanceof TallPlantBlock) {
            if (!structureWorldAccess.isAir(blockPos.up())) return false;
            TallPlantBlock tallPlantBlock = (TallPlantBlock)blockState.getBlock();
            tallPlantBlock.placeAt(structureWorldAccess, blockPos, 2);
            return true;
        } else {
            structureWorldAccess.setBlockState(blockPos, blockState, 2);
        }
        return true;
    }
}

