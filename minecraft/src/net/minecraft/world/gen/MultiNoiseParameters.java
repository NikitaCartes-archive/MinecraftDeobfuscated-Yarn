package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class MultiNoiseParameters {
	public static final Codec<MultiNoiseParameters> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("temperature").forGetter(MultiNoiseParameters::temperature),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("humidity").forGetter(MultiNoiseParameters::humidity),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("continentalness").forGetter(MultiNoiseParameters::continentalness),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("erosion").forGetter(MultiNoiseParameters::erosion),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("weirdness").forGetter(MultiNoiseParameters::weirdness),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("shift").forGetter(MultiNoiseParameters::shift)
				)
				.apply(instance, MultiNoiseParameters::new)
	);
	private final DoublePerlinNoiseSampler.NoiseParameters temperature;
	private final DoublePerlinNoiseSampler.NoiseParameters humidity;
	private final DoublePerlinNoiseSampler.NoiseParameters continentalness;
	private final DoublePerlinNoiseSampler.NoiseParameters erosion;
	private final DoublePerlinNoiseSampler.NoiseParameters weirdness;
	private final DoublePerlinNoiseSampler.NoiseParameters shift;

	public MultiNoiseParameters(
		DoublePerlinNoiseSampler.NoiseParameters temperature,
		DoublePerlinNoiseSampler.NoiseParameters humidity,
		DoublePerlinNoiseSampler.NoiseParameters continentalness,
		DoublePerlinNoiseSampler.NoiseParameters erosion,
		DoublePerlinNoiseSampler.NoiseParameters weirdness,
		DoublePerlinNoiseSampler.NoiseParameters shift
	) {
		this.temperature = temperature;
		this.humidity = humidity;
		this.continentalness = continentalness;
		this.erosion = erosion;
		this.weirdness = weirdness;
		this.shift = shift;
	}

	public DoublePerlinNoiseSampler.NoiseParameters temperature() {
		return this.temperature;
	}

	public DoublePerlinNoiseSampler.NoiseParameters humidity() {
		return this.humidity;
	}

	public DoublePerlinNoiseSampler.NoiseParameters continentalness() {
		return this.continentalness;
	}

	public DoublePerlinNoiseSampler.NoiseParameters erosion() {
		return this.erosion;
	}

	public DoublePerlinNoiseSampler.NoiseParameters weirdness() {
		return this.weirdness;
	}

	public DoublePerlinNoiseSampler.NoiseParameters shift() {
		return this.shift;
	}
}
