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
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.jetbrains.annotations.Nullable;

public class OctavePerlinNoiseSampler
implements NoiseSampler {
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

    public static OctavePerlinNoiseSampler create(AbstractRandom random, int offset, double ... amplitudes) {
        return OctavePerlinNoiseSampler.create(random, offset, new DoubleArrayList(amplitudes));
    }

    public static OctavePerlinNoiseSampler create(AbstractRandom random, int offset, DoubleList amplitudes) {
        return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes));
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
        this(random, octaves, ChunkRandom::new);
    }

    private OctavePerlinNoiseSampler(AbstractRandom random, IntSortedSet octaves, LongFunction<AbstractRandom> randomFunction) {
        this(random, OctavePerlinNoiseSampler.calculateAmplitudes(octaves), randomFunction);
    }

    protected OctavePerlinNoiseSampler(AbstractRandom random, Pair<Integer, DoubleList> offsetAndAmplitudes) {
        this(random, offsetAndAmplitudes, ChunkRandom::new);
    }

    protected OctavePerlinNoiseSampler(AbstractRandom random, Pair<Integer, DoubleList> pair, LongFunction<AbstractRandom> randomFunction) {
        double d;
        this.field_34758 = pair.getFirst();
        this.amplitudes = pair.getSecond();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
        int i = this.amplitudes.size();
        int j = -this.field_34758;
        this.octaveSamplers = new PerlinNoiseSampler[i];
        if (j >= 0 && j < i && (d = this.amplitudes.getDouble(j)) != 0.0) {
            this.octaveSamplers[j] = perlinNoiseSampler;
        }
        for (int k = j - 1; k >= 0; --k) {
            if (k < i) {
                double e = this.amplitudes.getDouble(k);
                if (e != 0.0) {
                    this.octaveSamplers[k] = new PerlinNoiseSampler(random);
                    continue;
                }
                OctavePerlinNoiseSampler.skipCalls(random);
                continue;
            }
            OctavePerlinNoiseSampler.skipCalls(random);
        }
        if (j < i - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
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

    @Override
    public double sample(double x, double y, double yScale, double yMax) {
        return this.sample(x, y, 0.0, yScale, yMax, false);
    }

    protected int method_38477() {
        return this.field_34758;
    }

    protected DoubleList method_38478() {
        return this.amplitudes;
    }
}

