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
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeature
extends Feature<RandomFeatureConfig> {
    public RandomFeature(Codec<RandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<RandomFeatureConfig> arg) {
        RandomFeatureConfig randomFeatureConfig = arg.method_33656();
        Random random = arg.method_33654();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        ChunkGenerator chunkGenerator = arg.method_33653();
        BlockPos blockPos = arg.method_33655();
        for (RandomFeatureEntry randomFeatureEntry : randomFeatureConfig.features) {
            if (!(random.nextFloat() < randomFeatureEntry.chance)) continue;
            return randomFeatureEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
        }
        return randomFeatureConfig.defaultFeature.get().generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}

