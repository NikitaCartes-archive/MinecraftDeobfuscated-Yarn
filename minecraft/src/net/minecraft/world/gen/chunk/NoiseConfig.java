package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseConfig {
	public static final Codec<NoiseConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 256).fieldOf("height").forGetter(NoiseConfig::getHeight),
					NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(NoiseConfig::getSampling),
					SlideConfig.CODEC.fieldOf("top_slide").forGetter(NoiseConfig::getTopSlide),
					SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(NoiseConfig::getBottomSlide),
					Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseConfig::getSizeHorizontal),
					Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseConfig::getSizeVertical),
					Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseConfig::getDensityFactor),
					Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseConfig::getDensityOffset),
					Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseConfig::hasSimplexSurfaceNoise),
					Codec.BOOL.optionalFieldOf("random_density_offset", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseConfig::hasRandomDensityOffset),
					Codec.BOOL.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseConfig::hasIslandNoiseOverride),
					Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseConfig::isAmplified)
				)
				.apply(instance, NoiseConfig::new)
	);
	private final int height;
	private final NoiseSamplingConfig sampling;
	private final SlideConfig topSlide;
	private final SlideConfig bottomSlide;
	private final int horizontalSize;
	private final int verticalSize;
	private final double densityFactor;
	private final double densityOffset;
	private final boolean simplexSurfaceNoise;
	private final boolean randomDensityOffset;
	private final boolean islandNoiseOverride;
	private final boolean amplified;

	public NoiseConfig(
		int height,
		NoiseSamplingConfig sampling,
		SlideConfig topSlide,
		SlideConfig bottomSlide,
		int sizeHorizontal,
		int sizeVertical,
		double densityFactor,
		double densityOffset,
		boolean simplexSurfaceNoise,
		boolean randomDensityOffset,
		boolean islandNoiseOverride,
		boolean amplified
	) {
		this.height = height;
		this.sampling = sampling;
		this.topSlide = topSlide;
		this.bottomSlide = bottomSlide;
		this.horizontalSize = sizeHorizontal;
		this.verticalSize = sizeVertical;
		this.densityFactor = densityFactor;
		this.densityOffset = densityOffset;
		this.simplexSurfaceNoise = simplexSurfaceNoise;
		this.randomDensityOffset = randomDensityOffset;
		this.islandNoiseOverride = islandNoiseOverride;
		this.amplified = amplified;
	}

	public int getHeight() {
		return this.height;
	}

	public NoiseSamplingConfig getSampling() {
		return this.sampling;
	}

	public SlideConfig getTopSlide() {
		return this.topSlide;
	}

	public SlideConfig getBottomSlide() {
		return this.bottomSlide;
	}

	public int getSizeHorizontal() {
		return this.horizontalSize;
	}

	public int getSizeVertical() {
		return this.verticalSize;
	}

	public double getDensityFactor() {
		return this.densityFactor;
	}

	public double getDensityOffset() {
		return this.densityOffset;
	}

	@Deprecated
	public boolean hasSimplexSurfaceNoise() {
		return this.simplexSurfaceNoise;
	}

	@Deprecated
	public boolean hasRandomDensityOffset() {
		return this.randomDensityOffset;
	}

	@Deprecated
	public boolean hasIslandNoiseOverride() {
		return this.islandNoiseOverride;
	}

	@Deprecated
	public boolean isAmplified() {
		return this.amplified;
	}
}
