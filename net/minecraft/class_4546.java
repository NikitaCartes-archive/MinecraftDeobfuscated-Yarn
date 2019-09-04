/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_4540;
import net.minecraft.class_4543;
import net.minecraft.class_4545;
import net.minecraft.world.biome.Biome;

public enum class_4546 implements class_4545
{
    INSTANCE;


    @Override
    public Biome method_22396(long l, int i, int j, int k, class_4543.class_4544 arg) {
        int y;
        int u;
        int t;
        int s;
        int m = i - 2;
        int n = j - 2;
        int o = k - 2;
        int p = m >> 2;
        int q = n >> 2;
        int r = o >> 2;
        double d = (double)(m & 3) / 4.0;
        double e = (double)(n & 3) / 4.0;
        double f = (double)(o & 3) / 4.0;
        double[] ds = new double[8];
        for (s = 0; s < 8; ++s) {
            boolean bl = (s & 4) == 0;
            boolean bl2 = (s & 2) == 0;
            boolean bl3 = (s & 1) == 0;
            t = bl ? p : p + 1;
            u = bl2 ? q : q + 1;
            int v = bl3 ? r : r + 1;
            double g = bl ? d : 1.0 - d;
            double h = bl2 ? e : 1.0 - e;
            double w = bl3 ? f : 1.0 - f;
            ds[s] = class_4546.method_22399(l, t, u, v, g, h, w);
        }
        s = 0;
        double x = ds[0];
        for (y = 1; y < 8; ++y) {
            if (!(x > ds[y])) continue;
            s = y;
            x = ds[y];
        }
        y = (s & 4) == 0 ? p : p + 1;
        t = (s & 2) == 0 ? q : q + 1;
        u = (s & 1) == 0 ? r : r + 1;
        return arg.getBiome(y, t, u);
    }

    private static double method_22399(long l, int i, int j, int k, double d, double e, double f) {
        long m = l;
        m = class_4540.method_22372(m, i);
        m = class_4540.method_22372(m, j);
        m = class_4540.method_22372(m, k);
        m = class_4540.method_22372(m, i);
        m = class_4540.method_22372(m, j);
        m = class_4540.method_22372(m, k);
        double g = class_4546.method_22398(m);
        m = class_4540.method_22372(m, l);
        double h = class_4546.method_22398(m);
        m = class_4540.method_22372(m, l);
        double n = class_4546.method_22398(m);
        return class_4546.method_22397(f + n) + class_4546.method_22397(e + h) + class_4546.method_22397(d + g);
    }

    private static double method_22398(long l) {
        double d = (double)((int)Math.floorMod(l >> 24, 1024L)) / 1024.0;
        return (d - 0.5) * 0.9;
    }

    private static double method_22397(double d) {
        return d * d;
    }
}

