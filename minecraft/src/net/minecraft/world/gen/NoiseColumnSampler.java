package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6748;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
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
	private final int verticalNoiseResolution;
	private final int noiseSizeY;
	private final GenerationShapeConfig config;
	private final int minY;
	private final VanillaTerrainParameters terrainParameters;
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
	private final ChunkNoiseSampler.ValueSamplerFactory oreFrequencyNoiseSamplerFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory firstOrePlacementNoiseFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory secondOrePlacementNoiseFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleNoiseFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleThicknessNoiseFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleRidgeFirstNoiseFactory;
	private final ChunkNoiseSampler.ValueSamplerFactory noodleRidgeSecondNoiseFactory;
	private final RandomDeriver aquiferRandomDeriver;
	private final RandomDeriver oreRandomDeriver;
	private final RandomDeriver depthBasedLayerRandomDeriver;
	private final List<MultiNoiseUtil.NoiseHypercube> spawnSuitabilityNoises = new VanillaBiomeParameters().getSpawnSuitabilityNoises();
	private final boolean field_35592;

	public NoiseColumnSampler(
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int noiseSizeY,
		GenerationShapeConfig config,
		boolean hasNoiseCaves,
		long seed,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry,
		ChunkRandom.RandomProvider randomProvider
	) {
		this.verticalNoiseResolution = verticalNoiseResolution;
		this.noiseSizeY = noiseSizeY;
		this.config = config;
		this.terrainParameters = config.terrainParameters();
		int i = config.minimumY();
		this.minY = MathHelper.floorDiv(i, verticalNoiseResolution);
		this.hasNoiseCaves = hasNoiseCaves;
		this.intialNoiseSampler = chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(
				(x, y, z) -> this.sampleNoiseColumn(
						x, y, z, chunkNoiseSampler.createMultiNoisePoint(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(z)).terrainInfo(), chunkNoiseSampler.method_39327()
					)
			);
		if (config.islandNoiseOverride()) {
			AbstractRandom abstractRandom = randomProvider.create(seed);
			abstractRandom.skip(17292);
			this.islandNoise = new SimplexNoiseSampler(abstractRandom);
		} else {
			this.islandNoise = null;
		}

		this.field_35592 = config.amplified();
		int j = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
		int k = Stream.of(NoiseColumnSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
		float f = 4.0F;
		double d = 2.6666666666666665;
		int l = i + 4;
		int m = i + config.height();
		boolean bl = config.largeBiomes();
		RandomDeriver randomDeriver = randomProvider.create(seed).createRandomDeriver();
		if (randomProvider != ChunkRandom.RandomProvider.LEGACY) {
			this.terrainNoise = new InterpolatedNoiseSampler(
				randomDeriver.createRandom(new Identifier("terrain")), config.sampling(), horizontalNoiseResolution, verticalNoiseResolution
			);
			this.temperatureNoise = NoiseParametersKeys.method_39173(
				noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE
			);
			this.humidityNoise = NoiseParametersKeys.method_39173(
				noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION
			);
			this.shiftNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.OFFSET_LARGE : NoiseParametersKeys.OFFSET);
		} else {
			this.terrainNoise = new InterpolatedNoiseSampler(randomProvider.create(seed), config.sampling(), horizontalNoiseResolution, verticalNoiseResolution);
			this.temperatureNoise = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(seed), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
			this.humidityNoise = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(seed + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
			this.shiftNoise = DoublePerlinNoiseSampler.create(
				randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
			);
		}

		this.aquiferRandomDeriver = randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
		this.oreRandomDeriver = randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
		this.depthBasedLayerRandomDeriver = randomDeriver.createRandom(new Identifier("depth_based_layer")).createRandomDeriver();
		this.aquiferBarrierNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.AQUIFER_BARRIER);
		this.aquiferFluidLevelFloodednessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
		this.aquiferLavaNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.AQUIFER_LAVA);
		this.aquiferFluidLevelSpreadNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD);
		this.pillarNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.PILLAR);
		this.pillarRarenessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.PILLAR_RARENESS);
		this.pillarThicknessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.PILLAR_THICKNESS);
		this.spaghetti2dNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D);
		this.spaghetti2dElevationNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_ELEVATION);
		this.spaghetti2dModulatorNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_MODULATOR);
		this.spaghetti2dThicknessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_2D_THICKNESS);
		this.spaghetti3dFirstNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_1);
		this.spaghetti3dSecondNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_2);
		this.spaghetti3dRarityNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_RARITY);
		this.spaghetti3dThicknessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_3D_THICKNESS);
		this.spaghettiRoughnessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_ROUGHNESS);
		this.spaghettiRoughnessModulatorNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR);
		this.caveEntranceNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.CAVE_ENTRANCE);
		this.caveLayerNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.CAVE_LAYER);
		this.caveCheeseNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.CAVE_CHEESE);
		this.continentalnessNoise = NoiseParametersKeys.method_39173(
			noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.CONTINENTALNESS_LARGE : NoiseParametersKeys.CONTINENTALNESS
		);
		this.erosionNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.EROSION_LARGE : NoiseParametersKeys.EROSION);
		this.weirdnessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, bl ? NoiseParametersKeys.RIDGE_LARGE : NoiseParametersKeys.RIDGE);
		this.oreFrequencyNoiseSamplerFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEININESS), j, k, 0, 1.5
		);
		this.firstOrePlacementNoiseFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEIN_A), j, k, 0, 4.0
		);
		this.secondOrePlacementNoiseFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEIN_B), j, k, 0, 4.0
		);
		this.oreGapNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_GAP);
		this.noodleNoiseFactory = createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE), l, m, -1, 1.0);
		this.noodleThicknessNoiseFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_THICKNESS), l, m, 0, 1.0
		);
		this.noodleRidgeFirstNoiseFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_A), l, m, 0, 2.6666666666666665
		);
		this.noodleRidgeSecondNoiseFactory = createNoiseSamplerFactory(
			NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_B), l, m, 0, 2.6666666666666665
		);
		this.jaggedNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.JAGGED);
	}

	private static ChunkNoiseSampler.ValueSamplerFactory createNoiseSamplerFactory(
		DoublePerlinNoiseSampler noiseSampler, int minY, int maxY, int alternative, double scale
	) {
		ChunkNoiseSampler.ColumnSampler columnSampler = (x, y, z) -> y <= maxY && y >= minY
				? noiseSampler.sample((double)x * scale, (double)y * scale, (double)z * scale)
				: (double)alternative;
		return chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(columnSampler);
	}

	private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point, class_6748 arg) {
		double d = this.terrainNoise.calculateNoise(x, y, z);
		boolean bl = !this.hasNoiseCaves;
		return this.sampleNoiseColumn(x, y, z, point, d, bl, true, arg);
	}

	private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point, double noise, boolean hasNoNoiseCaves, boolean bl, class_6748 arg) {
		double d;
		if (this.islandNoise != null) {
			d = ((double)TheEndBiomeSource.getNoiseAt(this.islandNoise, x / 8, z / 8) - 8.0) / 128.0;
		} else {
			double e = bl ? this.method_38409(point.peaks(), (double)x, (double)z) : 0.0;
			double f = (this.method_39331(y, point) + e) * point.factor();
			d = f * (double)(f > 0.0 ? 4 : 1);
		}

		double e = d + noise;
		double f = 1.5625;
		double g;
		double h;
		double i;
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
		j = arg.method_39338(x, y, z, j);
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

	private double method_39331(int i, TerrainNoisePoint terrainNoisePoint) {
		double d = 1.0 - (double)i / 128.0;
		return d + terrainNoisePoint.offset();
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
			double e = this.sampleNoiseColumn(x, j, z, point, -0.703125, true, false, class_6748.method_39336());
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

	@Debug
	public NoiseColumnSampler.class_6747 method_39330(int i, int j, class_6748 arg) {
		double d = (double)i + this.sampleShiftNoise(i, 0, j);
		double e = (double)j + this.sampleShiftNoise(j, i, 0);
		double f = this.sampleContinentalnessNoise(d, 0.0, e);
		double g = this.sampleWeirdnessNoise(d, 0.0, e);
		double h = this.sampleErosionNoise(d, 0.0, e);
		TerrainNoisePoint terrainNoisePoint = this.createTerrainNoisePoint(BiomeCoords.toBlock(i), BiomeCoords.toBlock(j), (float)f, (float)g, (float)h, arg);
		return new NoiseColumnSampler.class_6747(d, e, f, g, h, terrainNoisePoint);
	}

	@Override
	public MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k) {
		return this.method_39329(i, j, k, this.method_39330(i, k, class_6748.method_39336()));
	}

	@Debug
	public MultiNoiseUtil.NoiseValuePoint method_39329(int i, int j, int k, NoiseColumnSampler.class_6747 arg) {
		double d = arg.shiftedX();
		double e = (double)j + this.sampleShiftNoise(j, k, i);
		double f = arg.shiftedZ();
		double g = this.method_39331(BiomeCoords.toBlock(j), arg.terrainInfo());
		return MultiNoiseUtil.createNoiseValuePoint(
			(float)this.sampleTemperatureNoise(d, e, f),
			(float)this.sampleHumidityNoise(d, e, f),
			(float)arg.continentalness(),
			(float)arg.erosion(),
			(float)g,
			(float)arg.weirdness()
		);
	}

	public TerrainNoisePoint createTerrainNoisePoint(int x, int z, float continentalness, float weirdness, float erosion, class_6748 arg) {
		VanillaTerrainParameters.NoisePoint noisePoint = this.terrainParameters.createNoisePoint(continentalness, erosion, weirdness);
		float f = this.terrainParameters.getOffset(noisePoint);
		float g = this.terrainParameters.getFactor(noisePoint);
		float h = this.terrainParameters.getPeak(noisePoint);
		TerrainNoisePoint terrainNoisePoint;
		if (this.field_35592 && (double)f > -0.033203125) {
			double d = (double)f * 2.0 + 0.16601562F;
			double e = 1.25 - 6.25 / (double)(g + 5.0F);
			terrainNoisePoint = new TerrainNoisePoint(d, e, (double)h);
		} else {
			terrainNoisePoint = new TerrainNoisePoint((double)f, (double)g, (double)h);
		}

		return arg.method_39340(x, z, terrainNoisePoint);
	}

	@Override
	public BlockPos findBestSpawnPosition() {
		return MultiNoiseUtil.findFittestPosition(this.spawnSuitabilityNoises, this);
	}

	@Debug
	public double sampleShiftNoise(int x, int y, int z) {
		return this.shiftNoise.sample((double)x, (double)y, (double)z) * 4.0;
	}

	private double sampleTemperatureNoise(double x, double y, double z) {
		return this.temperatureNoise.sample(x, 0.0, z);
	}

	private double sampleHumidityNoise(double x, double y, double z) {
		return this.humidityNoise.sample(x, 0.0, z);
	}

	@Debug
	public double sampleContinentalnessNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896(new ChunkPos(BiomeCoords.toChunk(MathHelper.floor(x)), BiomeCoords.toChunk(MathHelper.floor(z))))) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(x / 2048.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.DEBUG_NOISE) {
			double d = x * 0.005;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.continentalnessNoise.sample(x, y, z);
		}
	}

	@Debug
	public double sampleErosionNoise(double x, double y, double z) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			if (SharedConstants.method_37896(new ChunkPos(BiomeCoords.toChunk(MathHelper.floor(x)), BiomeCoords.toChunk(MathHelper.floor(z))))) {
				return -1.0;
			} else {
				double d = MathHelper.fractionalPart(z / 256.0) * 2.0 - 1.0;
				return d * d * (double)(d < 0.0 ? -1 : 1);
			}
		} else if (SharedConstants.DEBUG_NOISE) {
			double d = z * 0.005;
			return Math.sin(d + 0.5 * Math.sin(d));
		} else {
			return this.erosionNoise.sample(x, y, z);
		}
	}

	@Debug
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

	public static record class_6747() {
		private final double shiftedX;
		private final double shiftedZ;
		private final double continentalness;
		private final double weirdness;
		private final double erosion;
		private final TerrainNoisePoint terrainInfo;

		public class_6747(double d, double e, double f, double g, double h, TerrainNoisePoint terrainNoisePoint) {
			this.shiftedX = d;
			this.shiftedZ = e;
			this.continentalness = f;
			this.weirdness = g;
			this.erosion = h;
			this.terrainInfo = terrainNoisePoint;
		}
	}
}
