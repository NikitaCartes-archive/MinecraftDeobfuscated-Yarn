/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;

public class OctaveSimplexNoiseSampler {
    private final SimplexNoiseSampler[] octaveSamplers;
    private final double persistence;
    private final double lacunarity;

    public OctaveSimplexNoiseSampler(Random random, List<Integer> octaves) {
        this(random, new IntRBTreeSet(octaves));
    }

    private OctaveSimplexNoiseSampler(Random random, IntSortedSet octaves) {
        int j;
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -octaves.firstInt();
        int k = i + (j = octaves.lastInt()) + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(random);
        int l = j;
        this.octaveSamplers = new SimplexNoiseSampler[k];
        if (l >= 0 && l < k && octaves.contains(0)) {
            this.octaveSamplers[l] = simplexNoiseSampler;
        }
        for (int m = l + 1; m < k; ++m) {
            if (m >= 0 && octaves.contains(l - m)) {
                this.octaveSamplers[m] = new SimplexNoiseSampler(random);
                continue;
            }
            random.skip(262);
        }
        if (j > 0) {
            long n = (long)(simplexNoiseSampler.sample(simplexNoiseSampler.originX, simplexNoiseSampler.originY, simplexNoiseSampler.originZ) * 9.223372036854776E18);
            ChunkRandom random2 = new ChunkRandom(new CheckedRandom(n));
            for (int o = l - 1; o >= 0; --o) {
                if (o < k && octaves.contains(l - o)) {
                    this.octaveSamplers[o] = new SimplexNoiseSampler(random2);
                    continue;
                }
                random2.skip(262);
            }
        }
        this.lacunarity = Math.pow(2.0, j);
        this.persistence = 1.0 / (Math.pow(2.0, k) - 1.0);
    }

    public double sample(double x, double y, boolean useOrigin) {
        double d = 0.0;
        double e = this.lacunarity;
        double f = this.persistence;
        for (SimplexNoiseSampler simplexNoiseSampler : this.octaveSamplers) {
            if (simplexNoiseSampler != null) {
                d += simplexNoiseSampler.sample(x * e + (useOrigin ? simplexNoiseSampler.originX : 0.0), y * e + (useOrigin ? simplexNoiseSampler.originY : 0.0)) * f;
            }
            e /= 2.0;
            f *= 2.0;
        }
        return d;
    }
}

