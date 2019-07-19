package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public final ConfiguredFeature<?> featureTrue;
	public final ConfiguredFeature<?> featureFalse;

	public RandomBooleanFeatureConfig(ConfiguredFeature<?> featureTrue, ConfiguredFeature<?> featureFalse) {
		this.featureTrue = featureTrue;
		this.featureFalse = featureFalse;
	}

	public RandomBooleanFeatureConfig(Feature<?> featureTrue, FeatureConfig featureConfigTrue, Feature<?> featureFalse, FeatureConfig featureConfig) {
		this(configure(featureTrue, featureConfigTrue), configure(featureFalse, featureConfig));
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig config) {
		return new ConfiguredFeature<>(feature, (FC)config);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("feature_true"),
					this.featureTrue.serialize(ops).getValue(),
					ops.createString("feature_false"),
					this.featureFalse.serialize(ops).getValue()
				)
			)
		);
	}

	public static <T> RandomBooleanFeatureConfig deserialize(Dynamic<T> dynamic) {
		ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("feature_true").orElseEmptyMap());
		ConfiguredFeature<?> configuredFeature2 = ConfiguredFeature.deserialize(dynamic.get("feature_false").orElseEmptyMap());
		return new RandomBooleanFeatureConfig(configuredFeature, configuredFeature2);
	}
}
