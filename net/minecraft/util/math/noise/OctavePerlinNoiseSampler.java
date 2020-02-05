/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
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
    private final double field_20659;
    private final double field_20660;

    public OctavePerlinNoiseSampler(ChunkRandom random, IntStream octaves) {
        this(random, octaves.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavePerlinNoiseSampler(ChunkRandom random, List<Integer> octaves) {
        this(random, new IntRBTreeSet(octaves));
    }

    private OctavePerlinNoiseSampler(ChunkRandom random, IntSortedSet octaves) {
        int j;
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -octaves.firstInt();
        int k = i + (j = octaves.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
        int l = j;
        this.octaveSamplers = new PerlinNoiseSampler[k];
        if (l >= 0 && l < k && octaves.contains(0)) {
            this.octaveSamplers[l] = perlinNoiseSampler;
        }
        for (int m = l + 1; m < k; ++m) {
            if (m >= 0 && octaves.contains(l - m)) {
                this.octaveSamplers[m] = new PerlinNoiseSampler(random);
                continue;
            }
            random.consume(262);
        }
        if (j > 0) {
            long n = (long)(perlinNoiseSampler.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372036854776E18);
            ChunkRandom chunkRandom = new ChunkRandom(n);
            for (int o = l - 1; o >= 0; --o) {
                if (o < k && octaves.contains(l - o)) {
                    this.octaveSamplers[o] = new PerlinNoiseSampler(chunkRandom);
                    continue;
                }
                chunkRandom.consume(262);
            }
        }
        this.field_20660 = Math.pow(2.0, j);
        this.field_20659 = 1.0 / (Math.pow(2.0, k) - 1.0);
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0, 0.0, false);
    }

    public double sample(double x, double y, double z, double d, double e, boolean bl) {
        double f = 0.0;
        double g = this.field_20660;
        double h = this.field_20659;
        for (PerlinNoiseSampler perlinNoiseSampler : this.octaveSamplers) {
            if (perlinNoiseSampler != null) {
                f += perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(x * g), bl ? -perlinNoiseSampler.originY : OctavePerlinNoiseSampler.maintainPrecision(y * g), OctavePerlinNoiseSampler.maintainPrecision(z * g), d * g, e * g) * h;
            }
            g /= 2.0;
            h *= 2.0;
        }
        return f;
    }

    @Nullable
    public PerlinNoiseSampler getOctave(int octave) {
        return this.octaveSamplers[octave];
    }

    public static double maintainPrecision(double d) {
        return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double sample(double x, double y, double d, double e) {
        return this.sample(x, y, 0.0, d, e, false);
    }
}

