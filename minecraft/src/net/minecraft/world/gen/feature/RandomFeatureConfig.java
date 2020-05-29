package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class RandomFeatureConfig implements FeatureConfig {
	public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.apply2(
				RandomFeatureConfig::new,
				RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(randomFeatureConfig -> randomFeatureConfig.features),
				ConfiguredFeature.CODEC.fieldOf("default").forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)
			)
	);
	public final List<RandomFeatureEntry<?>> features;
	public final ConfiguredFeature<?, ?> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry<?>> features, ConfiguredFeature<?, ?> defaultFeature) {
		this.features = features;
		this.defaultFeature = defaultFeature;
	}
}
