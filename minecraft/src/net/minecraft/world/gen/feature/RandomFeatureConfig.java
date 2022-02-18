package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.registry.RegistryEntry;

public class RandomFeatureConfig implements FeatureConfig {
	public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.apply2(
				RandomFeatureConfig::new,
				RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(config -> config.features),
				PlacedFeature.REGISTRY_CODEC.fieldOf("default").forGetter(config -> config.defaultFeature)
			)
	);
	public final List<RandomFeatureEntry> features;
	public final RegistryEntry<PlacedFeature> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry> features, RegistryEntry<PlacedFeature> defaultFeature) {
		this.features = features;
		this.defaultFeature = defaultFeature;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(
			this.features.stream().flatMap(entry -> ((PlacedFeature)entry.feature.value()).getDecoratedFeatures()),
			((PlacedFeature)this.defaultFeature.value()).getDecoratedFeatures()
		);
	}
}
