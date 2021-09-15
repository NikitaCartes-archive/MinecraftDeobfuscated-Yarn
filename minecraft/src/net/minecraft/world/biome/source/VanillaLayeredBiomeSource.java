package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class VanillaLayeredBiomeSource {
	public static final Codec<VanillaLayeredBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("temperature").forGetter(VanillaLayeredBiomeSource::method_38365),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("humidity").forGetter(VanillaLayeredBiomeSource::method_38367),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("continentalness").forGetter(VanillaLayeredBiomeSource::method_38368),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("erosion").forGetter(VanillaLayeredBiomeSource::method_38369),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("weirdness").forGetter(VanillaLayeredBiomeSource::method_38370),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("shift").forGetter(VanillaLayeredBiomeSource::method_38371)
				)
				.apply(instance, VanillaLayeredBiomeSource::new)
	);
	private final DoublePerlinNoiseSampler.NoiseParameters field_34626;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34627;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34628;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34629;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34630;
	private final DoublePerlinNoiseSampler.NoiseParameters field_34631;

	public VanillaLayeredBiomeSource(
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters2,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters3,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters4,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters5,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters6
	) {
		this.field_34626 = noiseParameters;
		this.field_34627 = noiseParameters2;
		this.field_34628 = noiseParameters3;
		this.field_34629 = noiseParameters4;
		this.field_34630 = noiseParameters5;
		this.field_34631 = noiseParameters6;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38365() {
		return this.field_34626;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38367() {
		return this.field_34627;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38368() {
		return this.field_34628;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38369() {
		return this.field_34629;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38370() {
		return this.field_34630;
	}

	public DoublePerlinNoiseSampler.NoiseParameters method_38371() {
		return this.field_34631;
	}
}
