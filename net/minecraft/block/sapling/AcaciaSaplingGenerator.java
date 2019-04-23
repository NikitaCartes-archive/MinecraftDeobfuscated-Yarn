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
import net.minecraft.world.gen.feature.SavannaTreeFeature;
import org.jetbrains.annotations.Nullable;

public class AcaciaSaplingGenerator
extends SaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return new SavannaTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true);
    }
}

