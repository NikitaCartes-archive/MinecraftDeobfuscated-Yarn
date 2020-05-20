/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;

public class SimpleBlockFeature
extends Feature<SimpleBlockFeatureConfig> {
    public SimpleBlockFeature(Codec<SimpleBlockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleBlockFeatureConfig simpleBlockFeatureConfig) {
        if (simpleBlockFeatureConfig.placeOn.contains(serverWorldAccess.getBlockState(blockPos.down())) && simpleBlockFeatureConfig.placeIn.contains(serverWorldAccess.getBlockState(blockPos)) && simpleBlockFeatureConfig.placeUnder.contains(serverWorldAccess.getBlockState(blockPos.up()))) {
            serverWorldAccess.setBlockState(blockPos, simpleBlockFeatureConfig.toPlace, 2);
            return true;
        }
        return false;
    }
}

