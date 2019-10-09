package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class RandomRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?, ?>> features;
	public final int count;

	public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> list, int i) {
		this.features = list;
		this.count = i;
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
		List<ConfiguredFeature<?, ?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
		int i = dynamic.get("count").asInt(0);
		return new RandomRandomFeatureConfig(list, i);
	}
}
