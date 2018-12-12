package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeaPickleFeatureConfig implements FeatureConfig {
	public final int count;

	public SeaPickleFeatureConfig(int i) {
		this.count = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
	}

	public static <T> SeaPickleFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.getInt("count", 0);
		return new SeaPickleFeatureConfig(i);
	}
}
