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
				ConfiguredFeature.CODEC.fieldOf("default").forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)
			)
	);
	public final List<RandomFeatureEntry> features;
	public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry> features, ConfiguredFeature<?, ?> defaultFeature) {
		this(features, () -> defaultFeature);
	}

	private RandomFeatureConfig(List<RandomFeatureEntry> list, Supplier<ConfiguredFeature<?, ?>> supplier) {
		this.features = list;
		this.defaultFeature = supplier;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> method_30649() {
		return Stream.concat(
			this.features.stream().flatMap(randomFeatureEntry -> ((ConfiguredFeature)randomFeatureEntry.feature.get()).method_30648()),
			((ConfiguredFeature)this.defaultFeature.get()).method_30648()
		);
	}
}
