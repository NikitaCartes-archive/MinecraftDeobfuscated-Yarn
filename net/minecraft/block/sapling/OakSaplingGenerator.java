/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return random.nextInt(10) == 0 ? Feature.FANCY_TREE.configure(bl ? DefaultBiomeFeatures.FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG : DefaultBiomeFeatures.FANCY_TREE_CONFIG) : Feature.NORMAL_TREE.configure(bl ? DefaultBiomeFeatures.OAK_TREE_WITH_MORE_BEEHIVES_CONFIG : DefaultBiomeFeatures.OAK_TREE_CONFIG);
    }
}

