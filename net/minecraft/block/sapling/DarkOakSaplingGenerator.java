/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import org.jetbrains.annotations.Nullable;

public class DarkOakSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        return null;
    }

    @Override
    @Nullable
    protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
        return ConfiguredFeatures.DARK_OAK;
    }
}

