package net.minecraft.world.gen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ProbabilityConfig implements CarverConfig, FeatureConfig {
	public final float probability;

	public ProbabilityConfig(float probability) {
		this.probability = probability;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("probability"), ops.createFloat(this.probability))));
	}

	public static <T> ProbabilityConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new ProbabilityConfig(f);
	}

	public static ProbabilityConfig method_26623(Random random) {
		return new ProbabilityConfig(random.nextFloat() / 2.0F);
	}
}
