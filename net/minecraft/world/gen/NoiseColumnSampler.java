/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.jetbrains.annotations.Nullable;

/**
 * Samples noise values for use in chunk generation.
 */
public class NoiseColumnSampler
implements MultiNoiseUtil.MultiNoiseSampler {
    private static final float field_34658 = 1.0f;
    private static final float field_34668 = 0.08f;
    private static final float field_34669 = 0.4f;
    private static final double field_34670 = 1.5;
    private static final int field_34671 = 20;
    private static final double field_34672 = 0.2;
    private static final float field_34673 = 0.7f;
    private static final float field_34674 = 0.1f;
    private static final float field_34675 = 0.3f;
    private static final float field_34676 = 0.6f;
    private static final float field_34677 = 0.02f;
    private static final float field_34678 = -0.3f;
    private static final double field_34679 = 1.5;
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

    public NoiseColumnSampler(int horizontalNoiseResolution, int verticalNoiseResolution, int noiseSizeY, GenerationShapeConfig config, boolean hasNoiseCaves, long seed, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, ChunkRandom.RandomProvider randomProvider) {
        this.verticalNoiseResolution = verticalNoiseResolution;
        this.noiseSizeY = noiseSizeY;
        this.config = config;
        this.densityFactor = config.densityFactor();
        this.densityOffset = config.densityOffset();
        int i = config.minimumY();
        this.minY = MathHelper.floorDiv(i, verticalNoiseResolution);
        this.hasNoiseCaves = hasNoiseCaves;
        this.intialNoiseSampler = chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator((x, y, z) -> this.sampleNoiseColumn(x, y, z, chunkNoiseSampler.getTerrainNoisePoint(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(z))));
        if (config.islandNoiseOverride()) {
            AbstractRandom abstractRandom = randomProvider.create(seed);
            abstractRandom.skip(17292);
            this.islandNoise = new SimplexNoiseSampler(abstractRandom);
        } else {
            this.islandNoise = null;
        }
        int j = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
        int k = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
        float f = 4.0f;
        double d = 2.6666666666666665;
        int l = i + 4;
        int m = i + config.height();
        RandomDeriver randomDeriver = randomProvider.create(seed).createBlockPosRandomDeriver();
        if (randomProvider != ChunkRandom.RandomProvider.LEGACY) {
            this.terrainNoise = new InterpolatedNoiseSampler(randomDeriver.createRandom("terrain"), config.sampling(), horizontalNoiseResolution, verticalNoiseResolution);
            this.temperatureNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.TEMPERATURE);
            this.humidityNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.VEGETATION);
            this.shiftNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.OFFSET);
        } else {
            AbstractRandom abstractRandom2 = randomProvider.create(seed);
            this.terrainNoise = new InterpolatedNoiseSampler(randomProvider.create(seed), config.sampling(), horizontalNoiseResolution, verticalNoiseResolution);
            this.temperatureNoise = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(seed), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
            this.humidityNoise = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(seed + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
            this.shiftNoise = DoublePerlinNoiseSampler.create(randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0, new double[0]));
        }
        this.aquiferRandomDeriver = randomDeriver.createRandom("aquifer").createBlockPosRandomDeriver();
        this.oreRandomDeriver = randomDeriver.createRandom("ore").createBlockPosRandomDeriver();
        this.depthBasedLayerRandomDeriver = randomDeriver.createRandom("depth_based_layer").createBlockPosRandomDeriver();
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
        this.continentalnessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.CONTINENTALNESS);
        this.erosionNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.EROSION);
        this.weirdnessNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.RIDGE);
        this.oreFrequencyNoiseSamplerFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEININESS), j, k, 0, 1.5);
        this.firstOrePlacementNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEIN_A), j, k, 0, 4.0);
        this.secondOrePlacementNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_VEIN_B), j, k, 0, 4.0);
        this.oreGapNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.ORE_GAP);
        this.noodleNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE), l, m, -1, 1.0);
        this.noodleThicknessNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_THICKNESS), l, m, 0, 1.0);
        this.noodleRidgeFirstNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_A), l, m, 0, 2.6666666666666665);
        this.noodleRidgeSecondNoiseFactory = NoiseColumnSampler.createNoiseSamplerFactory(NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.NOODLE_RIDGE_B), l, m, 0, 2.6666666666666665);
        this.jaggedNoise = NoiseParametersKeys.method_39173(noiseRegistry, randomDeriver, NoiseParametersKeys.JAGGED);
    }

    private static ChunkNoiseSampler.ValueSamplerFactory createNoiseSamplerFactory(DoublePerlinNoiseSampler noiseSampler, int minY, int maxY, int alternative, double scale) {
        ChunkNoiseSampler.ColumnSampler columnSampler = (x, y, z) -> {
            if (y > maxY || y < minY) {
                return alternative;
            }
            return noiseSampler.sample((double)x * scale, (double)y * scale, (double)z * scale);
        };
        return chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(columnSampler);
    }

    private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point) {
        double d = this.terrainNoise.calculateNoise(x, y, z);
        boolean bl = !this.hasNoiseCaves;
        return this.sampleNoiseColumn(x, y, z, point, d, bl, true);
    }

    private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point, double noise, boolean hasNoNoiseCaves, boolean bl) {
        double j;
        double i;
        double h;
        double g;
        double f;
        double e;
        double d;
        if (this.densityFactor == 0.0 && this.densityOffset == -0.030078125) {
            d = 0.0;
        } else {
            e = bl ? this.method_38409(point.peaks(), x, z) : 0.0;
            f = this.getDepth(y);
            g = (f + point.offset() + e) * point.factor();
            d = g * (double)(g > 0.0 ? 4 : 1);
        }
        e = d + noise;
        f = 1.5625;
        if (hasNoNoiseCaves || e < -64.0) {
            g = e;
            h = 64.0;
            i = -64.0;
        } else {
            j = e - 1.5625;
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
                double p;
                double o = this.sampleCaveLayerNoise(x, y, z);
                if (o > 64.0) {
                    g = 64.0;
                } else {
                    p = this.caveCheeseNoise.sample(x, (double)y / 1.5, z);
                    double q = MathHelper.clamp(p + 0.27, -1.0, 1.0);
                    double r = j * 1.28;
                    double s = q + MathHelper.clampedLerp(0.5, 0.0, r);
                    g = s + o;
                }
                p = this.sampleSpaghetti2dNoise(x, y, z);
                h = Math.min(n, p + l);
                i = this.samplePillarNoise(x, y, z);
            }
        }
        j = Math.max(Math.min(g, h), i);
        j = this.applySlides(j, y / this.verticalNoiseResolution);
        j = MathHelper.clamp(j, -64.0, 64.0);
        return j;
    }

    private double method_38409(double d, double e, double f) {
        if (d == 0.0) {
            return 0.0;
        }
        float g = 1500.0f;
        double h = this.jaggedNoise.sample(e * 1500.0, 0.0, f * 1500.0);
        return h > 0.0 ? d * h : d / 2.0 * h;
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
        noise = this.config.bottomSlide().method_38414(noise, i);
        return noise;
    }

    protected ChunkNoiseSampler.BlockStateSampler createInitialNoiseBlockStateSampler(ChunkNoiseSampler chunkNoiseSampler, ChunkNoiseSampler.ColumnSampler columnSampler, boolean hasNoodleCaves) {
        ChunkNoiseSampler.ValueSampler valueSampler = this.intialNoiseSampler.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler2 = hasNoodleCaves ? this.noodleNoiseFactory.create(chunkNoiseSampler) : () -> -1.0;
        ChunkNoiseSampler.ValueSampler valueSampler3 = hasNoodleCaves ? this.noodleThicknessNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
        ChunkNoiseSampler.ValueSampler valueSampler4 = hasNoodleCaves ? this.noodleRidgeFirstNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
        ChunkNoiseSampler.ValueSampler valueSampler5 = hasNoodleCaves ? this.noodleRidgeSecondNoiseFactory.create(chunkNoiseSampler) : () -> 0.0;
        return (x, y, z) -> {
            double d;
            double e = d = valueSampler.sample();
            e = MathHelper.clamp(e * 0.64, -1.0, 1.0);
            e = e / 2.0 - e * e * e / 24.0;
            if (valueSampler2.sample() >= 0.0) {
                double f = 0.05;
                double g = 0.1;
                double h = MathHelper.clampedLerpFromProgress(valueSampler3.sample(), -1.0, 1.0, 0.05, 0.1);
                double i = Math.abs(1.5 * valueSampler4.sample()) - h;
                double j = Math.abs(1.5 * valueSampler5.sample()) - h;
                e = Math.min(e, Math.max(i, j));
            }
            return chunkNoiseSampler.getAquiferSampler().apply(x, y, z, d, e += columnSampler.calculateNoise(x, y, z));
        };
    }

    protected ChunkNoiseSampler.BlockStateSampler createOreVeinSampler(ChunkNoiseSampler chunkNoiseSampler, boolean hasOreVeins) {
        if (!hasOreVeins) {
            return (x, y, z) -> null;
        }
        ChunkNoiseSampler.ValueSampler valueSampler = this.oreFrequencyNoiseSamplerFactory.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler2 = this.firstOrePlacementNoiseFactory.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler3 = this.secondOrePlacementNoiseFactory.create(chunkNoiseSampler);
        BlockState blockState = null;
        return (x, y, z) -> {
            AbstractRandom abstractRandom = this.oreRandomDeriver.createRandom(x, y, z);
            double d = valueSampler.sample();
            VeinType veinType = this.getVeinType(d, y);
            if (veinType == null) {
                return blockState;
            }
            if (abstractRandom.nextFloat() > 0.7f) {
                return blockState;
            }
            if (this.shouldPlaceOreVeinBlock(valueSampler2.sample(), valueSampler3.sample())) {
                double e = MathHelper.clampedLerpFromProgress(Math.abs(d), (double)0.4f, (double)0.6f, (double)0.1f, (double)0.3f);
                if ((double)abstractRandom.nextFloat() < e && this.oreGapNoise.sample(x, y, z) > (double)-0.3f) {
                    return abstractRandom.nextFloat() < 0.02f ? veinType.rawBlock : veinType.ore;
                }
                return veinType.stone;
            }
            return blockState;
        };
    }

    protected int method_38383(int x, int z, TerrainNoisePoint point) {
        for (int i = this.minY + this.noiseSizeY; i >= this.minY; --i) {
            int j = i * this.verticalNoiseResolution;
            double d = -0.703125;
            double e = this.sampleNoiseColumn(x, j, z, point, -0.703125, true, false);
            if (!(e > 0.390625)) continue;
            return j;
        }
        return Integer.MAX_VALUE;
    }

    protected AquiferSampler createAquiferSampler(ChunkNoiseSampler chunkNoiseSampler, int x, int z, int minimumY, int height, AquiferSampler.FluidLevelSampler fluidLevelSampler, boolean hasAquifers) {
        if (!hasAquifers) {
            return AquiferSampler.seaLevel(fluidLevelSampler);
        }
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        return AquiferSampler.aquifer(chunkNoiseSampler, new ChunkPos(i, j), this.aquiferBarrierNoise, this.aquiferFluidLevelFloodednessNoise, this.aquiferFluidLevelSpreadNoise, this.aquiferLavaNoise, this.aquiferRandomDeriver, this, minimumY * this.verticalNoiseResolution, height * this.verticalNoiseResolution, fluidLevelSampler);
    }

    @Override
    public MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k) {
        double d = (double)i + this.sampleShiftNoise(i, 0, k);
        double e = (double)k + this.sampleShiftNoise(k, i, 0);
        float f = (float)this.sampleContinentalnessNoise(d, 0.0, e);
        float g = (float)this.sampleErosionNoise(d, 0.0, e);
        float h = (float)this.sampleWeirdnessNoise(d, 0.0, e);
        double l = this.terrainParameters.getOffset(this.terrainParameters.createNoisePoint(f, g, h));
        return this.sample(i, j, k, d, e, f, g, h, l);
    }

    protected MultiNoiseUtil.NoiseValuePoint sample(int x, int y, int z, double noiseX, double noiseZ, float continentalness, float erosion, float weirdness, double offset) {
        double d = (double)y + this.sampleShiftNoise(y, z, x);
        double e = this.getDepth(BiomeCoords.toBlock(y)) + offset;
        return MultiNoiseUtil.createNoiseValuePoint((float)this.sampleTemperatureNoise(noiseX, d, noiseZ), (float)this.sampleHumidityNoise(noiseX, d, noiseZ), continentalness, erosion, (float)e, weirdness);
    }

    public TerrainNoisePoint createTerrainNoisePoint(int x, int z, float continentalness, float weirdness, float erosion) {
        if (this.islandNoise != null) {
            double d = TheEndBiomeSource.getNoiseAt(this.islandNoise, x / 8, z / 8) - 8.0f;
            double e = d > 0.0 ? 0.001953125 : 0.0078125;
            return new TerrainNoisePoint(d, e, 0.0);
        }
        VanillaTerrainParameters.NoisePoint noisePoint = this.terrainParameters.createNoisePoint(continentalness, erosion, weirdness);
        return new TerrainNoisePoint(this.terrainParameters.getOffset(noisePoint), this.terrainParameters.getFactor(noisePoint), this.terrainParameters.getPeak(noisePoint));
    }

    @Override
    public BlockPos findBestSpawnPosition() {
        return MultiNoiseUtil.findFittestPosition(this.spawnSuitabilityNoises, this);
    }

    public double sampleShiftNoise(int x, int y, int z) {
        return this.shiftNoise.sample(x, y, z) * 4.0;
    }

    public double sampleTemperatureNoise(double x, double y, double z) {
        return this.temperatureNoise.sample(x, 0.0, z);
    }

    public double sampleHumidityNoise(double x, double y, double z) {
        return this.humidityNoise.sample(x, 0.0, z);
    }

    public double sampleContinentalnessNoise(double x, double y, double z) {
        if (SharedConstants.DEBUG_BIOME_SOURCE) {
            double d;
            if (SharedConstants.method_37896((int)x * 4, (int)z * 4)) {
                return -1.0;
            }
            return d * d * (double)((d = MathHelper.fractionalPart(x / 2048.0) * 2.0 - 1.0) < 0.0 ? -1 : 1);
        }
        if (SharedConstants.field_34372) {
            double d = x * 0.005;
            return Math.sin(d + 0.5 * Math.sin(d));
        }
        return this.continentalnessNoise.sample(x, y, z);
    }

    public double sampleErosionNoise(double x, double y, double z) {
        if (SharedConstants.DEBUG_BIOME_SOURCE) {
            double d;
            if (SharedConstants.method_37896((int)x * 4, (int)z * 4)) {
                return -1.0;
            }
            return d * d * (double)((d = MathHelper.fractionalPart(z / 256.0) * 2.0 - 1.0) < 0.0 ? -1 : 1);
        }
        if (SharedConstants.field_34372) {
            double d = z * 0.005;
            return Math.sin(d + 0.5 * Math.sin(d));
        }
        return this.erosionNoise.sample(x, y, z);
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
        double f = NoiseHelper.lerpFromProgress(this.pillarRarenessNoise, x, y, z, 0.0, 2.0);
        double g = 0.0;
        double h = 1.1;
        double i = NoiseHelper.lerpFromProgress(this.pillarThicknessNoise, x, y, z, 0.0, 1.1);
        i = Math.pow(i, 3.0);
        double j = 25.0;
        double k = 0.3;
        double l = this.pillarNoise.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
        if ((l = i * (l * 2.0 - f)) > 0.03) {
            return l;
        }
        return Double.NEGATIVE_INFINITY;
    }

    private double sampleCaveLayerNoise(int x, int y, int z) {
        double d = this.caveLayerNoise.sample(x, y * 8, z);
        return MathHelper.square(d) * 4.0;
    }

    private double sampleSpaghetti3dNoise(int x, int y, int z) {
        double d = this.spaghetti3dRarityNoise.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleTunnels(d);
        double f = 0.065;
        double g = 0.088;
        double h = NoiseHelper.lerpFromProgress(this.spaghetti3dThicknessNoise, x, y, z, 0.065, 0.088);
        double i = NoiseColumnSampler.sample(this.spaghetti3dFirstNoise, x, y, z, e);
        double j = Math.abs(e * i) - h;
        double k = NoiseColumnSampler.sample(this.spaghetti3dSecondNoise, x, y, z, e);
        double l = Math.abs(e * k) - h;
        return NoiseColumnSampler.clampBetweenNoiseRange(Math.max(j, l));
    }

    private double sampleSpaghetti2dNoise(int x, int y, int z) {
        double d = this.spaghetti2dModulatorNoise.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleCaves(d);
        double f = 0.6;
        double g = 1.3;
        double h = NoiseHelper.lerpFromProgress(this.spaghetti2dThicknessNoise, x * 2, y, z * 2, 0.6, 1.3);
        double i = NoiseColumnSampler.sample(this.spaghetti2dNoise, x, y, z, e);
        double j = 0.083;
        double k = Math.abs(e * i) - 0.083 * h;
        int l = this.minY;
        int m = 8;
        double n = NoiseHelper.lerpFromProgress(this.spaghetti2dElevationNoise, x, 0.0, z, l, 8.0);
        double o = Math.abs(n - (double)y / 8.0) - 1.0 * h;
        o = o * o * o;
        return NoiseColumnSampler.clampBetweenNoiseRange(Math.max(o, k));
    }

    private double sampleSpaghettiRoughnessNoise(int x, int y, int z) {
        double d = NoiseHelper.lerpFromProgress(this.spaghettiRoughnessModulatorNoise, x, y, z, 0.0, 0.1);
        return (0.4 - Math.abs(this.spaghettiRoughnessNoise.sample(x, y, z))) * d;
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
        double e;
        double d = Math.abs(1.0 * firstOrePlacementNoise) - (double)0.08f;
        return Math.max(d, e = Math.abs(1.0 * secondOrePlacementNoise) - (double)0.08f) < 0.0;
    }

    @Nullable
    private VeinType getVeinType(double oreFrequencyNoise, int y) {
        VeinType veinType = oreFrequencyNoise > 0.0 ? VeinType.COPPER : VeinType.IRON;
        int i = veinType.maxY - y;
        int j = y - veinType.minY;
        if (j < 0 || i < 0) {
            return null;
        }
        int k = Math.min(i, j);
        double d = MathHelper.clampedLerpFromProgress((double)k, 0.0, 20.0, -0.2, 0.0);
        if (Math.abs(oreFrequencyNoise) + d < (double)0.4f) {
            return null;
        }
        return veinType;
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

    static final class CaveScaler {
        private CaveScaler() {
        }

        static double scaleCaves(double value) {
            if (value < -0.75) {
                return 0.5;
            }
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.5) {
                return 1.0;
            }
            if (value < 0.75) {
                return 2.0;
            }
            return 3.0;
        }

        static double scaleTunnels(double value) {
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.0) {
                return 1.0;
            }
            if (value < 0.5) {
                return 1.5;
            }
            return 2.0;
        }
    }
}

