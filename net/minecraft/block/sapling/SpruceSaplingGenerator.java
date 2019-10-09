/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.class_4636;
import net.minecraft.class_4640;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class SpruceSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<class_4640, ?> createTreeFeature(Random random) {
        return Feature.NORMAL_TREE.method_23397(DefaultBiomeFeatures.field_21185);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<class_4636, ?> createLargeTreeFeature(Random random) {
        return Feature.MEGA_SPRUCE_TREE.method_23397(random.nextBoolean() ? DefaultBiomeFeatures.field_21198 : DefaultBiomeFeatures.field_21199);
    }
}

