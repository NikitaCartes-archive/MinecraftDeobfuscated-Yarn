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
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return new TreeFeature((Function<Dynamic<?>, ? extends TreeFeatureConfig>)((Function<Dynamic<?>, TreeFeatureConfig>)TreeFeatureConfig::deserialize)).configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
        return Feature.TREE.configure(DefaultBiomeFeatures.MEGA_JUNGLE_TREE_CONFIG);
    }
}

