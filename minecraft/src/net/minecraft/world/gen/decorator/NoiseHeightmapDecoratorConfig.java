package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseHeightmapDecoratorConfig implements DecoratorConfig {
	public final double noiseLevel;
	public final int belowNoise;
	public final int aboveNoise;

	public NoiseHeightmapDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
		this.noiseLevel = noiseLevel;
		this.belowNoise = belowNoise;
		this.aboveNoise = aboveNoise;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("noise_level"),
					ops.createDouble(this.noiseLevel),
					ops.createString("below_noise"),
					ops.createInt(this.belowNoise),
					ops.createString("above_noise"),
					ops.createInt(this.aboveNoise)
				)
			)
		);
	}

	public static NoiseHeightmapDecoratorConfig deserialize(Dynamic<?> dynamic) {
		double d = dynamic.get("noise_level").asDouble(0.0);
		int i = dynamic.get("below_noise").asInt(0);
		int j = dynamic.get("above_noise").asInt(0);
		return new NoiseHeightmapDecoratorConfig(d, i, j);
	}
}
