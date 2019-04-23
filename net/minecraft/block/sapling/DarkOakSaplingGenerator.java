/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class DarkOakSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return null;
    }

    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
        return new DarkOakTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true);
    }
}

