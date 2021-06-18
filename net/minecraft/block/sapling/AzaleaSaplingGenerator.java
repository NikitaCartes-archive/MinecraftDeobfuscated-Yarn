/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class AzaleaSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean bees) {
        return ConfiguredFeatures.AZALEA_TREE;
    }
}

