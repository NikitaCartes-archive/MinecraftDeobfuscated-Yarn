/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import org.jetbrains.annotations.Nullable;

public class OakSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return random.nextInt(10) == 0 ? new LargeOakTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true) : new OakTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true);
    }
}

