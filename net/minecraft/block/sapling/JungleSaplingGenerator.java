/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.class_4636;
import net.minecraft.class_4640;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected ConfiguredFeature<class_4640, ?> createTreeFeature(Random random) {
        return new OakTreeFeature((Function<Dynamic<?>, ? extends class_4640>)((Function<Dynamic<?>, class_4640>)class_4640::method_23426)).method_23397(DefaultBiomeFeatures.field_21183);
    }

    @Override
    @Nullable
    protected ConfiguredFeature<class_4636, ?> createLargeTreeFeature(Random random) {
        return Feature.MEGA_JUNGLE_TREE.method_23397(DefaultBiomeFeatures.field_21200);
    }
}

