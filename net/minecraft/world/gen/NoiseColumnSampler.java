/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.TerrainNoisePoint;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.gen.MultiNoiseParameters;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.BlockPosRandomDeriver;
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
    private final ChunkNoiseSampler.ColumnSampler noise;
    @Nullable
    private final SimplexNoiseSampler islandNoise;
    private final DoublePerlinNoiseSampler field_34681;
    private final double densityFactor;
    private final double densityOffset;
    private final int field_34682;
    private final DoublePerlinNoiseSampler field_34683;
    private final DoublePerlinNoiseSampler field_34684;
    private final DoublePerlinNoiseSampler field_34685;
    private final BlockPosRandomDeriver field_34686;
    private final NoiseSampler noiseSampler;
    private final DoublePerlinNoiseSampler field_34633;
    private final DoublePerlinNoiseSampler field_34634;
    private final DoublePerlinNoiseSampler field_34635;
    private final DoublePerlinNoiseSampler field_34636;
    private final DoublePerlinNoiseSampler field_34637;
    private final DoublePerlinNoiseSampler field_34638;
    private final DoublePerlinNoiseSampler field_34639;
    private final DoublePerlinNoiseSampler field_34640;
    private final DoublePerlinNoiseSampler field_34641;
    private final DoublePerlinNoiseSampler field_34642;
    private final DoublePerlinNoiseSampler field_34643;
    private final DoublePerlinNoiseSampler field_34644;
    private final DoublePerlinNoiseSampler field_34645;
    private final DoublePerlinNoiseSampler field_34646;
    private final DoublePerlinNoiseSampler field_34647;
    private final DoublePerlinNoiseSampler field_34648;
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler continentalnessNoise;
    private final DoublePerlinNoiseSampler erosionNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final DoublePerlinNoiseSampler shiftNoise;
    private final VanillaTerrainParameters terrainParameters = new VanillaTerrainParameters();
    private final DoublePerlinNoiseSampler field_34656;
    private final ChunkNoiseSampler.ValueSamplerFactory intialNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory oreFrequencyNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory firstOrePlacementNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory secondOrePlacementNoiseSampler;
    private final BlockPosRandomDeriver field_34662;
    private final ChunkNoiseSampler.ValueSamplerFactory noodleCavesFrequencyNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory noodleCavesWeightReducingNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory noodleCavesFirstWeightNoiseSampler;
    private final ChunkNoiseSampler.ValueSamplerFactory noodleCavesSecondWeightNoiseSampler;
    private final boolean hasNoiseCaves;

    public NoiseColumnSampler(int horizontalNoiseResolution, int verticalNoiseResolution, int noiseSizeY, GenerationShapeConfig config, MultiNoiseParameters noiseParameters, boolean hasNoiseCaves, long seed) {
        AbstractRandom abstractRandom4;
        this.verticalNoiseResolution = verticalNoiseResolution;
        this.noiseSizeY = noiseSizeY;
        this.config = config;
        this.densityFactor = config.getDensityFactor();
        this.densityOffset = config.getDensityOffset();
        int i = config.getMinimumY();
        this.field_34682 = MathHelper.floorDiv(i, verticalNoiseResolution);
        AtomicSimpleRandom abstractRandom = new AtomicSimpleRandom(seed);
        AtomicSimpleRandom abstractRandom2 = new AtomicSimpleRandom(seed);
        AbstractRandom abstractRandom3 = config.method_38413() ? abstractRandom2 : abstractRandom.derive();
        this.noise = new InterpolatedNoiseSampler(abstractRandom3, config.getSampling(), horizontalNoiseResolution, verticalNoiseResolution);
        NoiseSampler noiseSampler = this.noiseSampler = config.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler((AbstractRandom)abstractRandom2, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler((AbstractRandom)abstractRandom2, IntStream.rangeClosed(-3, 0));
        if (config.hasIslandNoiseOverride()) {
            abstractRandom4 = new AtomicSimpleRandom(seed);
            abstractRandom4.skip(17292);
            this.islandNoise = new SimplexNoiseSampler(abstractRandom4);
        } else {
            this.islandNoise = null;
        }
        abstractRandom4 = abstractRandom.derive();
        this.field_34683 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -3, 1.0);
        this.field_34684 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -3, 0.2, 2.0, 1.0);
        this.field_34685 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -1, 1.0, 0.0);
        this.field_34686 = abstractRandom4.createBlockPosRandomDeriver();
        abstractRandom4 = abstractRandom.derive();
        this.field_34634 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0, 1.0);
        this.field_34635 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34636 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34637 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
        this.field_34638 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34639 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
        this.field_34640 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
        this.field_34641 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
        this.field_34642 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 1.0);
        this.field_34643 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -11, 1.0);
        this.field_34644 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34645 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -5, 1.0);
        this.field_34646 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34647 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -7, 0.4, 0.5, 1.0);
        this.field_34633 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 1.0);
        this.field_34648 = DoublePerlinNoiseSampler.create(abstractRandom4.derive(), -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
        this.hasNoiseCaves = hasNoiseCaves;
        this.temperatureNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed), noiseParameters.temperature());
        this.humidityNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 1L), noiseParameters.humidity());
        this.continentalnessNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 2L), noiseParameters.continentalness());
        this.erosionNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 3L), noiseParameters.erosion());
        this.weirdnessNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 4L), noiseParameters.weirdness());
        this.shiftNoise = DoublePerlinNoiseSampler.create(new AtomicSimpleRandom(seed + 5L), noiseParameters.shift());
        this.field_34681 = DoublePerlinNoiseSampler.create((AbstractRandom)new AtomicSimpleRandom(seed + 6L), -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        this.intialNoiseSampler = chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator((x, y, z) -> this.sampleNoiseColumn(x, y, z, chunkNoiseSampler.getTerrainNoisePoint(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(z))));
        int j = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
        int k = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
        float f = 4.0f;
        AbstractRandom abstractRandom5 = abstractRandom.derive();
        this.oreFrequencyNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(j, k, 0, 1.5, abstractRandom5.derive(), -8, 1.0);
        this.firstOrePlacementNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(j, k, 0, 4.0, abstractRandom5.derive(), -7, 1.0);
        this.secondOrePlacementNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(j, k, 0, 4.0, abstractRandom5.derive(), -7, 1.0);
        this.field_34656 = DoublePerlinNoiseSampler.create(abstractRandom5.derive(), -5, 1.0);
        this.field_34662 = abstractRandom5.createBlockPosRandomDeriver();
        double d = 2.6666666666666665;
        AbstractRandom abstractRandom6 = abstractRandom.derive();
        int l = i + 4;
        int m = i + config.getHeight();
        this.noodleCavesFrequencyNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(l, m, -1, 1.0, abstractRandom6.derive(), -8, 1.0);
        this.noodleCavesWeightReducingNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(l, m, 0, 1.0, abstractRandom6.derive(), -8, 1.0);
        this.noodleCavesFirstWeightNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(l, m, 0, 2.6666666666666665, abstractRandom6.derive(), -7, 1.0);
        this.noodleCavesSecondWeightNoiseSampler = NoiseColumnSampler.createNoiseSamplerFactory(l, m, 0, 2.6666666666666665, abstractRandom6.derive(), -7, 1.0);
    }

    private static ChunkNoiseSampler.ValueSamplerFactory createNoiseSamplerFactory(int minY, int maxY, int alternative, double noiseScale, AbstractRandom random, int offset, double ... octaves) {
        DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(random, offset, octaves);
        ChunkNoiseSampler.ColumnSampler columnSampler = (x, y, z) -> {
            if (y > maxY || y < minY) {
                return alternative;
            }
            return doublePerlinNoiseSampler.sample((double)x * noiseScale, (double)y * noiseScale, (double)z * noiseScale);
        };
        return chunkNoiseSampler -> chunkNoiseSampler.createNoiseInterpolator(columnSampler);
    }

    private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point) {
        double d = this.noise.calculateNoise(x, y, z);
        boolean bl = !this.hasNoiseCaves;
        return this.sampleNoiseColumn(x, y, z, point, d, bl);
    }

    private double sampleNoiseColumn(int x, int y, int z, TerrainNoisePoint point, double noise, boolean hasNoNoiseCaves) {
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
            e = this.method_38409(point.peaks(), x, z);
            f = this.getDepth(y);
            d = g * (double)((g = (f + point.offset() + e) * point.factor()) > 0.0 ? 4 : 1);
        }
        e = d + noise;
        f = 1.5625;
        if (hasNoNoiseCaves || e < -64.0) {
            g = e;
            h = 64.0;
            i = -64.0;
        } else {
            j = e - 1.5625;
            boolean bl = j < 0.0;
            double k = this.method_38398(x, y, z);
            double l = this.method_38411(x, y, z);
            double m = this.method_38408(x, y, z);
            double n = Math.min(k, m + l);
            if (bl) {
                g = e;
                h = n * 5.0;
                i = -64.0;
            } else {
                double p;
                double o = this.method_38405(x, y, z);
                if (o > 64.0) {
                    g = 64.0;
                } else {
                    p = this.field_34648.sample(x, (double)y / 1.5, z);
                    double q = MathHelper.clamp(p + 0.27, -1.0, 1.0);
                    double r = j * 1.28;
                    double s = q + MathHelper.clampedLerp(0.5, 0.0, r);
                    g = s + o;
                }
                p = this.method_38410(x, y, z);
                h = Math.min(n, p + l);
                i = this.method_38402(x, y, z);
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
        double h = this.field_34681.sample(e * 1500.0, 0.0, f * 1500.0);
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
        int i = y - this.field_34682;
        noise = this.config.getTopSlide().method_38414(noise, this.noiseSizeY - i);
        noise = this.config.getBottomSlide().method_38414(noise, i);
        return noise;
    }

    protected ChunkNoiseSampler.BlockStateSampler createInitialNoiseBlockStateSampler(ChunkNoiseSampler chunkNoiseSampler, ChunkNoiseSampler.ColumnSampler columnSampler, boolean hasNoodleCaves) {
        ChunkNoiseSampler.ValueSampler valueSampler = this.intialNoiseSampler.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler2 = hasNoodleCaves ? this.noodleCavesFrequencyNoiseSampler.create(chunkNoiseSampler) : () -> -1.0;
        ChunkNoiseSampler.ValueSampler valueSampler3 = hasNoodleCaves ? this.noodleCavesWeightReducingNoiseSampler.create(chunkNoiseSampler) : () -> 0.0;
        ChunkNoiseSampler.ValueSampler valueSampler4 = hasNoodleCaves ? this.noodleCavesFirstWeightNoiseSampler.create(chunkNoiseSampler) : () -> 0.0;
        ChunkNoiseSampler.ValueSampler valueSampler5 = hasNoodleCaves ? this.noodleCavesSecondWeightNoiseSampler.create(chunkNoiseSampler) : () -> 0.0;
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
        ChunkNoiseSampler.ValueSampler valueSampler = this.oreFrequencyNoiseSampler.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler2 = this.firstOrePlacementNoiseSampler.create(chunkNoiseSampler);
        ChunkNoiseSampler.ValueSampler valueSampler3 = this.secondOrePlacementNoiseSampler.create(chunkNoiseSampler);
        BlockState blockState = null;
        return (x, y, z) -> {
            AtomicSimpleRandom abstractRandom = this.field_34662.createRandom(x, y, z);
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
                if ((double)abstractRandom.nextFloat() < e && this.field_34656.sample(x, y, z) > (double)-0.3f) {
                    return abstractRandom.nextFloat() < 0.02f ? veinType.rawBlock : veinType.ore;
                }
                return veinType.stone;
            }
            return blockState;
        };
    }

    protected int method_38383(int x, int z, TerrainNoisePoint point) {
        for (int i = this.field_34682 + this.noiseSizeY; i >= this.field_34682; --i) {
            int j = i * this.verticalNoiseResolution;
            double d = -0.703125;
            double e = this.sampleNoiseColumn(x, j, z, point, -0.703125, true);
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
        return AquiferSampler.aquifer(chunkNoiseSampler, new ChunkPos(i, j), this.field_34683, this.field_34684, this.field_34685, this.field_34686, this, minimumY * this.verticalNoiseResolution, height * this.verticalNoiseResolution, fluidLevelSampler);
    }

    protected NoiseSampler getNoiseSampler() {
        return this.noiseSampler;
    }

    @Override
    public MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k) {
        double d = (double)i + this.sampleShiftNoise(i, 0, k);
        double e = (double)k + this.sampleShiftNoise(k, i, 0);
        float f = (float)this.sampleContinentalnessNoise(d, 0.0, e);
        float g = (float)this.sampleErosionNoise(d, 0.0, e);
        float h = (float)this.sampleWeirdnessNoise(d, 0.0, e);
        double l = this.terrainParameters.getOffset(this.terrainParameters.createNoisePoint(f, g, h));
        return this.method_38378(i, j, k, d, e, f, g, h, l);
    }

    protected MultiNoiseUtil.NoiseValuePoint method_38378(int x, int y, int z, double noiseX, double noiseZ, float continentalness, float erosion, float weirdness, double offset) {
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

    public double sampleShiftNoise(int x, int y, int z) {
        return this.shiftNoise.sample(x, y, z) * 4.0;
    }

    public double sampleTemperatureNoise(double x, double y, double z) {
        return this.temperatureNoise.sample(x, y, z);
    }

    public double sampleHumidityNoise(double x, double y, double z) {
        return this.humidityNoise.sample(x, y, z);
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

    private double method_38398(int x, int y, int z) {
        double d = 0.75;
        double e = 0.5;
        double f = 0.37;
        double g = this.field_34647.sample((double)x * 0.75, (double)y * 0.5, (double)z * 0.75) + 0.37;
        int i = -10;
        double h = (double)(y - -10) / 40.0;
        double j = 0.3;
        return g + MathHelper.clampedLerp(0.3, 0.0, h);
    }

    private double method_38402(int x, int y, int z) {
        double d = 0.0;
        double e = 2.0;
        double f = NoiseHelper.lerpFromProgress(this.field_34635, x, y, z, 0.0, 2.0);
        double g = 0.0;
        double h = 1.1;
        double i = NoiseHelper.lerpFromProgress(this.field_34636, x, y, z, 0.0, 1.1);
        i = Math.pow(i, 3.0);
        double j = 25.0;
        double k = 0.3;
        double l = this.field_34634.sample((double)x * 25.0, (double)y * 0.3, (double)z * 25.0);
        if ((l = i * (l * 2.0 - f)) > 0.03) {
            return l;
        }
        return Double.NEGATIVE_INFINITY;
    }

    private double method_38405(int x, int y, int z) {
        double d = this.field_34633.sample(x, y * 8, z);
        return MathHelper.square(d) * 4.0;
    }

    private double method_38408(int x, int y, int z) {
        double d = this.field_34643.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleTunnels(d);
        double f = 0.065;
        double g = 0.088;
        double h = NoiseHelper.lerpFromProgress(this.field_34644, x, y, z, 0.065, 0.088);
        double i = NoiseColumnSampler.sample(this.field_34641, x, y, z, e);
        double j = Math.abs(e * i) - h;
        double k = NoiseColumnSampler.sample(this.field_34642, x, y, z, e);
        double l = Math.abs(e * k) - h;
        return NoiseColumnSampler.method_38395(Math.max(j, l));
    }

    private double method_38410(int x, int y, int z) {
        double d = this.field_34639.sample(x * 2, y, z * 2);
        double e = CaveScaler.scaleCaves(d);
        double f = 0.6;
        double g = 1.3;
        double h = NoiseHelper.lerpFromProgress(this.field_34640, x * 2, y, z * 2, 0.6, 1.3);
        double i = NoiseColumnSampler.sample(this.field_34637, x, y, z, e);
        double j = 0.083;
        double k = Math.abs(e * i) - 0.083 * h;
        int l = this.field_34682;
        int m = 8;
        double n = NoiseHelper.lerpFromProgress(this.field_34638, x, 0.0, z, l, 8.0);
        double o = Math.abs(n - (double)y / 8.0) - 1.0 * h;
        o = o * o * o;
        return NoiseColumnSampler.method_38395(Math.max(o, k));
    }

    private double method_38411(int x, int y, int z) {
        double d = NoiseHelper.lerpFromProgress(this.field_34646, x, y, z, 0.0, 0.1);
        return (0.4 - Math.abs(this.field_34645.sample(x, y, z))) * d;
    }

    private static double method_38395(double d) {
        return MathHelper.clamp(d, -1.0, 1.0);
    }

    private static double sample(DoublePerlinNoiseSampler sampler, double x, double y, double z, double d) {
        return sampler.sample(x / d, y / d, z / d);
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

