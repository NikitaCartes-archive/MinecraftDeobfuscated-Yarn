package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class OceanConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEAGRASS_SHORT = ConfiguredFeatures.of("seagrass_short");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEAGRASS_SLIGHTLY_LESS_SHORT = ConfiguredFeatures.of("seagrass_slightly_less_short");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEAGRASS_MID = ConfiguredFeatures.of("seagrass_mid");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEAGRASS_TALL = ConfiguredFeatures.of("seagrass_tall");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEA_PICKLE = ConfiguredFeatures.of("sea_pickle");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SEAGRASS_SIMPLE = ConfiguredFeatures.of("seagrass_simple");
	public static final RegistryKey<ConfiguredFeature<?, ?>> KELP = ConfiguredFeatures.of("kelp");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WARM_OCEAN_VEGETATION = ConfiguredFeatures.of("warm_ocean_vegetation");

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		ConfiguredFeatures.register(featureRegisterable, SEAGRASS_SHORT, Feature.SEAGRASS, new ProbabilityConfig(0.3F));
		ConfiguredFeatures.register(featureRegisterable, SEAGRASS_SLIGHTLY_LESS_SHORT, Feature.SEAGRASS, new ProbabilityConfig(0.4F));
		ConfiguredFeatures.register(featureRegisterable, SEAGRASS_MID, Feature.SEAGRASS, new ProbabilityConfig(0.6F));
		ConfiguredFeatures.register(featureRegisterable, SEAGRASS_TALL, Feature.SEAGRASS, new ProbabilityConfig(0.8F));
		ConfiguredFeatures.register(featureRegisterable, SEA_PICKLE, Feature.SEA_PICKLE, new CountConfig(20));
		ConfiguredFeatures.register(featureRegisterable, SEAGRASS_SIMPLE, Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SEAGRASS)));
		ConfiguredFeatures.register(featureRegisterable, KELP, Feature.KELP);
		ConfiguredFeatures.register(
			featureRegisterable,
			WARM_OCEAN_VEGETATION,
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
}
