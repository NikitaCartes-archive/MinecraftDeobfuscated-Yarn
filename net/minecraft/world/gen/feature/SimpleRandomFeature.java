/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleRandomFeature
extends Feature<SimpleRandomFeatureConfig> {
    public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SimpleRandomFeatureConfig> context) {
        Random random = context.getRandom();
        SimpleRandomFeatureConfig simpleRandomFeatureConfig = context.getConfig();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getPos();
        ChunkGenerator chunkGenerator = context.getGenerator();
        int i = random.nextInt(simpleRandomFeatureConfig.features.size());
        ConfiguredFeature<?, ?> configuredFeature = simpleRandomFeatureConfig.features.get(i).get();
        return configuredFeature.generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}

