/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.class_4640;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<class_4640, ?> createTreeFeature(Random random) {
        return random.nextInt(10) == 0 ? Feature.FANCY_TREE.method_23397(DefaultBiomeFeatures.field_21190) : Feature.NORMAL_TREE.method_23397(DefaultBiomeFeatures.field_21126);
    }
}

