package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;

public final class GenerationShapeConfig extends Record {
	private final int minimumY;
	private final int height;
	private final NoiseSamplingConfig sampling;
	private final SlideConfig topSlide;
	private final SlideConfig bottomSlide;
	private final int horizontalSize;
	private final int verticalSize;
	private final boolean islandNoiseOverride;
	private final boolean amplified;
	private final boolean largeBiomes;
	private final VanillaTerrainParameters terrainParameters;
	public static final Codec<GenerationShapeConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(GenerationShapeConfig::minimumY),
						Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height").forGetter(GenerationShapeConfig::height),
						NoiseSamplingConfig.CODEC.fieldOf("sampling").forGetter(GenerationShapeConfig::sampling),
						SlideConfig.CODEC.fieldOf("top_slide").forGetter(GenerationShapeConfig::topSlide),
						SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(GenerationShapeConfig::bottomSlide),
						Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(GenerationShapeConfig::horizontalSize),
						Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(GenerationShapeConfig::verticalSize),
						Codec.BOOL
							.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental())
							.forGetter(GenerationShapeConfig::islandNoiseOverride),
						Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(GenerationShapeConfig::amplified),
						Codec.BOOL.optionalFieldOf("large_biomes", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(GenerationShapeConfig::largeBiomes),
						VanillaTerrainParameters.field_35456.fieldOf("terrain_shaper").forGetter(GenerationShapeConfig::terrainParameters)
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
		boolean bl,
		boolean bl2,
		boolean bl3,
		VanillaTerrainParameters vanillaTerrainParameters
	) {
		this.minimumY = minimumY;
		this.height = height;
		this.sampling = sampling;
		this.topSlide = topSlide;
		this.bottomSlide = bottomSlide;
		this.horizontalSize = horizontalSize;
		this.verticalSize = verticalSize;
		this.islandNoiseOverride = bl;
		this.amplified = bl2;
		this.largeBiomes = bl3;
		this.terrainParameters = vanillaTerrainParameters;
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
		boolean islandNoiseOverride,
		boolean amplified,
		boolean largeBiomes,
		VanillaTerrainParameters terrainParameters
	) {
		GenerationShapeConfig generationShapeConfig = new GenerationShapeConfig(
			minimumY, height, sampling, topSlide, bottomSlide, horizontalSize, verticalSize, islandNoiseOverride, amplified, largeBiomes, terrainParameters
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

	@Deprecated
	public boolean largeBiomes() {
		return this.largeBiomes;
	}

	public int method_39545() {
		return BiomeCoords.toBlock(this.verticalSize());
	}

	public int method_39546() {
		return BiomeCoords.toBlock(this.horizontalSize());
	}

	public int method_39547() {
		return this.height() / this.method_39545();
	}

	public int method_39548() {
		return MathHelper.floorDiv(this.minimumY(), this.method_39545());
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;islandNoiseOverride;isAmplified;largeBiomes;terrainShaper",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified,GenerationShapeConfig::largeBiomes,GenerationShapeConfig::terrainParameters>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;islandNoiseOverride;isAmplified;largeBiomes;terrainShaper",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified,GenerationShapeConfig::largeBiomes,GenerationShapeConfig::terrainParameters>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",GenerationShapeConfig,"minY;height;noiseSamplingSettings;topSlideSettings;bottomSlideSettings;noiseSizeHorizontal;noiseSizeVertical;islandNoiseOverride;isAmplified;largeBiomes;terrainShaper",GenerationShapeConfig::minimumY,GenerationShapeConfig::height,GenerationShapeConfig::sampling,GenerationShapeConfig::topSlide,GenerationShapeConfig::bottomSlide,GenerationShapeConfig::horizontalSize,GenerationShapeConfig::verticalSize,GenerationShapeConfig::islandNoiseOverride,GenerationShapeConfig::amplified,GenerationShapeConfig::largeBiomes,GenerationShapeConfig::terrainParameters>(
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

	public VanillaTerrainParameters terrainParameters() {
		return this.terrainParameters;
	}
}
