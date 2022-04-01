package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NoiseSamplingConfig(double xzScale, double yScale, double xzFactor, double yFactor) {
	private static final Codec<Double> CODEC_RANGE = Codec.doubleRange(0.001, 1000.0);
	public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CODEC_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::xzScale),
					CODEC_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingConfig::yScale),
					CODEC_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::xzFactor),
					CODEC_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingConfig::yFactor)
				)
				.apply(instance, NoiseSamplingConfig::new)
	);
}
