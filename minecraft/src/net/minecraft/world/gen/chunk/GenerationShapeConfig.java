package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GenerationShapeConfig {
	public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 256).fieldOf("height").forGetter(GenerationShapeConfig::getHeight),
					NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(GenerationShapeConfig::getSampling),
					SlideConfig.CODEC.fieldOf("top_slide").forGetter(GenerationShapeConfig::getTopSlide),
					SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(GenerationShapeConfig::getBottomSlide),
					Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(GenerationShapeConfig::getSizeHorizontal),
					Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(GenerationShapeConfig::getSizeVertical),
					Codec.DOUBLE.fieldOf("density_factor").forGetter(GenerationShapeConfig::getDensityFactor),
					Codec.DOUBLE.fieldOf("density_offset").forGetter(GenerationShapeConfig::getDensityOffset),
					Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(GenerationShapeConfig::hasSimplexSurfaceNoise),
					Codec.BOOL
						.optionalFieldOf("random_density_offset", Boolean.valueOf(false), Lifecycle.experimental())
						.forGetter(GenerationShapeConfig::hasRandomDensityOffset),
					Codec.BOOL
						.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental())
						.forGetter(GenerationShapeConfig::hasIslandNoiseOverride),
					Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(GenerationShapeConfig::isAmplified)
				)
				.apply(instance, GenerationShapeConfig::new)
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

	public GenerationShapeConfig(
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
