package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue),
					ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)
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
