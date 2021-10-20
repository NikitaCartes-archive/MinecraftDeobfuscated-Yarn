package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import net.minecraft.world.dimension.DimensionType;

public final class GenerationShapeConfig extends Record {
	private final int minimumY;
	private final int height;
	private final NoiseSamplingConfig sampling;
	private final SlideConfig topSlide;
	private final SlideConfig bottomSlide;
	private final int horizontalSize;
	private final int verticalSize;
	private final double densityFactor;
	private final double densityOffset;
	private final boolean islandNoiseOverride;
	private final boolean amplified;
	public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(GenerationShapeConfig::minimumY),
						Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height").forGetter(GenerationShapeConfig::height),
						NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(GenerationShapeConfig::sampling),
						SlideConfig.CODEC.fieldOf("top_slide").forGetter(GenerationShapeConfig::topSlide),
						SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(GenerationShapeConfig::bottomSlide),
						Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(GenerationShapeConfig::horizontalSize),
						Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(GenerationShapeConfig::verticalSize),
						Codec.DOUBLE.fieldOf("density_factor").forGetter(GenerationShapeConfig::densityFactor),
						Codec.DOUBLE.fieldOf("density_offset").forGetter(GenerationShapeConfig::densityOffset),
						Codec.BOOL
							.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental())
							.forGetter(GenerationShapeConfig::islandNoiseOverride),
						Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(GenerationShapeConfig::amplified)
					)
					.apply(instance, GenerationShapeConfig::new)
		)
		.comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());

	public GenerationShapeConfig(
		int minimumY,
		int height,
		NoiseSamplingConfig sampling,
		SlideConfig topSlide,
		SlideConfig bottomSlide,
		int horizontalSize,
		int verticalSize,
		double densityFactor,
		double densityOffset,
		boolean simplexSurfaceNoise,
		boolean randomDensityOffset
	) {
		this.minimumY = minimumY;
		this.height = height;
		this.sampling = sampling;
		this.topSlide = topSlide;
		this.bottomSlide = bottomSlide;
		this.horizontalSize = horizontalSize;
		this.verticalSize = verticalSize;
		this.densityFactor = densityFactor;
		this.densityOffset = densityOffset;
		this.islandNoiseOverride = simplexSurfaceNoise;
		this.amplified = randomDensityOffset;
	}

	private static DataResult<GenerationShapeConfig> checkHeight(GenerationShapeConfig config) {
		if (config.minimumY() + config.height() > DimensionType.MAX_COLUMN_HEIGHT + 1) {
			return DataResult.error("min_y + height cannot be higher than: " + (DimensionType.MAX_COLUMN_HEIGHT + 1));
		} else if (config.height() % 16 != 0) {
			return DataResult.error("height has to be a multiple of 16");
		} else {
			return config.minimumY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(config);
		}
	}

	public static GenerationShapeConfig create(
		int minimumY,
		int height,
		NoiseSamplingConfig sampling,
		SlideConfig topSlide,
		SlideConfig bottomSlide,
		int horizontalSize,
		int verticalSize,
		double densityFactor,
		double densityOffset,
		boolean simplexSurfaceNoise,
		boolean randomDensityOffset
	) {
		GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(
			minimumY, height, sampling, topSlide, bottomSlide, horizontalSize, verticalSize, densityFactor, densityOffset, simplexSurfaceNoise, randomDensityOffset
		);
		checkHeight(generationShapeConfig).error().ifPresent(partialResult -> {
			throw new IllegalStateException(partialResult.message());
		});
		return generationShapeConfig;
	}

	@Deprecated
	public boolean islandNoiseOverride() {
		return this.islandNoiseOverride;
	}

	@Deprecated
	public boolean amplified() {
		return this.amplified;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;densityFactor;densityOffset;islandNoiseOverride;isAmplified",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::densityFactor,GenerationShapeConfig::densityOffset,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;densityFactor;densityOffset;islandNoiseOverride;isAmplified",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::densityFactor,GenerationShapeConfig::densityOffset,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;densityFactor;densityOffset;islandNoiseOverride;isAmplified",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::densityFactor,GenerationShapeConfig::densityOffset,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified>(
			this, object
		);
	}

	public int minimumY() {
		return this.minimumY;
	}

	public int height() {
		return this.height;
	}

	public NoiseSamplingConfig sampling() {
		return this.sampling;
	}

	public SlideConfig topSlide() {
		return this.topSlide;
	}

	public SlideConfig bottomSlide() {
		return this.bottomSlide;
	}

	public int horizontalSize() {
		return this.horizontalSize;
	}

	public int verticalSize() {
		return this.verticalSize;
	}

	public double densityFactor() {
		return this.densityFactor;
	}

	public double densityOffset() {
		return this.densityOffset;
	}
}
