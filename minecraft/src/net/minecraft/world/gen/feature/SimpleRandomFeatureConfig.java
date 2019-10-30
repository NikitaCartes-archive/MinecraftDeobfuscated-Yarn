package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public final List<ConfiguredFeature<?, ?>> features;

	public SimpleRandomFeatureConfig(List<ConfiguredFeature<?, ?>> list) {
		this.features = list;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("features"), ops.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(ops).getValue())))
			)
		);
	}

	public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
		List<ConfiguredFeature<?, ?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
		return new SimpleRandomFeatureConfig(list);
	}
}
