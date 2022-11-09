package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true").forGetter(config -> config.featureTrue),
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false").forGetter(config -> config.featureFalse)
				)
				.apply(instance, RandomBooleanFeatureConfig::new)
	);
	public final RegistryEntry<PlacedFeature> featureTrue;
	public final RegistryEntry<PlacedFeature> featureFalse;

	public RandomBooleanFeatureConfig(RegistryEntry<PlacedFeature> featureTrue, RegistryEntry<PlacedFeature> featureFalse) {
		this.featureTrue = featureTrue;
		this.featureFalse = featureFalse;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(((PlacedFeature)this.featureTrue.value()).getDecoratedFeatures(), ((PlacedFeature)this.featureFalse.value()).getDecoratedFeatures());
	}
}
