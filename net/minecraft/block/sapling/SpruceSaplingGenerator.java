/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class SpruceSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        return TreeConfiguredFeatures.SPRUCE;
    }

    @Override
    protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
        return random.nextBoolean() ? TreeConfiguredFeatures.MEGA_SPRUCE : TreeConfiguredFeatures.MEGA_PINE;
    }
}

