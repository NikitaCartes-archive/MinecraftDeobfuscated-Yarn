/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.WorldGenRandom;
import org.jetbrains.annotations.Nullable;

public class OreVeinGenerator {
    private static final float field_33588 = 1.0f;
    private static final float ORE_PLACEMENT_NOISE_SCALE = 4.0f;
    private static final float ORE_PLACEMENT_NOISE_THRESHOLD = 0.08f;
    private static final float FREQUENCY_NOISE_THRESHOLD = 0.5f;
    private static final double ORE_FREQUENCY_NOISE_SCALE = 1.5;
    private static final int field_33695 = 20;
    private static final double field_33696 = 0.2;
    private static final float GENERATION_CHANCE = 0.7f;
    private static final float ORE_CHANCE_START = 0.1f;
    private static final float ORE_CHANCE_END = 0.3f;
    private static final float MAX_ORE_CHANCE_NOISE_THRESHOLD = 0.6f;
    private static final float RAW_ORE_CHANCE = 0.02f;
    private static final float ORE_CHANCE_THRESHOLD = -0.3f;
    private final int maxY;
    private final int minY;
    private final BlockState defaultState;
    private final DoublePerlinNoiseSampler oreFrequencyNoiseSampler;
    private final DoublePerlinNoiseSampler firstOrePlacementNoiseSampler;
    private final DoublePerlinNoiseSampler secondOrePlacementNoiseSampler;
    private final DoublePerlinNoiseSampler oreChanceNoiseSampler;
    private final int horizontalNoiseResolution;
    private final int verticalNoiseResolution;

    public OreVeinGenerator(long seed, BlockState defaultState, int horizontalNoiseResolution, int verticalNoiseResolution, int minY) {
        Random random = new Random(seed);
        this.defaultState = defaultState;
        this.oreFrequencyNoiseSampler = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.firstOrePlacementNoiseSampler = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.secondOrePlacementNoiseSampler = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.oreChanceNoiseSampler = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(0L), -5, 1.0);
        this.horizontalNoiseResolution = horizontalNoiseResolution;
        this.verticalNoiseResolution = verticalNoiseResolution;
        this.maxY = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(minY);
        this.minY = Stream.of(VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(minY);
    }

    public void sampleOreFrequencyNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
        this.sample(buffer, x, z, this.oreFrequencyNoiseSampler, 1.5, minY, noiseSizeY);
    }

    public void sampleFirstOrePlacementNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
        this.sample(buffer, x, z, this.firstOrePlacementNoiseSampler, 4.0, minY, noiseSizeY);
    }

    public void sampleSecondOrePlacementNoise(double[] buffer, int x, int z, int minY, int noiseSizeY) {
        this.sample(buffer, x, z, this.secondOrePlacementNoiseSampler, 4.0, minY, noiseSizeY);
    }

    public void sample(double[] buffer, int x, int z, DoublePerlinNoiseSampler sampler, double scale, int minY, int noiseSizeY) {
        for (int i = 0; i < noiseSizeY; ++i) {
            int j = i + minY;
            int k = x * this.horizontalNoiseResolution;
            int l = j * this.verticalNoiseResolution;
            int m = z * this.horizontalNoiseResolution;
            double d = l >= this.minY && l <= this.maxY ? sampler.sample((double)k * scale, (double)l * scale, (double)m * scale) : 0.0;
            buffer[i] = d;
        }
    }

    public BlockState sample(WorldGenRandom random, int x, int y, int z, double oreFrequencyNoise, double firstOrePlacementNoise, double secondOrePlacementNoise) {
        BlockState blockState = this.defaultState;
        VeinType veinType = this.getVeinType(oreFrequencyNoise, y);
        if (veinType == null) {
            return blockState;
        }
        if (random.nextFloat() > 0.7f) {
            return blockState;
        }
        if (this.shouldPlaceOreBlock(firstOrePlacementNoise, secondOrePlacementNoise)) {
            double d = MathHelper.clampedLerpFromProgress(Math.abs(oreFrequencyNoise), 0.5, 0.6f, 0.1f, 0.3f);
            if ((double)random.nextFloat() < d && this.oreChanceNoiseSampler.sample(x, y, z) > (double)-0.3f) {
                return random.nextFloat() < 0.02f ? veinType.rawBlock : veinType.ore;
            }
            return veinType.stone;
        }
        return blockState;
    }

    private boolean shouldPlaceOreBlock(double firstOrePlacementNoise, double secondOrePlacementNoise) {
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
        double d = MathHelper.clampedLerpFromProgress(k, 0.0, 20.0, -0.2, 0.0);
        if (Math.abs(oreFrequencyNoise) + d < 0.5) {
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
}

