package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RandomFeatureConfig implements FeatureConfig {
	public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.apply2(
				RandomFeatureConfig::new,
				RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(randomFeatureConfig -> randomFeatureConfig.features),
				PlacedFeature.REGISTRY_CODEC.fieldOf("default").forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)
			)
	);
	public final List<RandomFeatureEntry> features;
	public final Supplier<PlacedFeature> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry> features, PlacedFeature placedFeature) {
		this(features, () -> placedFeature);
	}

	private RandomFeatureConfig(List<RandomFeatureEntry> features, Supplier<PlacedFeature> defaultFeature) {
		this.features = features;
		this.defaultFeature = defaultFeature;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(
			this.features.stream().flatMap(randomFeatureEntry -> ((PlacedFeature)randomFeatureEntry.feature.get()).getDecoratedFeatures()),
			((PlacedFeature)this.defaultFeature.get()).getDecoratedFeatures()
		);
	}
}
