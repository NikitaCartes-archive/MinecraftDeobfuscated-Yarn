package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public final ConfiguredFeature<?, ?> featureTrue;
	public final ConfiguredFeature<?, ?> featureFalse;

	public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> configuredFeature, ConfiguredFeature<?, ?> configuredFeature2) {
		this.featureTrue = configuredFeature;
		this.featureFalse = configuredFeature2;
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
		ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("feature_true").orElseEmptyMap());
		ConfiguredFeature<?, ?> configuredFeature2 = ConfiguredFeature.deserialize(dynamic.get("feature_false").orElseEmptyMap());
		return new RandomBooleanFeatureConfig(configuredFeature, configuredFeature2);
	}
}
