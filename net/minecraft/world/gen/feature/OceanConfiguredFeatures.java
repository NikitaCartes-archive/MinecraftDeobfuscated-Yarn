/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class OceanConfiguredFeatures {
    public static final ConfiguredFeature<?, ?> SEAGRASS_SHORT = ConfiguredFeatures.register("seagrass_short", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3f)));
    public static final ConfiguredFeature<?, ?> SEAGRASS_SLIGHTLY_LESS_SHORT = ConfiguredFeatures.register("seagrass_slightly_less_short", Feature.SEAGRASS.configure(new ProbabilityConfig(0.4f)));
    public static final ConfiguredFeature<?, ?> SEAGRASS_MID = ConfiguredFeatures.register("seagrass_mid", Feature.SEAGRASS.configure(new ProbabilityConfig(0.6f)));
    public static final ConfiguredFeature<?, ?> SEAGRASS_TALL = ConfiguredFeatures.register("seagrass_tall", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8f)));
    public static final ConfiguredFeature<?, ?> SEA_PICKLE = ConfiguredFeatures.register("sea_pickle", Feature.SEA_PICKLE.configure(new CountConfig(20)));
    public static final ConfiguredFeature<?, ?> SEAGRASS_SIMPLE = ConfiguredFeatures.register("seagrass_simple", Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SEAGRASS))));
    public static final ConfiguredFeature<DefaultFeatureConfig, ?> KELP = ConfiguredFeatures.register("kelp", Feature.KELP.configure(FeatureConfig.DEFAULT));
    public static final ConfiguredFeature<SimpleRandomFeatureConfig, ?> WARM_OCEAN_VEGETATION = ConfiguredFeatures.register("warm_ocean_vegetation", Feature.SIMPLE_RANDOM_SELECTOR.configure(new SimpleRandomFeatureConfig(List.of(() -> Feature.CORAL_TREE.configure(FeatureConfig.DEFAULT).withPlacement(new PlacementModifier[0]), () -> Feature.CORAL_CLAW.configure(FeatureConfig.DEFAULT).withPlacement(new PlacementModifier[0]), () -> Feature.CORAL_MUSHROOM.configure(FeatureConfig.DEFAULT).withPlacement(new PlacementModifier[0])))));
}

