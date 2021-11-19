package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue),
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)
				)
				.apply(instance, RandomBooleanFeatureConfig::new)
	);
	public final Supplier<PlacedFeature> featureTrue;
	public final Supplier<PlacedFeature> featureFalse;

	public RandomBooleanFeatureConfig(Supplier<PlacedFeature> featureTrue, Supplier<PlacedFeature> featureFalse) {
		this.featureTrue = featureTrue;
		this.featureFalse = featureFalse;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(((PlacedFeature)this.featureTrue.get()).getDecoratedFeatures(), ((PlacedFeature)this.featureFalse.get()).getDecoratedFeatures());
	}
}
