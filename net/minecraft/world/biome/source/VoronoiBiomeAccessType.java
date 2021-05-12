/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.SeedMixer;

public enum VoronoiBiomeAccessType implements BiomeAccessType
{
    INSTANCE;

    private static final int field_30979 = 2;
    private static final int field_30980 = 4;
    private static final int field_30981 = 3;

    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        int p;
        int i = x - 2;
        int j = y - 2;
        int k = z - 2;
        int l = i >> 2;
        int m = j >> 2;
        int n = k >> 2;
        double d = (double)(i & 3) / 4.0;
        double e = (double)(j & 3) / 4.0;
        double f = (double)(k & 3) / 4.0;
        int o = 0;
        double g = Double.POSITIVE_INFINITY;
        for (p = 0; p < 8; ++p) {
            double u;
            double t;
            double h;
            boolean bl3;
            int s;
            boolean bl2;
            int r;
            boolean bl = (p & 4) == 0;
            int q = bl ? l : l + 1;
            double v = VoronoiBiomeAccessType.calcSquaredDistance(seed, q, r = (bl2 = (p & 2) == 0) ? m : m + 1, s = (bl3 = (p & 1) == 0) ? n : n + 1, h = bl ? d : d - 1.0, t = bl2 ? e : e - 1.0, u = bl3 ? f : f - 1.0);
            if (!(g > v)) continue;
            o = p;
            g = v;
        }
        p = (o & 4) == 0 ? l : l + 1;
        int w = (o & 2) == 0 ? m : m + 1;
        int aa = (o & 1) == 0 ? n : n + 1;
        return storage.getBiomeForNoiseGen(p, w, aa);
    }

    private static double calcSquaredDistance(long seed, int x, int y, int z, double xFraction, double yFraction, double zFraction) {
        long l = seed;
        l = SeedMixer.mixSeed(l, x);
        l = SeedMixer.mixSeed(l, y);
        l = SeedMixer.mixSeed(l, z);
        l = SeedMixer.mixSeed(l, x);
        l = SeedMixer.mixSeed(l, y);
        l = SeedMixer.mixSeed(l, z);
        double d = VoronoiBiomeAccessType.distribute(l);
        l = SeedMixer.mixSeed(l, seed);
        double e = VoronoiBiomeAccessType.distribute(l);
        l = SeedMixer.mixSeed(l, seed);
        double f = VoronoiBiomeAccessType.distribute(l);
        return VoronoiBiomeAccessType.square(zFraction + f) + VoronoiBiomeAccessType.square(yFraction + e) + VoronoiBiomeAccessType.square(xFraction + d);
    }

    private static double distribute(long seed) {
        double d = (double)Math.floorMod(seed >> 24, 1024) / 1024.0;
        return (d - 0.5) * 0.9;
    }

    private static double square(double d) {
        return d * d;
    }
}

