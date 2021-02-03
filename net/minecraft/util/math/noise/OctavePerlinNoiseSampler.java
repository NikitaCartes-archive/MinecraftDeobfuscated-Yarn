/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.class_5819;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import org.jetbrains.annotations.Nullable;

public class OctavePerlinNoiseSampler
implements NoiseSampler {
    private final PerlinNoiseSampler[] octaveSamplers;
    private final DoubleList amplitudes;
    private final double persistence;
    private final double lacunarity;

    public OctavePerlinNoiseSampler(class_5819 arg, IntStream octaves) {
        this(arg, octaves.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(class_5819 arg, List<Integer> octaves) {
        this(arg, new IntRBTreeSet(octaves));
    }

    public static OctavePerlinNoiseSampler create(class_5819 arg, int offset, DoubleList amplitudes) {
        return new OctavePerlinNoiseSampler(arg, Pair.of(offset, amplitudes));
    }

    private static Pair<Integer, DoubleList> calculateAmplitudes(IntSortedSet octaves) {
        int j;
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -octaves.firstInt();
        int k = i + (j = octaves.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        DoubleArrayList doubleList = new DoubleArrayList(new double[k]);
        IntBidirectionalIterator intBidirectionalIterator = octaves.iterator();
        while (intBidirectionalIterator.hasNext()) {
            int l = intBidirectionalIterator.nextInt();
            doubleList.set(l + i, 1.0);
        }
        return Pair.of(-i, doubleList);
    }

    private OctavePerlinNoiseSampler(class_5819 arg, IntSortedSet octaves) {
        this(arg, OctavePerlinNoiseSampler.calculateAmplitudes(octaves));
    }

    private OctavePerlinNoiseSampler(class_5819 arg, Pair<Integer, DoubleList> offsetAndAmplitudes) {
        double d;
        int i = offsetAndAmplitudes.getFirst();
        this.amplitudes = offsetAndAmplitudes.getSecond();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(arg);
        int j = this.amplitudes.size();
        int k = -i;
        this.octaveSamplers = new PerlinNoiseSampler[j];
        if (k >= 0 && k < j && (d = this.amplitudes.getDouble(k)) != 0.0) {
            this.octaveSamplers[k] = perlinNoiseSampler;
        }
        for (int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double e = this.amplitudes.getDouble(l);
                if (e != 0.0) {
                    this.octaveSamplers[l] = new PerlinNoiseSampler(arg);
                    continue;
                }
                arg.method_33650(262);
                continue;
            }
            arg.method_33650(262);
        }
        if (k < j - 1) {
            long m = (long)(perlinNoiseSampler.method_33658(0.0, 0.0, 0.0) * 9.223372036854776E18);
            ChunkRandom lv = new ChunkRandom(m);
            for (int n = k + 1; n < j; ++n) {
                if (n >= 0) {
                    double f = this.amplitudes.getDouble(n);
                    if (f != 0.0) {
                        this.octaveSamplers[n] = new PerlinNoiseSampler(lv);
                        continue;
                    }
                    lv.method_33650(262);
                    continue;
                }
                lv.method_33650(262);
            }
        }
        this.lacunarity = Math.pow(2.0, -k);
        this.persistence = Math.pow(2.0, j - 1) / (Math.pow(2.0, j) - 1.0);
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0, 0.0, false);
    }

    @Deprecated
    public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
        double d = 0.0;
        double e = this.lacunarity;
        double f = this.persistence;
        for (int i = 0; i < this.octaveSamplers.length; ++i) {
            PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
            if (perlinNoiseSampler != null) {
                d += this.amplitudes.getDouble(i) * perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : OctavePerlinNoiseSampler.maintainPrecision(y * e), OctavePerlinNoiseSampler.maintainPrecision(z * e), yScale * e, yMax * e) * f;
            }
            e *= 2.0;
            f /= 2.0;
        }
        return d;
    }

    @Nullable
    public PerlinNoiseSampler getOctave(int octave) {
        return this.octaveSamplers[this.octaveSamplers.length - 1 - octave];
    }

    public static double maintainPrecision(double value) {
        return value - (double)MathHelper.lfloor(value / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double sample(double x, double y, double yScale, double yMax) {
        return this.sample(x, y, 0.0, yScale, yMax, false);
    }
}

