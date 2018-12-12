package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseHeightmapDecoratorConfig implements DecoratorConfig {
	public final double noiseLevel;
	public final int belowNoise;
	public final int aboveNoise;

	public NoiseHeightmapDecoratorConfig(double d, int i, int j) {
		this.noiseLevel = d;
		this.belowNoise = i;
		this.aboveNoise = j;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("noise_level"),
					dynamicOps.createDouble(this.noiseLevel),
					dynamicOps.createString("below_noise"),
					dynamicOps.createInt(this.belowNoise),
					dynamicOps.createString("above_noise"),
					dynamicOps.createInt(this.aboveNoise)
				)
			)
		);
	}

	public static NoiseHeightmapDecoratorConfig deserialize(Dynamic<?> dynamic) {
		double d = dynamic.getDouble("noise_level", 0.0);
		int i = dynamic.getInt("below_noise", 0);
		int j = dynamic.getInt("above_noise", 0);
		return new NoiseHeightmapDecoratorConfig(d, i, j);
	}
}
