package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeagrassFeatureConfig implements FeatureConfig {
	public final int count;
	public final double tallSeagrassProbability;

	public SeagrassFeatureConfig(int count, double tallSeagrassProbability) {
		this.count = count;
		this.tallSeagrassProbability = tallSeagrassProbability;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("count"), ops.createInt(this.count), ops.createString("tall_seagrass_probability"), ops.createDouble(this.tallSeagrassProbability)
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
