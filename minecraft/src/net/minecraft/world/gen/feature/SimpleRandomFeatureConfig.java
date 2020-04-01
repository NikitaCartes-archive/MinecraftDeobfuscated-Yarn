package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?, ?>> features;

	public SimpleRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features) {
		this.features = features;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops, ops.createMap(ImmutableMap.of(ops.createString("features"), ops.createList(this.features.stream().map(feature -> feature.serialize(ops).getValue()))))
		);
	}

	public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<ConfiguredFeature<?, ?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
		return new SimpleRandomFeatureConfig(list);
	}

	public static SimpleRandomFeatureConfig method_26643(Random random) {
		return new SimpleRandomFeatureConfig(
			(List<ConfiguredFeature<?, ?>>)Util.method_26716(random, 1, 10, Registry.FEATURE).map(feature -> feature.method_26588(random)).collect(Collectors.toList())
		);
	}
}
