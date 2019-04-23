/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.sapling;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaJungleTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import org.jetbrains.annotations.Nullable;

public class JungleSaplingGenerator
extends LargeTreeSaplingGenerator {
    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
        return new OakTreeFeature(DefaultFeatureConfig::deserialize, true, 4 + random.nextInt(7), Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), false);
    }

    @Override
    @Nullable
    protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
        return new MegaJungleTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize), true, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState());
    }
}

