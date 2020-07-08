package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig implements DecoratorConfig {
	public static final Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT
						.fieldOf("noise_to_count_ratio")
						.forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio),
					Codec.DOUBLE.fieldOf("noise_factor").forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor),
					Codec.DOUBLE
						.fieldOf("noise_offset")
						.orElse(0.0)
						.forGetter(topSolidHeightmapNoiseBiasedDecoratorConfig -> topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset)
				)
				.apply(instance, TopSolidHeightmapNoiseBiasedDecoratorConfig::new)
	);
	public final int noiseToCountRatio;
	public final double noiseFactor;
	public final double noiseOffset;

	public TopSolidHeightmapNoiseBiasedDecoratorConfig(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
		this.noiseToCountRatio = noiseToCountRatio;
		this.noiseFactor = noiseFactor;
		this.noiseOffset = noiseOffset;
	}
}
