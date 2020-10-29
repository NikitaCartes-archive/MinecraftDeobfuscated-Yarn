package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingConfig {
	private static final Codec<Double> CODEC_RANGE = Codec.doubleRange(0.001, 1000.0);
	public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CODEC_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::getXZScale),
					CODEC_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingConfig::getYScale),
					CODEC_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::getXZFactor),
					CODEC_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingConfig::getYFactor)
				)
				.apply(instance, NoiseSamplingConfig::new)
	);
	private final double xzScale;
	private final double yScale;
	private final double xzFactor;
	private final double yFactor;

	public NoiseSamplingConfig(double xzScale, double yScale, double xzFactor, double yFactor) {
		this.xzScale = xzScale;
		this.yScale = yScale;
		this.xzFactor = xzFactor;
		this.yFactor = yFactor;
	}

	public double getXZScale() {
		return this.xzScale;
	}

	public double getYScale() {
		return this.yScale;
	}

	public double getXZFactor() {
		return this.xzFactor;
	}

	public double getYFactor() {
		return this.yFactor;
	}
}
