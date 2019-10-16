/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random) {
        return new OakTreeFeature((Function<Dynamic<?>, ? extends NormalTreeFeatureConfig>)((Function<Dynamic<?>, NormalTreeFeatureConfig>)NormalTreeFeatureConfig::method_23426)).configure(DefaultBiomeFeatures.field_21183);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
        return Feature.MEGA_JUNGLE_TREE.configure(DefaultBiomeFeatures.field_21200);
    }
}

