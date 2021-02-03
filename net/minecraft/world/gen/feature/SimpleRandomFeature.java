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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

public class SimpleRandomFeature
extends Feature<SimpleRandomFeatureConfig> {
    public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<SimpleRandomFeatureConfig> arg) {
        Random random = arg.method_33654();
        SimpleRandomFeatureConfig simpleRandomFeatureConfig = arg.method_33656();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        BlockPos blockPos = arg.method_33655();
        ChunkGenerator chunkGenerator = arg.method_33653();
        int i = random.nextInt(simpleRandomFeatureConfig.features.size());
        ConfiguredFeature<?, ?> configuredFeature = simpleRandomFeatureConfig.features.get(i).get();
        return configuredFeature.generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}

