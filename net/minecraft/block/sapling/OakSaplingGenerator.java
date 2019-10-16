/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random) {
        return random.nextInt(10) == 0 ? Feature.FANCY_TREE.configure(DefaultBiomeFeatures.field_21190) : Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.field_21126);
    }
}

