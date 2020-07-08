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

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        if (random.nextInt(10) == 0) {
            return bl ? ConfiguredFeatures.FANCY_OAK_BEES_005 : ConfiguredFeatures.FANCY_OAK;
        }
        return bl ? ConfiguredFeatures.OAK_BEES_005 : ConfiguredFeatures.OAK;
    }
}

