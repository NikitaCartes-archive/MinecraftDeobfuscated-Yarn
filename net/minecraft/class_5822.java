/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.stream.IntStream;
import net.minecraft.class_5819;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;

public class class_5822 {
    private OctavePerlinNoiseSampler field_28774;
    private OctavePerlinNoiseSampler field_28775;
    private OctavePerlinNoiseSampler field_28776;

    public class_5822(OctavePerlinNoiseSampler octavePerlinNoiseSampler, OctavePerlinNoiseSampler octavePerlinNoiseSampler2, OctavePerlinNoiseSampler octavePerlinNoiseSampler3) {
        this.field_28774 = octavePerlinNoiseSampler;
        this.field_28775 = octavePerlinNoiseSampler2;
        this.field_28776 = octavePerlinNoiseSampler3;
    }

    public class_5822(class_5819 arg) {
        this(new OctavePerlinNoiseSampler(arg, IntStream.rangeClosed(-15, 0)), new OctavePerlinNoiseSampler(arg, IntStream.rangeClosed(-15, 0)), new OctavePerlinNoiseSampler(arg, IntStream.rangeClosed(-7, 0)));
    }

    public double method_33657(int i, int j, int k, double d, double e, double f, double g) {
        double h = 0.0;
        double l = 0.0;
        double m = 0.0;
        boolean bl = true;
        double n = 1.0;
        for (int o = 0; o < 16; ++o) {
            PerlinNoiseSampler perlinNoiseSampler3;
            PerlinNoiseSampler perlinNoiseSampler2;
            double p = OctavePerlinNoiseSampler.maintainPrecision((double)i * d * n);
            double q = OctavePerlinNoiseSampler.maintainPrecision((double)j * e * n);
            double r = OctavePerlinNoiseSampler.maintainPrecision((double)k * d * n);
            double s = e * n;
            PerlinNoiseSampler perlinNoiseSampler = this.field_28774.getOctave(o);
            if (perlinNoiseSampler != null) {
                h += perlinNoiseSampler.sample(p, q, r, s, (double)j * s) / n;
            }
            if ((perlinNoiseSampler2 = this.field_28775.getOctave(o)) != null) {
                l += perlinNoiseSampler2.sample(p, q, r, s, (double)j * s) / n;
            }
            if (o < 8 && (perlinNoiseSampler3 = this.field_28776.getOctave(o)) != null) {
                m += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((double)i * f * n), OctavePerlinNoiseSampler.maintainPrecision((double)j * g * n), OctavePerlinNoiseSampler.maintainPrecision((double)k * f * n), g * n, (double)j * g * n) / n;
            }
            n /= 2.0;
        }
        return MathHelper.clampedLerp(h / 512.0, l / 512.0, (m / 10.0 + 1.0) / 2.0);
    }
}

