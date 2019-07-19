package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PillagerOutpostFeatureConfig implements FeatureConfig {
	public final double probability;

	public PillagerOutpostFeatureConfig(double probability) {
		this.probability = probability;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability))));
	}

	public static <T> PillagerOutpostFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new PillagerOutpostFeatureConfig((double)f);
	}
}
