package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

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
}
