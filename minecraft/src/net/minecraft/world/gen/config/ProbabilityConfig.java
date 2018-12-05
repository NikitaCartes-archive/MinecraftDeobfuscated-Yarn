package net.minecraft.world.gen.config;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.config.carver.CarverConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;

public class ProbabilityConfig implements CarverConfig, FeatureConfig {
	public final float probability;

	public ProbabilityConfig(float f) {
		this.probability = f;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
	}

	public static <T> ProbabilityConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.getFloat("probability", 0.0F);
		return new ProbabilityConfig(f);
	}
}
