package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.dimension.DimensionType;

public record GenerationShapeConfig(
	int minimumY,
	int height,
	NoiseSamplingConfig sampling,
	SlideConfig topSlide,
	SlideConfig bottomSlide,
	int horizontalSize,
	int verticalSize,
	@Deprecated boolean islandNoiseOverride,
	@Deprecated boolean amplified,
	@Deprecated boolean largeBiomes,
	VanillaTerrainParameters terrainParameters
) {
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
						VanillaTerrainParameters.CODEC.fieldOf("terrain_shaper").forGetter(GenerationShapeConfig::terrainParameters)
					)
					.apply(instance, GenerationShapeConfig::new)
		)
		.comapFlatMap(GenerationShapeConfig::checkHeight, Function.identity());
	static final GenerationShapeConfig field_37138 = create(
		0,
		128,
		new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
		new SlideConfig(0.9375, 3, 0),
		new SlideConfig(2.5, 4, -1),
		1,
		2,
		false,
		false,
		false,
		VanillaTerrainParametersCreator.createNetherParameters()
	);
	static final GenerationShapeConfig field_37139 = create(
		0,
		128,
		new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
		new SlideConfig(-23.4375, 64, -46),
		new SlideConfig(-0.234375, 7, 1),
		2,
		1,
		true,
		false,
		false,
		VanillaTerrainParametersCreator.createEndParameters()
	);
	static final GenerationShapeConfig field_37140 = create(
		-64,
		192,
		new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0),
		new SlideConfig(0.9375, 3, 0),
		new SlideConfig(2.5, 4, -1),
		1,
		2,
		false,
		false,
		false,
		VanillaTerrainParametersCreator.createCavesParameters()
	);
	static final GenerationShapeConfig field_37141 = create(
		0,
		256,
		new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0),
		new SlideConfig(-23.4375, 64, -46),
		new SlideConfig(-0.234375, 7, 1),
		2,
		1,
		false,
		false,
		false,
		VanillaTerrainParametersCreator.createFloatingIslandsParameters()
	);

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

	static GenerationShapeConfig method_41126(boolean bl, boolean bl2) {
		return create(
			-64,
			384,
			new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0),
			new SlideConfig(-0.078125, 2, bl ? 0 : 8),
			new SlideConfig(bl ? 0.4 : 0.1171875, 3, 0),
			1,
			2,
			false,
			bl,
			bl2,
			VanillaTerrainParametersCreator.createSurfaceParameters(bl)
		);
	}

	public int verticalBlockSize() {
		return BiomeCoords.toBlock(this.verticalSize());
	}

	public int horizontalBlockSize() {
		return BiomeCoords.toBlock(this.horizontalSize());
	}

	public int verticalBlockCount() {
		return this.height() / this.verticalBlockSize();
	}

	public int minimumBlockY() {
		return MathHelper.floorDiv(this.minimumY(), this.verticalBlockSize());
	}
}
