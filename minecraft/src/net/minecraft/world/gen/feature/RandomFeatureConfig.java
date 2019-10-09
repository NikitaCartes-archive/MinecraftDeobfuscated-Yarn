package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class RandomFeatureConfig implements FeatureConfig {
	public final List<RandomFeatureEntry<?>> features;
	public final ConfiguredFeature<?, ?> defaultFeature;

	public RandomFeatureConfig(List<RandomFeatureEntry<?>> list, ConfiguredFeature<?, ?> configuredFeature) {
		this.features = list;
		this.defaultFeature = configuredFeature;
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
		ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeature.deserialize(dynamic.get("default").orElseEmptyMap());
		return new RandomFeatureConfig(list, configuredFeature);
	}
}
