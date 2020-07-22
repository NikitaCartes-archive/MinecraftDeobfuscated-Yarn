/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;

public class SimpleBlockFeature
extends Feature<SimpleBlockFeatureConfig> {
    public SimpleBlockFeature(Codec<SimpleBlockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleBlockFeatureConfig simpleBlockFeatureConfig) {
        if (simpleBlockFeatureConfig.placeOn.contains(structureWorldAccess.getBlockState(blockPos.down())) && simpleBlockFeatureConfig.placeIn.contains(structureWorldAccess.getBlockState(blockPos)) && simpleBlockFeatureConfig.placeUnder.contains(structureWorldAccess.getBlockState(blockPos.up()))) {
            structureWorldAccess.setBlockState(blockPos, simpleBlockFeatureConfig.toPlace, 2);
            return true;
        }
        return false;
    }
}

