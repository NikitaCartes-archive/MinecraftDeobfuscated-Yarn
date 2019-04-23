/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;

public class RandomRandomFeature
extends Feature<RandomRandomFeatureConfig> {
    public RandomRandomFeature(Function<Dynamic<?>, ? extends RandomRandomFeatureConfig> function) {
        super(function);
    }

    public boolean method_13696(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, RandomRandomFeatureConfig randomRandomFeatureConfig) {
        int i = random.nextInt(5) - 3 + randomRandomFeatureConfig.count;
        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(randomRandomFeatureConfig.features.size());
            ConfiguredFeature<?> configuredFeature = randomRandomFeatureConfig.features.get(k);
            configuredFeature.generate(iWorld, chunkGenerator, random, blockPos);
        }
        return true;
    }
}

