/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.jetbrains.annotations.Nullable;

public class OctavePerlinNoiseSampler {
    private static final int field_31704 = 0x2000000;
    private final PerlinNoiseSampler[] octaveSamplers;
    private final int field_34758;
    private final DoubleList amplitudes;
    private final double persistence;
    private final double lacunarity;

    public OctavePerlinNoiseSampler(AbstractRandom random, IntStream octaves) {
        this(random, octaves.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(AbstractRandom random, List<Integer> octaves) {
        this(random, new IntRBTreeSet(octaves));
    }

    @Deprecated
    public static OctavePerlinNoiseSampler method_39125(AbstractRandom abstractRandom, int i, double d, double ... ds) {
        DoubleArrayList doubleArrayList = new DoubleArrayList(ds);
        doubleArrayList.add(0, d);
        return OctavePerlinNoiseSampler.method_39126(abstractRandom, i, doubleArrayList);
    }

    @Deprecated
    public static OctavePerlinNoiseSampler method_39126(AbstractRandom abstractRandom, int i, DoubleList doubleList) {
        return new OctavePerlinNoiseSampler(abstractRandom, Pair.of(i, doubleList));
    }

    public static OctavePerlinNoiseSampler method_39128(AbstractRandom abstractRandom, IntStream intStream) {
        return OctavePerlinNoiseSampler.method_39127(abstractRandom, intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public static OctavePerlinNoiseSampler method_39127(AbstractRandom abstractRandom, List<Integer> list) {
        return new OctavePerlinNoiseSampler(abstractRandom, OctavePerlinNoiseSampler.calculateAmplitudes(new IntRBTreeSet(list)), true);
    }

    public static OctavePerlinNoiseSampler create(AbstractRandom abstractRandom, int offset, double d, double ... ds) {
        DoubleArrayList doubleArrayList = new DoubleArrayList(ds);
        doubleArrayList.add(0, d);
        return new OctavePerlinNoiseSampler(abstractRandom, Pair.of(offset, doubleArrayList), true);
    }

    public static OctavePerlinNoiseSampler create(AbstractRandom random, int offset, DoubleList amplitudes) {
        return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes), true);
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

    private OctavePerlinNoiseSampler(AbstractRandom random, IntSortedSet octaves) {
        this(random, OctavePerlinNoiseSampler.calculateAmplitudes(octaves), false);
    }

    protected OctavePerlinNoiseSampler(AbstractRandom random, Pair<Integer, DoubleList> offsetAndAmplitudes) {
        this(random, offsetAndAmplitudes, false);
    }

    protected OctavePerlinNoiseSampler(AbstractRandom abstractRandom, Pair<Integer, DoubleList> pair, boolean bl) {
        this.field_34758 = pair.getFirst();
        this.amplitudes = pair.getSecond();
        int i = this.amplitudes.size();
        int j = -this.field_34758;
        this.octaveSamplers = new PerlinNoiseSampler[i];
        if (bl) {
            RandomDeriver randomDeriver = abstractRandom.createBlockPosRandomDeriver();
            for (int k = 0; k < i; ++k) {
                if (this.amplitudes.getDouble(k) == 0.0) continue;
                int l = this.field_34758 + k;
                this.octaveSamplers[k] = new PerlinNoiseSampler(randomDeriver.createRandom("octave_" + l));
            }
        } else {
            double d;
            PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(abstractRandom);
            if (j >= 0 && j < i && (d = this.amplitudes.getDouble(j)) != 0.0) {
                this.octaveSamplers[j] = perlinNoiseSampler;
            }
            for (int k = j - 1; k >= 0; --k) {
                if (k < i) {
                    double e = this.amplitudes.getDouble(k);
                    if (e != 0.0) {
                        this.octaveSamplers[k] = new PerlinNoiseSampler(abstractRandom);
                        continue;
                    }
                    OctavePerlinNoiseSampler.skipCalls(abstractRandom);
                    continue;
                }
                OctavePerlinNoiseSampler.skipCalls(abstractRandom);
            }
            if (Arrays.stream(this.octaveSamplers).filter(Objects::nonNull).count() != this.amplitudes.stream().filter(double_ -> double_ != 0.0).count()) {
                throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
            }
            if (j < i - 1) {
                throw new IllegalArgumentException("Positive octaves are temporarily disabled");
            }
        }
        this.lacunarity = Math.pow(2.0, -j);
        this.persistence = Math.pow(2.0, i - 1) / (Math.pow(2.0, i) - 1.0);
    }

    private static void skipCalls(AbstractRandom random) {
        random.skip(262);
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
                double g = perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : OctavePerlinNoiseSampler.maintainPrecision(y * e), OctavePerlinNoiseSampler.maintainPrecision(z * e), yScale * e, yMax * e);
                d += this.amplitudes.getDouble(i) * g * f;
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

    protected int method_38477() {
        return this.field_34758;
    }

    protected DoubleList method_38478() {
        return this.amplitudes;
    }

    @VisibleForTesting
    public void addDebugInfo(StringBuilder info) {
        info.append("PerlinNoise{");
        List<String> list = this.amplitudes.stream().map(double_ -> String.format("%.2f", double_)).toList();
        info.append("first octave: ").append(this.field_34758).append(", amplitudes: ").append(list).append(", noise levels: [");
        for (int i = 0; i < this.octaveSamplers.length; ++i) {
            info.append(i).append(": ");
            PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
            if (perlinNoiseSampler == null) {
                info.append("null");
            } else {
                perlinNoiseSampler.addDebugInfo(info);
            }
            info.append(", ");
        }
        info.append("]");
        info.append("}");
    }
}

