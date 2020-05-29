package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class RandomRandomFeatureConfig implements FeatureConfig {
	public static final Codec<RandomRandomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter(randomRandomFeatureConfig -> randomRandomFeatureConfig.features),
					Codec.INT.fieldOf("count").withDefault(0).forGetter(randomRandomFeatureConfig -> randomRandomFeatureConfig.count)
				)
				.apply(instance, RandomRandomFeatureConfig::new)
	);
	public final List<ConfiguredFeature<?, ?>> features;
	public final int count;

	public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features, int count) {
		this.features = features;
		this.count = count;
	}
}
