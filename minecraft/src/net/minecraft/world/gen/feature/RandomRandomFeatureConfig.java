package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?>> features;
	public final int count;

	public RandomRandomFeatureConfig(List<ConfiguredFeature<?>> features, int count) {
		this.features = features;
		this.count = count;
	}

	public RandomRandomFeatureConfig(Feature<?>[] features, FeatureConfig[] configs, int count) {
		this((List<ConfiguredFeature<?>>)IntStream.range(0, features.length).mapToObj(i -> configure(features[i], configs[i])).collect(Collectors.toList()), count);
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<?> configure(Feature<FC> feature, FeatureConfig config) {
		return new ConfiguredFeature<>(feature, (FC)config);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("features"),
					ops.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(ops).getValue())),
					ops.createString("count"),
					ops.createInt(this.count)
				)
			)
		);
	}

	public static <T> RandomRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<ConfiguredFeature<?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
		int i = dynamic.get("count").asInt(0);
		return new RandomRandomFeatureConfig(list, i);
	}
}
