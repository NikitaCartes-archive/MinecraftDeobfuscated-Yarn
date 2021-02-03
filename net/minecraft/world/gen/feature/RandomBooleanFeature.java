/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;

public class RandomBooleanFeature
extends Feature<RandomBooleanFeatureConfig> {
    public RandomBooleanFeature(Codec<RandomBooleanFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<RandomBooleanFeatureConfig> arg) {
        Random random = arg.method_33654();
        RandomBooleanFeatureConfig randomBooleanFeatureConfig = arg.method_33656();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        ChunkGenerator chunkGenerator = arg.method_33653();
        BlockPos blockPos = arg.method_33655();
        boolean bl = random.nextBoolean();
        if (bl) {
            return randomBooleanFeatureConfig.featureTrue.get().generate(structureWorldAccess, chunkGenerator, random, blockPos);
        }
        return randomBooleanFeatureConfig.featureFalse.get().generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}

