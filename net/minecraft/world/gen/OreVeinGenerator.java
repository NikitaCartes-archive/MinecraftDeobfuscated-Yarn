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
    private static final float field_33589 = 4.0f;
    private static final float field_33590 = 0.08f;
    private static final float field_33591 = 0.4f;
    private static final float field_33592 = 0.7f;
    private static final float field_33662 = 0.1f;
    private static final float field_33663 = 0.3f;
    private static final float field_33664 = 0.6f;
    private static final float field_33665 = 0.01f;
    private static final float field_33666 = -0.3f;
    private final int field_33595;
    private final int field_33596;
    private final BlockState defaultState;
    private final DoublePerlinNoiseSampler field_33598;
    private final DoublePerlinNoiseSampler field_33599;
    private final DoublePerlinNoiseSampler field_33600;
    private final DoublePerlinNoiseSampler field_33667;
    private final int field_33601;
    private final int field_33602;

    public OreVeinGenerator(long seed, BlockState defaultState, int i, int j, int k) {
        Random random = new Random(seed);
        this.defaultState = defaultState;
        this.field_33598 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -8, 1.0);
        this.field_33599 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.field_33600 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.field_33667 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(0L), -5, 1.0);
        this.field_33601 = i;
        this.field_33602 = j;
        this.field_33595 = Stream.of(VeinType.values()).mapToInt(veinType -> ((VeinType)veinType).maxY).max().orElse(k);
        this.field_33596 = Stream.of(VeinType.values()).mapToInt(veinType -> ((VeinType)veinType).minY).min().orElse(k);
    }

    public void method_36401(double[] ds, int i, int j, int k, int l) {
        this.method_36402(ds, i, j, this.field_33598, 1.0, k, l);
    }

    public void method_36404(double[] ds, int i, int j, int k, int l) {
        this.method_36402(ds, i, j, this.field_33599, 4.0, k, l);
    }

    public void method_36405(double[] ds, int i, int j, int k, int l) {
        this.method_36402(ds, i, j, this.field_33600, 4.0, k, l);
    }

    public void method_36402(double[] ds, int i, int j, DoublePerlinNoiseSampler doublePerlinNoiseSampler, double d, int k, int l) {
        for (int m = 0; m < l; ++m) {
            int n = m + k;
            int o = i * this.field_33601;
            int p = n * this.field_33602;
            int q = j * this.field_33601;
            double e = p >= this.field_33596 && p <= this.field_33595 ? doublePerlinNoiseSampler.sample((double)o * d, (double)p * d, (double)q * d) : 0.0;
            ds[m] = e;
        }
    }

    public BlockState sample(WorldGenRandom random, int x, int y, int z, double d, double e, double f) {
        BlockState blockState = this.defaultState;
        VeinType veinType = this.getVeinType(d);
        if (veinType == null || y < veinType.minY || y > veinType.maxY) {
            return blockState;
        }
        if (random.nextFloat() > 0.7f) {
            return blockState;
        }
        if (this.method_36398(e, f)) {
            double g = MathHelper.clampedLerpFromProgress(Math.abs(d), 0.4f, 0.6f, 0.1f, 0.3f);
            if ((double)random.nextFloat() < g && this.field_33667.sample(x, y, z) > (double)-0.3f) {
                return random.nextFloat() < 0.01f ? veinType.rawBlock : veinType.ore;
            }
            return veinType.stone;
        }
        return blockState;
    }

    private boolean method_36398(double d, double e) {
        double g;
        double f = Math.abs(1.0 * d) - (double)0.08f;
        return Math.max(f, g = Math.abs(1.0 * e) - (double)0.08f) < 0.0;
    }

    @Nullable
    private VeinType getVeinType(double d) {
        if (Math.abs(d) < (double)0.4f) {
            return null;
        }
        return d > 0.0 ? VeinType.COPPER : VeinType.IRON;
    }

    static enum VeinType {
        COPPER(Blocks.COPPER_ORE.getDefaultState(), Blocks.RAW_COPPER_BLOCK.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
        IRON(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.RAW_IRON_BLOCK.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

        private final BlockState ore;
        private final BlockState rawBlock;
        private final BlockState stone;
        private final int minY;
        private final int maxY;

        private VeinType(BlockState ore, BlockState rawBlock, BlockState stone, int minY, int maxY) {
            this.ore = ore;
            this.rawBlock = rawBlock;
            this.stone = stone;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}

