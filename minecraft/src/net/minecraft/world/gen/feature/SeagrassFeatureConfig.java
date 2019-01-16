package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeagrassFeatureConfig implements FeatureConfig {
	public final int count;
	public final double tallSeagrassProbability;

	public SeagrassFeatureConfig(int i, double d) {
		this.count = i;
		this.tallSeagrassProbability = d;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.count),
					dynamicOps.createString("tall_seagrass_probability"),
					dynamicOps.createDouble(this.tallSeagrassProbability)
				)
			)
		);
	}

	public static <T> SeagrassFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.get("count").asInt(0);
		double d = dynamic.get("tall_seagrass_probability").asDouble(0.0);
		return new SeagrassFeatureConfig(i, d);
	}
}
