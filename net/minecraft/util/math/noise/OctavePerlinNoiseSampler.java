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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import org.jetbrains.annotations.Nullable;

public class OctavePerlinNoiseSampler
implements NoiseSampler {
    private final PerlinNoiseSampler[] octaveSamplers;
    private final DoubleList field_26445;
    private final double field_20659;
    private final double field_20660;

    public OctavePerlinNoiseSampler(ChunkRandom random, IntStream octaves) {
        this(random, octaves.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(ChunkRandom random, List<Integer> octaves) {
        this(random, new IntRBTreeSet(octaves));
    }

    public static OctavePerlinNoiseSampler method_30847(ChunkRandom chunkRandom, int i, DoubleList doubleList) {
        return new OctavePerlinNoiseSampler(chunkRandom, Pair.of(i, doubleList));
    }

    private static Pair<Integer, DoubleList> method_30848(IntSortedSet intSortedSet) {
        int j;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -intSortedSet.firstInt();
        int k = i + (j = intSortedSet.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        DoubleArrayList doubleList = new DoubleArrayList(new double[k]);
        IntBidirectionalIterator intBidirectionalIterator = intSortedSet.iterator();
        while (intBidirectionalIterator.hasNext()) {
            int l = intBidirectionalIterator.nextInt();
            doubleList.set(l + i, 1.0);
        }
        return Pair.of(-i, doubleList);
    }

    private OctavePerlinNoiseSampler(ChunkRandom random, IntSortedSet octaves) {
        this(random, OctavePerlinNoiseSampler.method_30848(octaves));
    }

    private OctavePerlinNoiseSampler(ChunkRandom chunkRandom, Pair<Integer, DoubleList> pair) {
        double d;
        int i = pair.getFirst();
        this.field_26445 = pair.getSecond();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(chunkRandom);
        int j = this.field_26445.size();
        int k = -i;
        this.octaveSamplers = new PerlinNoiseSampler[j];
        if (k >= 0 && k < j && (d = this.field_26445.getDouble(k)) != 0.0) {
            this.octaveSamplers[k] = perlinNoiseSampler;
        }
        for (int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double e = this.field_26445.getDouble(l);
                if (e != 0.0) {
                    this.octaveSamplers[l] = new PerlinNoiseSampler(chunkRandom);
                    continue;
                }
                chunkRandom.consume(262);
                continue;
            }
            chunkRandom.consume(262);
        }
        if (k < j - 1) {
            long m = (long)(perlinNoiseSampler.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372036854776E18);
            ChunkRandom chunkRandom2 = new ChunkRandom(m);
            for (int n = k + 1; n < j; ++n) {
                if (n >= 0) {
                    double f = this.field_26445.getDouble(n);
                    if (f != 0.0) {
                        this.octaveSamplers[n] = new PerlinNoiseSampler(chunkRandom2);
                        continue;
                    }
                    chunkRandom2.consume(262);
                    continue;
                }
                chunkRandom2.consume(262);
            }
        }
        this.field_20660 = Math.pow(2.0, -k);
        this.field_20659 = Math.pow(2.0, j - 1) / (Math.pow(2.0, j) - 1.0);
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0, 0.0, false);
    }

    public double sample(double x, double y, double z, double d, double e, boolean bl) {
        double f = 0.0;
        double g = this.field_20660;
        double h = this.field_20659;
        for (int i = 0; i < this.octaveSamplers.length; ++i) {
            PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
            if (perlinNoiseSampler != null) {
                f += this.field_26445.getDouble(i) * perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(x * g), bl ? -perlinNoiseSampler.originY : OctavePerlinNoiseSampler.maintainPrecision(y * g), OctavePerlinNoiseSampler.maintainPrecision(z * g), d * g, e * g) * h;
            }
            g *= 2.0;
            h /= 2.0;
        }
        return f;
    }

    @Nullable
    public PerlinNoiseSampler getOctave(int octave) {
        return this.octaveSamplers[this.octaveSamplers.length - 1 - octave];
    }

    public static double maintainPrecision(double d) {
        return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double sample(double x, double y, double d, double e) {
        return this.sample(x, y, 0.0, d, e, false);
    }
}

