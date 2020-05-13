/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;

public class RandomBooleanFeature
extends Feature<RandomBooleanFeatureConfig> {
    public RandomBooleanFeature(Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfig randomBooleanFeatureConfig) {
        boolean bl = random.nextBoolean();
        if (bl) {
            return randomBooleanFeatureConfig.featureTrue.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
        }
        return randomBooleanFeatureConfig.featureFalse.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
    }
}

