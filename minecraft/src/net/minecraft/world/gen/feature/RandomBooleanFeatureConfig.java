package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> field_24900 = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.field_24833.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue),
					ConfiguredFeature.field_24833.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)
				)
				.apply(instance, RandomBooleanFeatureConfig::new)
	);
	public final ConfiguredFeature<?, ?> featureTrue;
	public final ConfiguredFeature<?, ?> featureFalse;

	public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> featureTrue, ConfiguredFeature<?, ?> featureFalse) {
		this.featureTrue = featureTrue;
		this.featureFalse = featureFalse;
	}
}
