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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

public class SimpleRandomFeature
extends Feature<SimpleRandomFeatureConfig> {
    public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleRandomFeatureConfig simpleRandomFeatureConfig) {
        int i = random.nextInt(simpleRandomFeatureConfig.features.size());
        ConfiguredFeature<?, ?> configuredFeature = simpleRandomFeatureConfig.features.get(i);
        return configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
    }
}

