package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class OceanConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> SEAGRASS_SHORT = ConfiguredFeatures.register(
		"seagrass_short", Feature.SEAGRASS, new ProbabilityConfig(0.3F)
	);
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> SEAGRASS_SLIGHTLY_LESS_SHORT = ConfiguredFeatures.register(
		"seagrass_slightly_less_short", Feature.SEAGRASS, new ProbabilityConfig(0.4F)
	);
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> SEAGRASS_MID = ConfiguredFeatures.register(
		"seagrass_mid", Feature.SEAGRASS, new ProbabilityConfig(0.6F)
	);
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> SEAGRASS_TALL = ConfiguredFeatures.register(
		"seagrass_tall", Feature.SEAGRASS, new ProbabilityConfig(0.8F)
	);
	public static final RegistryEntry<ConfiguredFeature<CountConfig, ?>> SEA_PICKLE = ConfiguredFeatures.register(
		"sea_pickle", Feature.SEA_PICKLE, new CountConfig(20)
	);
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> SEAGRASS_SIMPLE = ConfiguredFeatures.register(
		"seagrass_simple", Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SEAGRASS))
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> KELP = ConfiguredFeatures.register("kelp", Feature.KELP);
	public static final RegistryEntry<ConfiguredFeature<SimpleRandomFeatureConfig, ?>> WARM_OCEAN_VEGETATION = ConfiguredFeatures.register(
		"warm_ocean_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR,
		new SimpleRandomFeatureConfig(
			RegistryEntryList.of(
				PlacedFeatures.createEntry(Feature.CORAL_TREE, FeatureConfig.DEFAULT),
				PlacedFeatures.createEntry(Feature.CORAL_CLAW, FeatureConfig.DEFAULT),
				PlacedFeatures.createEntry(Feature.CORAL_MUSHROOM, FeatureConfig.DEFAULT)
			)
		)
	);
}
