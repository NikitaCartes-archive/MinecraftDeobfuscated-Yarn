package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PillagerOutpostFeatureConfig implements FeatureConfig {
	public final double probability;

	public PillagerOutpostFeatureConfig(double d) {
		this.probability = d;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability))));
	}

	public static <T> PillagerOutpostFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.getFloat("probability", 0.0F);
		return new PillagerOutpostFeatureConfig((double)f);
	}
}
