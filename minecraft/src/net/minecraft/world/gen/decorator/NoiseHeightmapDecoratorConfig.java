package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseHeightmapDecoratorConfig implements DecoratorConfig {
	public static final Codec<NoiseHeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("noise_level").forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.noiseLevel),
					Codec.INT.fieldOf("below_noise").forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.belowNoise),
					Codec.INT.fieldOf("above_noise").forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.aboveNoise)
				)
				.apply(instance, NoiseHeightmapDecoratorConfig::new)
	);
	public final double noiseLevel;
	public final int belowNoise;
	public final int aboveNoise;

	public NoiseHeightmapDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
		this.noiseLevel = noiseLevel;
		this.belowNoise = belowNoise;
		this.aboveNoise = aboveNoise;
	}
}
