package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.NumberCodecs;

public class NoiseSamplingConfig {
	private static final Codec<Double> field_25188 = NumberCodecs.rangedDouble(0.001, 1000.0);
	public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					field_25188.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::getXZScale),
					field_25188.fieldOf("y_scale").forGetter(NoiseSamplingConfig::getYScale),
					field_25188.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::getXZFactor),
					field_25188.fieldOf("y_factor").forGetter(NoiseSamplingConfig::getYFactor)
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
