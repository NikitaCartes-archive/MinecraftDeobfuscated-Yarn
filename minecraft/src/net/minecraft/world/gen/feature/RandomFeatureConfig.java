package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomFeatureConfig implements FeatureConfig {
	public final List<RandomFeatureEntry<?>> features;
	public final ConfiguredFeature<?> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry<?>> list, ConfiguredFeature<?> configuredFeature) {
		this.features = list;
		this.defaultFeature = configuredFeature;
	}

	public RandomFeatureConfig(Feature<?>[] features, FeatureConfig[] featureConfigs, float[] fs, Feature<?> feature, FeatureConfig featureConfig) {
		this(
			(List<RandomFeatureEntry<?>>)IntStream.range(0, features.length)
				.mapToObj(i -> makeEntry(features[i], featureConfigs[i], fs[i]))
				.collect(Collectors.toList()),
			configure(feature, featureConfig)
		);
	}

	private static <FC extends FeatureConfig> RandomFeatureEntry<FC> makeEntry(Feature<FC> feature, FeatureConfig featureConfig, float f) {
		return new RandomFeatureEntry<>(feature, (FC)featureConfig, Float.valueOf(f));
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig featureConfig) {
		return new ConfiguredFeature<>(feature, (FC)featureConfig);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.features.stream().map(randomFeatureEntry -> randomFeatureEntry.serialize(dynamicOps).getValue()));
		T object2 = this.defaultFeature.serialize(dynamicOps).getValue();
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("features"), object, dynamicOps.createString("default"), object2))
		);
	}

	public static <T> RandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<RandomFeatureEntry<?>> list = dynamic.get("features").asList(RandomFeatureEntry::deserialize);
		ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
		return new RandomFeatureConfig(list, configuredFeature);
	}
}
