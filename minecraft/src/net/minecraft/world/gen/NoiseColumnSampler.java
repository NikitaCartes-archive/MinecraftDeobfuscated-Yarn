package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler implements MultiNoiseUtil.MultiNoiseSampler {
	private static final float field_34658 = 1.0F;
	private static final float field_34668 = 0.08F;
	private static final float field_34669 = 0.4F;
	private static final double field_34670 = 1.5;
	private static final int field_34671 = 20;
	private static final double field_34672 = 0.2;
	private static final float field_34673 = 0.7F;
	private static final float field_34674 = 0.1F;
	private static final float field_34675 = 0.3F;
	private static final float field_34676 = 0.6F;
	private static final float field_34677 = 0.02F;
	private static final float field_34678 = -0.3F;
	private static final double field_34679 = 1.5;
	private static final DoublePerlinNoiseSampler.NoiseParameters AQUIFER_BARRIER_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters AQUIFER_FLUID_LEVEL_FLOODEDNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(
		-7, 1.0
	);
	private static final DoublePerlinNoiseSampler.NoiseParameters AQUIFER_LAVA_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-1, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters AQUIFER_FLUID_LEVEL_SPREAD_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(
		-4, 1.0
	);
	private static final DoublePerlinNoiseSampler.NoiseParameters PILLAR_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters PILLAR_RARENESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters PILLAR_THICKNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_2D_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_2D_ELEVATION_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_2D_MODULATOR_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-11, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_2D_THICKNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-11, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_3D_FIRST_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_3D_SECOND_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_3D_RARITY_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-11, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_3D_THICKNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_ROUGHNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-5, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SPAGHETTI_ROUGHNESS_MODULATOR_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(
		-8, 1.0
	);
	private static final DoublePerlinNoiseSampler.NoiseParameters CAVE_ENTRANCE_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 0.4, 0.5, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters CAVE_LAYER_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters CAVE_CHEESE_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(
		-8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0
	);
	private static final DoublePerlinNoiseSampler.NoiseParameters JAGGED_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(
		-16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0
	);
	private static final DoublePerlinNoiseSampler.NoiseParameters ORE_FREQUENCY_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters FIRST_ORE_PLACEMENT_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters SECOND_ORE_PLACEMENT_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters ORE_GAP_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-5, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters NOODLE_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters NOODLE_THICKNESS_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters NOODLE_RIDGE_FIRST_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private static final DoublePerlinNoiseSampler.NoiseParameters NOODLE_RIDGE_SECOND_NOISE_PARAMETERS = new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0);
	private final int verticalNoiseResolution;
	private final int noiseSizeY;
	private final GenerationShapeConfig config;
	private final double densityFactor;
	private final double densityOffset;
	private final int minY;
	private final VanillaTerrainParameters terrainParameters = new VanillaTerrainParameters();
	private final boolean hasNoiseCaves;
	private final ChunkNoiseSampler.ValueSamplerFactory intialNoiseSampler;
	private final InterpolatedNoiseSampler terrainNoise;
	@Nullable
	private final SimplexNoiseSampler islandNoise;
	private final DoublePerlinNoiseSampler jaggedNoise;
	private final DoublePerlinNoiseSampler aquiferBarrierNoise;
	private final DoublePerlinNoiseSampler aquiferFluidLevelFloodednessNoise;
	private final DoublePerlinNoiseSampler aquiferFluidLevelSpreadNoise;
	private final DoublePerlinNoiseSampler aquiferLavaNoise;
	private final DoublePerlinNoiseSampler caveLayerNoise;
	private final DoublePerlinNoiseSampler pillarNoise;
	private final DoublePerlinNoiseSampler pillarRarenessNoise;
	private final DoublePerlinNoiseSampler pillarThicknessNoise;
	private final DoublePerlinNoiseSampler spaghetti2dNoise;
	private final DoublePerlinNoiseSampler spaghetti2dElevationNoise;
	private final DoublePerlinNoiseSampler spaghetti2dModulatorNoise;
	private final DoublePerlinNoiseSampler spaghetti2dThicknessNoise;
	private final DoublePerlinNoiseSampler spaghetti3dFirstNoise;
	private final DoublePerlinNoiseSampler spaghetti3dSecondNoise;
	private final DoublePerlinNoiseSampler spaghetti3dRarityNoise;
	private final DoublePerlinNoiseSampler spaghetti3dThicknessNoise;
	private final DoublePerlinNoiseSampler spaghettiRoughnessNoise;
	private final DoublePerlinNoiseSampler spaghettiRoughnessModulatorNoise;
	private final DoublePerlinNoiseSampler caveEntranceNoise;
	private final DoublePerlinNoiseSampler caveCheeseNoise;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler continentalnessNoise;
	private final DoublePerlinNoiseSampler erosionNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final DoublePerlinNoiseSampler shiftNoise;
	private final DoublePerlinNoiseSampler oreGapNoise;
	private final DoublePerlinNoiseSampler oreFrequencyNoiseSampler;
	private final ChunkNoiseSampler.ValueSamplerFactory oreFrequencyNoiseSamplerFactory;
	private final DoublePerlinNoiseSampler firstOrePlacementNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory firstOrePlacementNoiseFactory;
	private final DoublePerlinNoiseSampler secondOrePlacementNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory secondOrePlacementNoiseFactory;
	private final DoublePerlinNoiseSampler noodleNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleNoiseFactory;
	private final DoublePerlinNoiseSampler noodleThicknessNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleThicknessNoiseFactory;
	private final DoublePerlinNoiseSampler noodleRidgeFirstNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleRidgeFirstNoiseFactory;
	private final DoublePerlinNoiseSampler noodleRidgeSecondNoise;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleRidgeSecondNoiseFactory;
	private final RandomDeriver aquiferRandomDeriver;
	private final RandomDeriver oreRandomDeriver;
	private final RandomDeriver depthBasedLayerRandomDeriver;

	public NoiseColumnSampler(
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int noiseSizeY,
		GenerationShapeConfig config,
		MultiNoiseParameters noiseParameters,
		boolean hasNoiseCaves,
		long seed,
		ChunkRandom.RandomProvider randomProvider
	) {
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.noiseSizeY = noiseSizeY;
		this.config = config;
		this.densityFactor = config.densityFactor();
		this.densityOffset = config.densityOffset();
		int i = config.minimumY();
		this.minY = MathHelper.floorDiv(i, verticalNoiseResolution);
		this.hasNoiseCaves = hasNoiseCaves;
		this.intialNoiseSampler = chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(
				(x, y, z) -> this.sampleNoiseColumn(x, y, z, chunkNoiseSampler.getTerrainNoisePoint(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(z)))
			);
		if (config.islandNoiseOverride()) {
			AbstractRandom abstractRandom = randomProvider.create(seed);
			abstractRandom.skip(17292);
			this.islandNoise = new SimplexNoiseSampler(abstractRandom);
		} else {
			this.islandNoise = null;
		}

		int j = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
		int k = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
		float f = 4.0F;
		double d = 2.6666666666666665;
		int l = i + 4;
		int m = i + config.height();
		if (randomProvider != ChunkRandom.RandomProvider.LEGACY) {
			RandomDeriver randomDeriver = randomProvider.create(seed).createBlockPosRandomDeriver();
			this.terrainNoise = new InterpolatedNoiseSampler(
				randomDeriver.createRandom("terrain"), config.sampling(), horizontalNoiseResolution, verticalNoiseResolution
			);
			this.aquiferBarrierNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("aquifer_barrier"), AQUIFER_BARRIER_NOISE_PARAMETERS);
			this.aquiferFluidLevelFloodednessNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("aquifer_fluid_level_floodedness"), AQUIFER_FLUID_LEVEL_FLOODEDNESS_NOISE_PARAMETERS
			);
			this.aquiferLavaNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("aquifer_lava"), AQUIFER_LAVA_NOISE_PARAMETERS);
			this.aquiferRandomDeriver = randomDeriver.createRandom("aquifer").createBlockPosRandomDeriver();
			this.aquiferFluidLevelSpreadNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("aquifer_fluid_level_spread"), AQUIFER_FLUID_LEVEL_SPREAD_NOISE_PARAMETERS
			);
			this.pillarNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("pillar"), PILLAR_NOISE_PARAMETERS);
			this.pillarRarenessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("pillar_rareness"), PILLAR_RARENESS_NOISE_PARAMETERS);
			this.pillarThicknessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("pillar_thickness"), PILLAR_THICKNESS_NOISE_PARAMETERS);
			this.spaghetti2dNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("spaghetti_2d"), SPAGHETTI_2D_NOISE_PARAMETERS);
			this.spaghetti2dElevationNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("spaghetti_2d_elevation"), SPAGHETTI_2D_ELEVATION_NOISE_PARAMETERS
			);
			this.spaghetti2dModulatorNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("spaghetti_2d_modulator"), SPAGHETTI_2D_MODULATOR_NOISE_PARAMETERS
			);
			this.spaghetti2dThicknessNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("spaghetti_2d_thickness"), SPAGHETTI_2D_THICKNESS_NOISE_PARAMETERS
			);
			this.spaghetti3dFirstNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("spaghetti_3d_1"), SPAGHETTI_3D_FIRST_NOISE_PARAMETERS);
			this.spaghetti3dSecondNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("spaghetti_3d_2"), SPAGHETTI_3D_SECOND_NOISE_PARAMETERS);
			this.spaghetti3dRarityNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("spaghetti_3d_rarity"), SPAGHETTI_3D_RARITY_NOISE_PARAMETERS);
			this.spaghetti3dThicknessNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("spaghetti_3d_thickness"), SPAGHETTI_3D_THICKNESS_NOISE_PARAMETERS
			);
			this.spaghettiRoughnessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("spaghetti_roughness"), SPAGHETTI_ROUGHNESS_NOISE_PARAMETERS);
			this.spaghettiRoughnessModulatorNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom("spaghetti_roughness_modulator"), SPAGHETTI_ROUGHNESS_MODULATOR_NOISE_PARAMETERS
			);
			this.caveEntranceNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("cave_entrance"), CAVE_ENTRANCE_NOISE_PARAMETERS);
			this.caveLayerNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("cave_layer"), CAVE_LAYER_NOISE_PARAMETERS);
			this.caveCheeseNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("cave_cheese"), CAVE_CHEESE_NOISE_PARAMETERS);
			this.temperatureNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("temperature"), noiseParameters.temperature());
			this.humidityNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("vegetation"), noiseParameters.humidity());
			this.continentalnessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("continentalness"), noiseParameters.continentalness());
			this.erosionNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("erosion"), noiseParameters.erosion());
			this.weirdnessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("ridge"), noiseParameters.weirdness());
			this.shiftNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("offset"), noiseParameters.shift());
			this.oreFrequencyNoiseSampler = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("ore_veininess"), ORE_FREQUENCY_NOISE_PARAMETERS);
			this.oreFrequencyNoiseSamplerFactory = createNoiseSamplerFactory(this.oreFrequencyNoiseSampler, j, k, 0, 1.5);
			this.firstOrePlacementNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("ore_vein_a"), FIRST_ORE_PLACEMENT_NOISE_PARAMETERS);
			this.firstOrePlacementNoiseFactory = createNoiseSamplerFactory(this.firstOrePlacementNoise, j, k, 0, 4.0);
			this.secondOrePlacementNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("ore_vein_b"), SECOND_ORE_PLACEMENT_NOISE_PARAMETERS);
			this.secondOrePlacementNoiseFactory = createNoiseSamplerFactory(this.secondOrePlacementNoise, j, k, 0, 4.0);
			this.oreGapNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("ore_gap"), ORE_GAP_NOISE_PARAMETERS);
			this.oreRandomDeriver = randomDeriver.createRandom("ore").createBlockPosRandomDeriver();
			this.noodleNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("noodle"), NOODLE_NOISE_PARAMETERS);
			this.noodleNoiseFactory = createNoiseSamplerFactory(this.noodleNoise, l, m, -1, 1.0);
			this.noodleThicknessNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("noodle_thickness"), NOODLE_THICKNESS_NOISE_PARAMETERS);
			this.noodleThicknessNoiseFactory = createNoiseSamplerFactory(this.noodleThicknessNoise, l, m, 0, 1.0);
			this.noodleRidgeFirstNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("noodle_ridge_a"), NOODLE_RIDGE_FIRST_NOISE_PARAMETERS);
			this.noodleRidgeFirstNoiseFactory = createNoiseSamplerFactory(this.noodleRidgeFirstNoise, l, m, 0, 2.6666666666666665);
			this.noodleRidgeSecondNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("noodle_ridge_b"), NOODLE_RIDGE_SECOND_NOISE_PARAMETERS);
			this.noodleRidgeSecondNoiseFactory = createNoiseSamplerFactory(this.noodleRidgeSecondNoise, l, m, 0, 2.6666666666666665);
			this.jaggedNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom("jagged"), JAGGED_NOISE_PARAMETERS);
			this.depthBasedLayerRandomDeriver = randomDeriver.createRandom("depth_based_layer").createBlockPosRandomDeriver();
		} else {
			AbstractRandom abstractRandom2 = randomProvider.create(seed);
			AbstractRandom abstractRandom3 = randomProvider.create(seed);
			AbstractRandom abstractRandom4 = config.useLegacyRandom() ? abstractRandom3 : abstractRandom2.derive();
			this.terrainNoise = new InterpolatedNoiseSampler(abstractRandom4, config.sampling(), horizontalNoiseResolution, verticalNoiseResolution);
			AbstractRandom abstractRandom5 = abstractRandom2.derive();
			this.aquiferBarrierNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom5.derive(), AQUIFER_BARRIER_NOISE_PARAMETERS);
			this.aquiferFluidLevelFloodednessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom5.derive(), AQUIFER_FLUID_LEVEL_FLOODEDNESS_NOISE_PARAMETERS);
			this.aquiferLavaNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom5.derive(), AQUIFER_LAVA_NOISE_PARAMETERS);
			this.aquiferRandomDeriver = abstractRandom5.createBlockPosRandomDeriver();
			this.aquiferFluidLevelSpreadNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom5.derive(), AQUIFER_FLUID_LEVEL_SPREAD_NOISE_PARAMETERS);
			AbstractRandom abstractRandom6 = abstractRandom2.derive();
			this.pillarNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), PILLAR_NOISE_PARAMETERS);
			this.pillarRarenessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), PILLAR_RARENESS_NOISE_PARAMETERS);
			this.pillarThicknessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), PILLAR_THICKNESS_NOISE_PARAMETERS);
			this.spaghetti2dNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_2D_NOISE_PARAMETERS);
			this.spaghetti2dElevationNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_2D_ELEVATION_NOISE_PARAMETERS);
			this.spaghetti2dModulatorNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_2D_MODULATOR_NOISE_PARAMETERS);
			this.spaghetti2dThicknessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_2D_THICKNESS_NOISE_PARAMETERS);
			this.spaghetti3dFirstNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_3D_FIRST_NOISE_PARAMETERS);
			this.spaghetti3dSecondNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_3D_SECOND_NOISE_PARAMETERS);
			this.spaghetti3dRarityNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_3D_RARITY_NOISE_PARAMETERS);
			this.spaghetti3dThicknessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_3D_THICKNESS_NOISE_PARAMETERS);
			this.spaghettiRoughnessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_ROUGHNESS_NOISE_PARAMETERS);
			this.spaghettiRoughnessModulatorNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), SPAGHETTI_ROUGHNESS_MODULATOR_NOISE_PARAMETERS);
			this.caveEntranceNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), CAVE_ENTRANCE_NOISE_PARAMETERS);
			this.caveLayerNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), CAVE_LAYER_NOISE_PARAMETERS);
			this.caveCheeseNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom6.derive(), CAVE_CHEESE_NOISE_PARAMETERS);
			this.temperatureNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed), noiseParameters.temperature());
			this.humidityNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 1L), noiseParameters.humidity());
			this.continentalnessNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 2L), noiseParameters.continentalness());
			this.erosionNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 3L), noiseParameters.erosion());
			this.weirdnessNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 4L), noiseParameters.weirdness());
			this.shiftNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 5L), noiseParameters.shift());
			this.jaggedNoise = DoublePerlinNoiseSampler.method_39123(randomProvider.create(seed + 6L), JAGGED_NOISE_PARAMETERS);
			AbstractRandom abstractRandom7 = abstractRandom2.derive();
			this.oreFrequencyNoiseSampler = DoublePerlinNoiseSampler.method_39123(abstractRandom7.derive(), ORE_FREQUENCY_NOISE_PARAMETERS);
			this.oreFrequencyNoiseSamplerFactory = createNoiseSamplerFactory(this.oreFrequencyNoiseSampler, j, k, 0, 1.5);
			this.firstOrePlacementNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom7.derive(), FIRST_ORE_PLACEMENT_NOISE_PARAMETERS);
			this.firstOrePlacementNoiseFactory = createNoiseSamplerFactory(this.firstOrePlacementNoise, j, k, 0, 4.0);
			this.secondOrePlacementNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom7.derive(), SECOND_ORE_PLACEMENT_NOISE_PARAMETERS);
			this.secondOrePlacementNoiseFactory = createNoiseSamplerFactory(this.secondOrePlacementNoise, j, k, 0, 4.0);
			this.oreGapNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom7.derive(), ORE_GAP_NOISE_PARAMETERS);
			this.oreRandomDeriver = abstractRandom7.createBlockPosRandomDeriver();
			AbstractRandom abstractRandom8 = abstractRandom2.derive();
			this.noodleNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom8.derive(), NOODLE_NOISE_PARAMETERS);
			this.noodleNoiseFactory = createNoiseSamplerFactory(this.noodleNoise, l, m, -1, 1.0);
			this.noodleThicknessNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom8.derive(), NOODLE_THICKNESS_NOISE_PARAMETERS);
			this.noodleThicknessNoiseFactory = createNoiseSamplerFactory(this.noodleThicknessNoise, l, m, 0, 1.0);
			this.noodleRidgeFirstNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom8.derive(), NOODLE_RIDGE_FIRST_NOISE_PARAMETERS);
			this.noodleRidgeFirstNoiseFactory = createNoiseSamplerFactory(this.noodleRidgeFirstNoise, l, m, 0, 2.6666666666666665);
			this.noodleRidgeSecondNoise = DoublePerlinNoiseSampler.method_39123(abstractRandom8.derive(), NOODLE_RIDGE_SECOND_NOISE_PARAMETERS);
			this.noodleRidgeSecondNoiseFactory = createNoiseSamplerFactory(this.noodleRidgeSecondNoise, l, m, 0, 2.6666666666666665);
			this.depthBasedLayerRandomDeriver = randomProvider.create(seed).createBlockPosRandomDeriver();
		}
	}

	private static ChunkNoiseSampler.ValueSamplerFactory createNoiseSamplerFactory(
		DoublePerlinNoiseSampler noiseSampler, int minY, int maxY, int alternative, double scale
	) {
		ChunkNoiseSampler.ColumnSampler columnSampler = (x, y, z) -> y <= maxY && y >= minY
				? noiseSampler.sample((double)x * scale, (double)y * scale, (double)z * scale)
				: (double)alternative;
		return chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(columnSampler);
	}

	private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point) {
		double d = this.terrainNoise.calculateNoise(x, y, z);
		boolean bl = !this.hasNoiseCaves;
		return this.sampleNoiseColumn(x, y, z, point, d, bl, true);
	}

	private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point, double noise, boolean hasNoNoiseCaves, boolean bl) {
		double d;
		if (this.densityFactor == 0.0 && this.densityOffset == -0.030078125) {
			d = 0.0;
		} else {
			double e = bl ? this.method_38409(point.peaks(), (double)x, (double)z) : 0.0;
			double f = this.getDepth((double)y);
			double g = (f + point.offset() + e) * point.factor();
			d = g * (double)(g > 0.0 ? 4 : 1);
		}

		double e = d + noise;
		double f = 1.5625;
		double h;
		double i;
		double g;
		if (!hasNoNoiseCaves && !(e < -64.0)) {
			double j = e - 1.5625;
			boolean bl2 = j < 0.0;
			double k = this.sampleCaveEntranceNoise(x, y, z);
			double l = this.sampleSpaghettiRoughnessNoise(x, y, z);
			double m = this.sampleSpaghetti3dNoise(x, y, z);
			double n = Math.min(k, m + l);
			if (bl2) {
				g = e;
				h = n * 5.0;
				i = -64.0;
			} else {
				double o = this.sampleCaveLayerNoise(x, y, z);
				if (o > 64.0) {
					g = 64.0;
				} else {
					double p = this.caveCheeseNoise.sample((double)x, (double)y / 1.5, (double)z);
					double q = MathHelper.clamp(p + 0.27, -1.0, 1.0);
					double r = j * 1.28;
					double s = q + MathHelper.clampedLerp(0.5, 0.0, r);
					g = s + o;
				}

				double p = this.sampleSpaghetti2dNoise(x, y, z);
				h = Math.min(n, p + l);
				i = this.samplePillarNoise(x, y, z);
			}
		} else {
			g = e;
			h = 64.0;
			i = -64.0;
		}

		double j = Math.max(Math.min(g, h), i);
		j = this.applySlides(j, y / this.verticalNoiseResolution);
		return MathHelper.clamp(j, -64.0, 64.0);
	}

	private double method_38409(double d, double e, double f) {
		if (d == 0.0) {
			return 0.0;
		} else {
			float g = 1500.0F;
			double h = this.jaggedNoise.sample(e * 1500.0, 0.0, f * 1500.0);
			return h > 0.0 ? d * h : d / 2.0 * h;
		}
	}

	private double getDepth(double d) {
		double e = 1.0 - d / 128.0;
		return e * this.densityFactor + this.densityOffset;
	}

	/**
	 * Interpolates the noise at the top and bottom of the world.
	 */
	private double applySlides(double noise, int y) {
		int i = y - this.minY;
		noise = this.config.topSlide().method_38414(noise, this.noiseSizeY - i);
		return this.config.bottomSlide().method_38414(noise, i);
	}

	public ChunkNoiseSampler.BlockStateSampler createInitialNoiseBlockStateSampler(
		ChunkNoiseSampler chunkNoiseSampler, ChunkNoiseSampler.ColumnSampler columnSampler, boolean hasNoodleCaves
	) {
		ChunkNoiseSampler.ValueSampler valueSampler = this.intialNoiseSampler.create(chunkNoiseSampler);
		ChunkNoiseSampler.ValueSampler valueSampler2 = hasNoodleCaves ? this.noodleNoiseFactory.create(chunkNoiseSampler) : () -> -1.0;
		ChunkNoiseSampler.ValueSampler valueSampler3 = hasNoodleCaves ? this.noodleThicknessNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
		ChunkNoiseSampler.ValueSampler valueSampler4 = hasNoodleCaves ? this.noodleRidgeFirstNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
		ChunkNoiseSampler.ValueSampler valueSampler5 = hasNoodleCaves ? this.noodleRidgeSecondNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
		return (x, y, z) -> {
			double d = valueSampler.sample();
			double e = MathHelper.clamp(d * 0.64, -1.0, 1.0);
			e = e / 2.0 - e * e * e / 24.0;
			if (valueSampler2.sample() >= 0.0) {
				double f = 0.05;
				double g = 0.1;
				double h = MathHelper.clampedLerpFromProgress(valueSampler3.sample(), -1.0, 1.0, 0.05, 0.1);
				double i = Math.abs(1.5 * valueSampler4.sample()) - h;
				double j = Math.abs(1.5 * valueSampler5.sample()) - h;
				e = Math.min(e, Math.max(i, j));
			}

			e += columnSampler.calculateNoise(x, y, z);
			return chunkNoiseSampler.getAquiferSampler().apply(x, y, z, d, e);
		};
	}

	public ChunkNoiseSampler.BlockStateSampler createOreVeinSampler(ChunkNoiseSampler chunkNoiseSampler, boolean hasOreVeins) {
		if (!hasOreVeins) {
			return (x, y, z) -> null;
		} else {
			ChunkNoiseSampler.ValueSampler valueSampler = this.oreFrequencyNoiseSamplerFactory.create(chunkNoiseSampler);
			ChunkNoiseSampler.ValueSampler valueSampler2 = this.firstOrePlacementNoiseFactory.create(chunkNoiseSampler);
			ChunkNoiseSampler.ValueSampler valueSampler3 = this.secondOrePlacementNoiseFactory.create(chunkNoiseSampler);
			BlockState blockState = null;
			return (x, y, z) -> {
				AbstractRandom abstractRandom = this.oreRandomDeriver.createRandom(x, y, z);
				double d = valueSampler.sample();
				NoiseColumnSampler.VeinType veinType = this.getVeinType(d, y);
				if (veinType == null) {
					return blockState;
				} else if (abstractRandom.nextFloat() > 0.7F) {
					return blockState;
				} else if (this.shouldPlaceOreVeinBlock(valueSampler2.sample(), valueSampler3.sample())) {
					double e = MathHelper.clampedLerpFromProgress(Math.abs(d), 0.4F, 0.6F, 0.1F, 0.3F);
					if ((double)abstractRandom.nextFloat() < e && this.oreGapNoise.sample((double)x, (double)y, (double)z) > -0.3F) {
						return abstractRandom.nextFloat() < 0.02F ? veinType.rawBlock : veinType.ore;
					} else {
						return veinType.stone;
					}
				} else {
					return blockState;
				}
			};
		}
	}

	public int method_38383(int x, int z, TerrainNoisePoint point) {
		for (int i = this.minY + this.noiseSizeY; i >= this.minY; i--) {
			int j = i * this.verticalNoiseResolution;
			double d = -0.703125;
			double e = this.sampleNoiseColumn(x, j, z, point, -0.703125, true, false);
			if (e > 0.390625) {
				return j;
			}
		}

		return Integer.MAX_VALUE;
	}

	public AquiferSampler createAquiferSampler(
		ChunkNoiseSampler chunkNoiseSampler, int x, int z, int minimumY, int height, AquiferSampler.FluidLevelSampler fluidLevelSampler, boolean hasAquifers
	) {
		if (!hasAquifers) {
			return AquiferSampler.seaLevel(fluidLevelSampler);
		} else {
			int i = ChunkSectionPos.getSectionCoord(x);
			int j = ChunkSectionPos.getSectionCoord(z);
			return AquiferSampler.aquifer(
				chunkNoiseSampler,
				new ChunkPos(i, j),
				this.aquiferBarrierNoise,
				this.aquiferFluidLevelFloodednessNoise,
				this.aquiferFluidLevelSpreadNoise,
				this.aquiferLavaNoise,
				this.aquiferRandomDeriver,
				this,
				minimumY * this.verticalNoiseResolution,
				height * this.verticalNoiseResolution,
				fluidLevelSampler
			);
		}
	}

	@Override
	public MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k) {
		double d = (double)i + this.sampleShiftNoise(i, 0, k);
		double e = (double)k + this.sampleShiftNoise(k, i, 0);
		float f = (float)this.sampleContinentalnessNoise(d, 0.0, e);
		float g = (float)this.sampleErosionNoise(d, 0.0, e);
		float h = (float)this.sampleWeirdnessNoise(d, 0.0, e);
		double l = (double)this.terrainParameters.getOffset(this.terrainParameters.createNoisePoint(f, g, h));
		return this.method_38378(i, j, k, d, e, f, g, h, l);
	}

	public MultiNoiseUtil.NoiseValuePoint method_38378(
		int x, int y, int z, double noiseX, double noiseZ, float continentalness, float erosion, float weirdness, double offset
	) {
		double d = (double)y + this.sampleShiftNoise(y, z, x);
		double e = this.getDepth((double)BiomeCoords.toBlock(y)) + offset;
		return MultiNoiseUtil.createNoiseValuePoint(
			(float)this.sampleTemperatureNoise(noiseX, d, noiseZ), (float)this.sampleHumidityNoise(noiseX, d, noiseZ), continentalness, erosion, (float)e, weirdness
		);
	}

	public TerrainNoisePoint createTerrainNoisePoint(int x, int z, float continentalness, float weirdness, float erosion) {
		if (this.islandNoise != null) {
			double d = (double)(TheEndBiomeSource.getNoiseAt(this.islandNoise, x / 8, z / 8) - 8.0F);
			double e;
			if (d > 0.0) {
				e = 0.001953125;
			} else {
				e = 0.0078125;
			}

			return new TerrainNoisePoint(d, e, 0.0);
		} else {
			VanillaTerrainParameters.NoisePoint noisePoint = this.terrainParameters.createNoisePoint(continentalness, erosion, weirdness);
			return new TerrainNoisePoint(
				(double)this.terrainParameters.getOffset(noisePoint),
				(double)this.terrainParameters.getFactor(noisePoint),
				(double)this.terrainParameters.getPeak(noisePoint)
			);
		}
	}

	public double sampleShiftNoise(int x, int y, int z) {
		return this.shiftNoise.sample((double)x, (double)y, (double)z) * 4.0;
	}

	public double sampleTemperatureNoise(double x, double y, double z) {
		return this.temperatureNoise.sample(x, 0.0, z);
	}

	public double sampleHumidityNoise(double x, double y, double z) {
		return this.humidityNoise.sample(x, 0.0, z);
	}

	public double sampleContinentalnessNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896((int)x * 4, (int)z * 4)) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(x / 2048.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34372) {
			double d = x * 0.005;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.continentalnessNoise.sample(x, y, z);
		}
	}

	public double sampleErosionNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896((int)x * 4, (int)z * 4)) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(z / 256.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.field_34372) {
			double d = z * 0.005;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.erosionNoise.sample(x, y, z);
		}
	}

	public double sampleWeirdnessNoise(double x, double y, double z) {
		return this.weirdnessNoise.sample(x, y, z);
	}

	private double sampleCaveEntranceNoise(int x, int y, int z) {
		double d = 0.75;
		double e = 0.5;
		double f = 0.37;
		double g = this.caveEntranceNoise.sample((double)x * 0.75, (double)y * 0.5, (double)z * 0.75) + 0.37;
		int i = -10;
		double h = (double)(y - -10) / 40.0;
		double j = 0.3;
		return g + MathHelper.clampedLerp(0.3, 0.0, h);
	}

	private double samplePillarNoise(int x, int y, int z) {
		double d = 0.0;
		double e = 2.0;
		double f = NoiseHelper.lerpFromProgress(this.pillarRarenessNoise, (double)x, (double)y, (double)z, 0.0, 2.0);
		double g = 0.0;
		double h = 1.1;
		double i = NoiseHelper.lerpFromProgress(this.pillarThicknessNoise, (double)x, (double)y, (double)z, 0.0, 1.1);
		i = Math.pow(i, 3.0);
		double j = 25.0;
		double k = 0.3;
		double l = this.pillarNoise.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
		l = i * (l * 2.0 - f);
		return l > 0.03 ? l : Double.NEGATIVE_INFINITY;
	}

	private double sampleCaveLayerNoise(int x, int y, int z) {
		double d = this.caveLayerNoise.sample((double)x, (double)(y * 8), (double)z);
		return MathHelper.square(d) * 4.0;
	}

	private double sampleSpaghetti3dNoise(int x, int y, int z) {
		double d = this.spaghetti3dRarityNoise.sample((double)(x * 2), (double)y, (double)(z * 2));
		double e = NoiseColumnSampler.CaveScaler.scaleTunnels(d);
		double f = 0.065;
		double g = 0.088;
		double h = NoiseHelper.lerpFromProgress(this.spaghetti3dThicknessNoise, (double)x, (double)y, (double)z, 0.065, 0.088);
		double i = sample(this.spaghetti3dFirstNoise, (double)x, (double)y, (double)z, e);
		double j = Math.abs(e * i) - h;
		double k = sample(this.spaghetti3dSecondNoise, (double)x, (double)y, (double)z, e);
		double l = Math.abs(e * k) - h;
		return clampBetweenNoiseRange(Math.max(j, l));
	}

	private double sampleSpaghetti2dNoise(int x, int y, int z) {
		double d = this.spaghetti2dModulatorNoise.sample((double)(x * 2), (double)y, (double)(z * 2));
		double e = NoiseColumnSampler.CaveScaler.scaleCaves(d);
		double f = 0.6;
		double g = 1.3;
		double h = NoiseHelper.lerpFromProgress(this.spaghetti2dThicknessNoise, (double)(x * 2), (double)y, (double)(z * 2), 0.6, 1.3);
		double i = sample(this.spaghetti2dNoise, (double)x, (double)y, (double)z, e);
		double j = 0.083;
		double k = Math.abs(e * i) - 0.083 * h;
		int l = this.minY;
		int m = 8;
		double n = NoiseHelper.lerpFromProgress(this.spaghetti2dElevationNoise, (double)x, 0.0, (double)z, (double)l, 8.0);
		double o = Math.abs(n - (double)y / 8.0) - 1.0 * h;
		o = o * o * o;
		return clampBetweenNoiseRange(Math.max(o, k));
	}

	private double sampleSpaghettiRoughnessNoise(int x, int y, int z) {
		double d = NoiseHelper.lerpFromProgress(this.spaghettiRoughnessModulatorNoise, (double)x, (double)y, (double)z, 0.0, 0.1);
		return (0.4 - Math.abs(this.spaghettiRoughnessNoise.sample((double)x, (double)y, (double)z))) * d;
	}

	@VisibleForTesting
	public void addDebugInfo(StringBuilder info) {
		info.append("blended: ");
		this.terrainNoise.addDebugInfo(info);
		info.append("\n").append("jagged: ");
		this.jaggedNoise.addDebugInfo(info);
		info.append("\n").append("barrier: ");
		this.aquiferBarrierNoise.addDebugInfo(info);
		info.append("\n").append("fluid level floodedness: ");
		this.aquiferFluidLevelFloodednessNoise.addDebugInfo(info);
		info.append("\n").append("lava: ");
		this.aquiferLavaNoise.addDebugInfo(info);
		info.append("\n").append("aquifer positional: ");
		this.aquiferRandomDeriver.addDebugInfo(info);
		info.append("\n").append("fluid level spread: ");
		this.aquiferFluidLevelSpreadNoise.addDebugInfo(info);
		info.append("\n").append("layer: ");
		this.caveLayerNoise.addDebugInfo(info);
		info.append("\n").append("pillar: ");
		this.pillarNoise.addDebugInfo(info);
		info.append("\n").append("pillar rareness: ");
		this.pillarRarenessNoise.addDebugInfo(info);
		info.append("\n").append("pillar thickness: ");
		this.pillarThicknessNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 2d: ");
		this.spaghetti2dNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 2d elev: ");
		this.spaghetti2dElevationNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 2d rare: ");
		this.spaghetti2dModulatorNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 2d thick: ");
		this.spaghetti2dThicknessNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 3d 1: ");
		this.spaghetti3dFirstNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 3d 2: ");
		this.spaghetti3dSecondNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 3d rarity: ");
		this.spaghetti3dRarityNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti 3d thickness: ");
		this.spaghetti3dThicknessNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti roughness: ");
		this.spaghettiRoughnessNoise.addDebugInfo(info);
		info.append("\n").append("spaghetti roughness modulator: ");
		this.spaghettiRoughnessModulatorNoise.addDebugInfo(info);
		info.append("\n").append("big entrance: ");
		this.caveEntranceNoise.addDebugInfo(info);
		info.append("\n").append("cheese: ");
		this.caveCheeseNoise.addDebugInfo(info);
		info.append("\n").append("temp: ");
		this.temperatureNoise.addDebugInfo(info);
		info.append("\n").append("humidity: ");
		this.humidityNoise.addDebugInfo(info);
		info.append("\n").append("continentalness: ");
		this.continentalnessNoise.addDebugInfo(info);
		info.append("\n").append("erosion: ");
		this.erosionNoise.addDebugInfo(info);
		info.append("\n").append("weirdness: ");
		this.weirdnessNoise.addDebugInfo(info);
		info.append("\n").append("offset: ");
		this.shiftNoise.addDebugInfo(info);
		info.append("\n").append("gap noise: ");
		this.oreGapNoise.addDebugInfo(info);
		info.append("\n").append("veininess: ");
		this.oreFrequencyNoiseSampler.addDebugInfo(info);
		info.append("\n").append("vein a: ");
		this.firstOrePlacementNoise.addDebugInfo(info);
		info.append("\n").append("vein b: ");
		this.secondOrePlacementNoise.addDebugInfo(info);
		info.append("\n").append("vein positional: ");
		this.oreRandomDeriver.addDebugInfo(info);
		info.append("\n").append("noodle toggle: ");
		this.noodleNoise.addDebugInfo(info);
		info.append("\n").append("noodle thickness: ");
		this.noodleThicknessNoise.addDebugInfo(info);
		info.append("\n").append("noodle ridge a: ");
		this.noodleRidgeFirstNoise.addDebugInfo(info);
		info.append("\n").append("noodle ridge b: ");
		this.noodleRidgeSecondNoise.addDebugInfo(info);
		info.append("\n").append("depth based layer: ");
		this.depthBasedLayerRandomDeriver.addDebugInfo(info);
		info.append("\n");
	}

	public RandomDeriver getDepthBasedLayerRandomDeriver() {
		return this.depthBasedLayerRandomDeriver;
	}

	private static double clampBetweenNoiseRange(double value) {
		return MathHelper.clamp(value, -1.0, 1.0);
	}

	private static double sample(DoublePerlinNoiseSampler sampler, double x, double y, double z, double invertedScale) {
		return sampler.sample(x / invertedScale, y / invertedScale, z / invertedScale);
	}

	private boolean shouldPlaceOreVeinBlock(double firstOrePlacementNoise, double secondOrePlacementNoise) {
		double d = Math.abs(1.0 * firstOrePlacementNoise) - 0.08F;
		double e = Math.abs(1.0 * secondOrePlacementNoise) - 0.08F;
		return Math.max(d, e) < 0.0;
	}

	@Nullable
	private NoiseColumnSampler.VeinType getVeinType(double oreFrequencyNoise, int y) {
		NoiseColumnSampler.VeinType veinType = oreFrequencyNoise > 0.0 ? NoiseColumnSampler.VeinType.COPPER : NoiseColumnSampler.VeinType.IRON;
		int i = veinType.maxY - y;
		int j = y - veinType.minY;
		if (j >= 0 && i >= 0) {
			int k = Math.min(i, j);
			double d = MathHelper.clampedLerpFromProgress((double)k, 0.0, 20.0, -0.2, 0.0);
			return Math.abs(oreFrequencyNoise) + d < 0.4F ? null : veinType;
		} else {
			return null;
		}
	}

	static final class CaveScaler {
		private CaveScaler() {
		}

		static double scaleCaves(double value) {
			if (value < -0.75) {
				return 0.5;
			} else if (value < -0.5) {
				return 0.75;
			} else if (value < 0.5) {
				return 1.0;
			} else {
				return value < 0.75 ? 2.0 : 3.0;
			}
		}

		static double scaleTunnels(double value) {
			if (value < -0.5) {
				return 0.75;
			} else if (value < 0.0) {
				return 1.0;
			} else {
				return value < 0.5 ? 1.5 : 2.0;
			}
		}
	}

	static enum VeinType {
		COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
		IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

		final BlockState ore;
		final BlockState rawBlock;
		final BlockState stone;
		final int minY;
		final int maxY;

		private VeinType(BlockState ore, BlockState rawBlock, BlockState stone, int minY, int maxY) {
			this.ore = ore;
			this.rawBlock = rawBlock;
			this.stone = stone;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
