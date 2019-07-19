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

	public RandomFeatureConfig(List<RandomFeatureEntry<?>> features, ConfiguredFeature<?> defaultFeature) {
		this.features = features;
		this.defaultFeature = defaultFeature;
	}

	public RandomFeatureConfig(Feature<?>[] features, FeatureConfig[] configs, float[] chances, Feature<?> defaultFeature, FeatureConfig featureConfig) {
		this(
			(List<RandomFeatureEntry<?>>)IntStream.range(0, features.length).mapToObj(i -> makeEntry(features[i], configs[i], chances[i])).collect(Collectors.toList()),
			configure(defaultFeature, featureConfig)
		);
	}

	private static <FC extends FeatureConfig> RandomFeatureEntry<FC> makeEntry(Feature<FC> feature, FeatureConfig config, float chance) {
		return new RandomFeatureEntry<>(feature, (FC)config, Float.valueOf(chance));
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig config) {
		return new ConfiguredFeature<>(feature, (FC)config);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		T object = ops.createList(this.features.stream().map(randomFeatureEntry -> randomFeatureEntry.serialize(ops).getValue()));
		T object2 = this.defaultFeature.serialize(ops).getValue();
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("features"), object, ops.createString("default"), object2)));
	}

	public static <T> RandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<RandomFeatureEntry<?>> list = dynamic.get("features").asList(RandomFeatureEntry::deserialize);
		ConfiguredFeature<?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
		return new RandomFeatureConfig(list, configuredFeature);
	}
}
