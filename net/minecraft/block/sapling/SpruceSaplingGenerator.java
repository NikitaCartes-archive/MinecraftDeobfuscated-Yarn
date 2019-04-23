/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaPineTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import org.jetbrains.annotations.Nullable;

public class SpruceSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return new SpruceTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true);
    }

    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
        return new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, random.nextBoolean());
    }
}

