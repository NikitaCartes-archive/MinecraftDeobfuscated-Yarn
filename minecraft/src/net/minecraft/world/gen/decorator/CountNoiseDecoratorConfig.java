package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CountNoiseDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountNoiseDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.DOUBLE.fieldOf("noise_level").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.noiseLevel),
					Codec.INT.fieldOf("below_noise").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.belowNoise),
					Codec.INT.fieldOf("above_noise").forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.aboveNoise)
				)
				.apply(instance, CountNoiseDecoratorConfig::new)
	);
	public final double noiseLevel;
	public final int belowNoise;
	public final int aboveNoise;

	public CountNoiseDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
		this.noiseLevel = noiseLevel;
		this.belowNoise = belowNoise;
		this.aboveNoise = aboveNoise;
	}
}
