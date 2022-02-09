package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.registry.RegistryEntry;

public class RandomFeatureConfig implements FeatureConfig {
	public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.apply2(
				RandomFeatureConfig::new,
				RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(randomFeatureConfig -> randomFeatureConfig.features),
				PlacedFeature.REGISTRY_CODEC.fieldOf("default").forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)
			)
	);
	public final List<RandomFeatureEntry> features;
	public final RegistryEntry<PlacedFeature> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry> features, RegistryEntry<PlacedFeature> registryEntry) {
		this.features = features;
		this.defaultFeature = registryEntry;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(
			this.features.stream().flatMap(randomFeatureEntry -> randomFeatureEntry.feature.value().getDecoratedFeatures()),
			this.defaultFeature.value().getDecoratedFeatures()
		);
	}
}
