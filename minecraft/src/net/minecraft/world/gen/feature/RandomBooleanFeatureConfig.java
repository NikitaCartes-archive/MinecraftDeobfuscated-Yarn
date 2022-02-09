package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import net.minecraft.util.registry.RegistryEntry;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue),
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)
				)
				.apply(instance, RandomBooleanFeatureConfig::new)
	);
	public final RegistryEntry<PlacedFeature> featureTrue;
	public final RegistryEntry<PlacedFeature> featureFalse;

	public RandomBooleanFeatureConfig(RegistryEntry<PlacedFeature> registryEntry, RegistryEntry<PlacedFeature> registryEntry2) {
		this.featureTrue = registryEntry;
		this.featureFalse = registryEntry2;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(((PlacedFeature)this.featureTrue.value()).getDecoratedFeatures(), ((PlacedFeature)this.featureFalse.value()).getDecoratedFeatures());
	}
}
