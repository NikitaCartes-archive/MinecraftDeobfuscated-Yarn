package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public final ConfiguredFeature<?> featureTrue;
	public final ConfiguredFeature<?> featureFalse;

	public RandomBooleanFeatureConfig(ConfiguredFeature<?> configuredFeature, ConfiguredFeature<?> configuredFeature2) {
		this.featureTrue = configuredFeature;
		this.featureFalse = configuredFeature2;
	}

	public RandomBooleanFeatureConfig(Feature<?> feature, FeatureConfig featureConfig, Feature<?> feature2, FeatureConfig featureConfig2) {
		this(configure(feature, featureConfig), configure(feature2, featureConfig2));
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig featureConfig) {
		return new ConfiguredFeature<>(feature, (FC)featureConfig);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("feature_true"),
					this.featureTrue.serialize(dynamicOps).getValue(),
					dynamicOps.createString("feature_false"),
					this.featureFalse.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public static <T> RandomBooleanFeatureConfig deserialize(Dynamic<T> dynamic) {
		ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize((Dynamic<T>)dynamic.get("feature_true").orElseGet(dynamic::emptyMap));
		ConfiguredFeature<?> configuredFeature2 = ConfiguredFeature.deserialize((Dynamic<T>)dynamic.get("feature_false").orElseGet(dynamic::emptyMap));
		return new RandomBooleanFeatureConfig(configuredFeature, configuredFeature2);
	}
}
