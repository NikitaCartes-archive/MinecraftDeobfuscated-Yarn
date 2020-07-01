/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;

public class RandomBooleanFeature
extends Feature<RandomBooleanFeatureConfig> {
    public RandomBooleanFeature(Codec<RandomBooleanFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfig randomBooleanFeatureConfig) {
        boolean bl = random.nextBoolean();
        if (bl) {
            return randomBooleanFeatureConfig.featureTrue.generate(serverWorldAccess, chunkGenerator, random, blockPos);
        }
        return randomBooleanFeatureConfig.featureFalse.generate(serverWorldAccess, chunkGenerator, random, blockPos);
    }
}

