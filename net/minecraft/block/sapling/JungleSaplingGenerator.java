/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return new TreeFeature(TreeFeatureConfig.CODEC).configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
        return Feature.TREE.configure(DefaultBiomeFeatures.MEGA_JUNGLE_TREE_CONFIG);
    }
}

