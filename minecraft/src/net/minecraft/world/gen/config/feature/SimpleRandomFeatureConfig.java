package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?>> features;

	public SimpleRandomFeatureConfig(List<ConfiguredFeature<?>> list) {
		this.features = list;
	}

	public SimpleRandomFeatureConfig(Feature<?>[] features, FeatureConfig[] featureConfigs) {
		this((List<ConfiguredFeature<?>>)IntStream.range(0, features.length).mapToObj(i -> configure(features[i], featureConfigs[i])).collect(Collectors.toList()));
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig featureConfig) {
		return new ConfiguredFeature<>(feature, (FC)featureConfig);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("features"),
					dynamicOps.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(dynamicOps).getValue()))
				)
			)
		);
	}

	public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<ConfiguredFeature<?>> list = (List<ConfiguredFeature<?>>)((Stream)dynamic.get("features").flatMap(Dynamic::getStream).orElseGet(Stream::empty))
			.map(ConfiguredFeature::deserialize)
			.collect(Collectors.toList());
		return new SimpleRandomFeatureConfig(list);
	}
}
