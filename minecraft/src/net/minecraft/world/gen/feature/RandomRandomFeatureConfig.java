package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RandomRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?>> features;
	public final int count;

	public RandomRandomFeatureConfig(List<ConfiguredFeature<?>> list, int i) {
		this.features = list;
		this.count = i;
	}

	public RandomRandomFeatureConfig(Feature<?>[] features, FeatureConfig[] featureConfigs, int i) {
		this(
			(List<ConfiguredFeature<?>>)IntStream.range(0, features.length).mapToObj(ix -> configure(features[ix], featureConfigs[ix])).collect(Collectors.toList()), i
		);
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<?> configure(Feature<FC> feature, FeatureConfig featureConfig) {
		return new ConfiguredFeature<>(feature, (FC)featureConfig);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("features"),
					dynamicOps.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(dynamicOps).getValue())),
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.count)
				)
			)
		);
	}

	public static <T> RandomRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<ConfiguredFeature<?>> list = (List<ConfiguredFeature<?>>)((Stream)dynamic.get("features").flatMap(Dynamic::getStream).orElseGet(Stream::empty))
			.map(ConfiguredFeature::deserialize)
			.collect(Collectors.toList());
		int i = dynamic.getInt("count", 0);
		return new RandomRandomFeatureConfig(list, i);
	}
}
