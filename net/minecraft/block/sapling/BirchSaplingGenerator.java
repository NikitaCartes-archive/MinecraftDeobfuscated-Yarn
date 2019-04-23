/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class BirchSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return new BirchTreeFeature(DefaultFeatureConfig::deserialize, true, false);
    }
}

