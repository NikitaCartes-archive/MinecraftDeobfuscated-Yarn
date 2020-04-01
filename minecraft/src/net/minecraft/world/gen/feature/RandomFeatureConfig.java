package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class RandomFeatureConfig implements FeatureConfig {
	public final List<RandomFeatureEntry<?>> features;
	public final ConfiguredFeature<?, ?> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry<?>> features, ConfiguredFeature<?, ?> defaultFeature) {
		this.features = features;
		this.defaultFeature = defaultFeature;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		T object = ops.createList(this.features.stream().map(feature -> feature.serialize(ops).getValue()));
		T object2 = this.defaultFeature.serialize(ops).getValue();
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("features"), object, ops.createString("default"), object2)));
	}

	public static <T> RandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<RandomFeatureEntry<?>> list = dynamic.get("features").asList(RandomFeatureEntry::deserialize);
		ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
		return new RandomFeatureConfig(list, configuredFeature);
	}

	public static RandomFeatureConfig method_26625(Random random) {
		return new RandomFeatureConfig(
			(List<RandomFeatureEntry<?>>)Util.method_26718(random, 10, Registry.FEATURE)
				.map(feature -> new RandomFeatureEntry(feature.method_26588(random), random.nextFloat()))
				.collect(Collectors.toList()),
			Registry.FEATURE.getRandom(random).method_26588(random)
		);
	}
}
