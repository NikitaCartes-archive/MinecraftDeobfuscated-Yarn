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
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;

public class RandomRandomFeature
extends Feature<RandomRandomFeatureConfig> {
    public RandomRandomFeature(Codec<RandomRandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomRandomFeatureConfig randomRandomFeatureConfig) {
        int i = random.nextInt(5) - 3 + randomRandomFeatureConfig.count;
        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(randomRandomFeatureConfig.features.size());
            ConfiguredFeature<?, ?> configuredFeature = randomRandomFeatureConfig.features.get(k);
            configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
        }
        return true;
    }
}

