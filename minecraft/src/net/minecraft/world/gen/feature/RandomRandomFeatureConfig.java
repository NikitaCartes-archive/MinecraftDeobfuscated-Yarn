package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class RandomRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?, ?>> features;
	public final int count;

	public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features, int count) {
		this.features = features;
		this.count = count;
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
		List<ConfiguredFeature<?, ?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
		int i = dynamic.get("count").asInt(0);
		return new RandomRandomFeatureConfig(list, i);
	}

	public static RandomRandomFeatureConfig method_26628(Random random) {
		return new RandomRandomFeatureConfig(
			(List<ConfiguredFeature<?, ?>>)Util.method_26716(random, 1, 10, Registry.FEATURE).map(feature -> feature.method_26588(random)).collect(Collectors.toList()),
			random.nextInt(5) + 3
		);
	}
}
