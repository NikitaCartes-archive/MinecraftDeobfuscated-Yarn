/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.WorldGenRandom;
import org.jetbrains.annotations.Nullable;

public class class_6353 {
    private static final double field_33588 = 1.0;
    private static final double field_33589 = 4.0;
    private static final double field_33590 = 0.08;
    private static final double field_33591 = 0.3;
    private static final float field_33592 = 0.7f;
    private static final double field_33593 = 0.3;
    private static final double field_33594 = 0.00390625;
    private final int field_33595;
    private final int field_33596;
    private final BlockState field_33597;
    private final DoublePerlinNoiseSampler field_33598;
    private final DoublePerlinNoiseSampler field_33599;
    private final DoublePerlinNoiseSampler field_33600;
    private final int field_33601;
    private final int field_33602;

    public class_6353(long l, BlockState blockState, int i, int j, int k) {
        Random random = new Random(l);
        this.field_33597 = blockState;
        this.field_33598 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), 0, 1.0);
        this.field_33599 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.field_33600 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(random.nextLong()), -7, 1.0);
        this.field_33601 = i;
        this.field_33602 = j;
        this.field_33595 = Stream.of(class_6354.values()).mapToInt(arg -> ((class_6354)arg).field_33608).max().orElse(k);
        this.field_33596 = Stream.of(class_6354.values()).mapToInt(arg -> ((class_6354)arg).field_33607).min().orElse(k);
    }

    public void method_36401(double[] ds, int i, int j, int k, int l) {
        this.method_36402(ds, i, j, this.field_33598, 0.00390625, k, l);
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

    public BlockState method_36400(WorldGenRandom worldGenRandom, int i, double d, double e, double f) {
        BlockState blockState = this.field_33597;
        class_6354 lv = this.method_36397(d);
        if (lv == null || i < lv.field_33607 || i > lv.field_33608) {
            return blockState;
        }
        if (worldGenRandom.nextFloat() > 0.7f) {
            return blockState;
        }
        if (this.method_36398(e, f)) {
            return (double)worldGenRandom.nextFloat() < 0.3 ? lv.field_33605 : lv.field_33606;
        }
        return blockState;
    }

    private boolean method_36398(double d, double e) {
        double g;
        double f = Math.abs(1.0 * d) - 0.08;
        return Math.max(f, g = Math.abs(1.0 * e) - 0.08) < 0.0;
    }

    @Nullable
    private class_6354 method_36397(double d) {
        if (Math.abs(d) < 0.3) {
            return null;
        }
        return d > 0.0 ? class_6354.field_33603 : class_6354.field_33604;
    }

    static enum class_6354 {
        field_33603(Blocks.COPPER_ORE.getDefaultState(), Blocks.GRANITE.getDefaultState(), 0, 50),
        field_33604(Blocks.DEEPSLATE_IRON_ORE.getDefaultState(), Blocks.TUFF.getDefaultState(), -60, -8);

        private final BlockState field_33605;
        private final BlockState field_33606;
        private final int field_33607;
        private final int field_33608;

        private class_6354(BlockState blockState, BlockState blockState2, int j, int k) {
            this.field_33605 = blockState;
            this.field_33606 = blockState2;
            this.field_33607 = j;
            this.field_33608 = k;
        }
    }
}

