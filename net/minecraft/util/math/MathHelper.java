/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
    public static final float SQUARE_ROOT_OF_TWO = MathHelper.sqrt(2.0f);
    private static final float[] SINE_TABLE = Util.make(new float[65536], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i) {
            fs[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
    });
    private static final Random RANDOM = new Random();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ARCSINE_TABLE = new double[257];
    private static final double[] COSINE_TABLE = new double[257];

    public static float sin(float f) {
        return SINE_TABLE[(int)(f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SINE_TABLE[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float f) {
        return (float)Math.sqrt(f);
    }

    public static float sqrt(double d) {
        return (float)Math.sqrt(d);
    }

    public static int floor(float f) {
        int i = (int)f;
        return f < (float)i ? i - 1 : i;
    }

    @Environment(value=EnvType.CLIENT)
    public static int fastFloor(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static int floor(double d) {
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }

    public static long lfloor(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }

    @Environment(value=EnvType.CLIENT)
    public static int absFloor(double d) {
        return (int)(d >= 0.0 ? d : -d + 1.0);
    }

    public static float abs(float f) {
        return Math.abs(f);
    }

    public static int abs(int i) {
        return Math.abs(i);
    }

    public static int ceil(float f) {
        int i = (int)f;
        return f > (float)i ? i + 1 : i;
    }

    public static int ceil(double d) {
        int i = (int)d;
        return d > (double)i ? i + 1 : i;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Environment(value=EnvType.CLIENT)
    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double clampedLerp(double first, double second, double delta) {
        if (delta < 0.0) {
            return first;
        }
        if (delta > 1.0) {
            return second;
        }
        return MathHelper.lerp(delta, first, second);
    }

    public static double absMax(double d, double e) {
        if (d < 0.0) {
            d = -d;
        }
        if (e < 0.0) {
            e = -e;
        }
        return d > e ? d : e;
    }

    public static int floorDiv(int i, int j) {
        return Math.floorDiv(i, j);
    }

    public static int nextInt(Random random, int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        if (min >= max) {
            return min;
        }
        return random.nextFloat() * (max - min) + min;
    }

    public static double nextDouble(Random random, double min, double max) {
        if (min >= max) {
            return min;
        }
        return random.nextDouble() * (max - min) + min;
    }

    public static double average(long[] array) {
        long l = 0L;
        for (long m : array) {
            l += m;
        }
        return (double)l / (double)array.length;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < 1.0E-5f;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < (double)1.0E-5f;
    }

    public static int floorMod(int i, int j) {
        return Math.floorMod(i, j);
    }

    @Environment(value=EnvType.CLIENT)
    public static float floorMod(float f, float g) {
        return (f % g + g) % g;
    }

    @Environment(value=EnvType.CLIENT)
    public static double floorMod(double d, double e) {
        return (d % e + e) % e;
    }

    @Environment(value=EnvType.CLIENT)
    public static int wrapDegrees(int i) {
        int j = i % 360;
        if (j >= 180) {
            j -= 360;
        }
        if (j < -180) {
            j += 360;
        }
        return j;
    }

    public static float wrapDegrees(float f) {
        float g = f % 360.0f;
        if (g >= 180.0f) {
            g -= 360.0f;
        }
        if (g < -180.0f) {
            g += 360.0f;
        }
        return g;
    }

    public static double wrapDegrees(double d) {
        double e = d % 360.0;
        if (e >= 180.0) {
            e -= 360.0;
        }
        if (e < -180.0) {
            e += 360.0;
        }
        return e;
    }

    public static float subtractAngles(float start, float end) {
        return MathHelper.wrapDegrees(end - start);
    }

    public static float angleBetween(float first, float second) {
        return MathHelper.abs(MathHelper.subtractAngles(first, second));
    }

    public static float method_20306(float start, float end, float speed) {
        float f = MathHelper.subtractAngles(start, end);
        float g = MathHelper.clamp(f, -speed, speed);
        return end - g;
    }

    public static float method_15348(float f, float g, float h) {
        h = MathHelper.abs(h);
        if (f < g) {
            return MathHelper.clamp(f + h, f, g);
        }
        return MathHelper.clamp(f - h, g, f);
    }

    public static float method_15388(float f, float g, float h) {
        float i = MathHelper.subtractAngles(f, g);
        return MathHelper.method_15348(f, f + i, h);
    }

    @Environment(value=EnvType.CLIENT)
    public static int parseInt(String string, int fallback) {
        return NumberUtils.toInt(string, fallback);
    }

    @Environment(value=EnvType.CLIENT)
    public static int parseInt(String string, int fallback, int minimum) {
        return Math.max(minimum, MathHelper.parseInt(string, fallback));
    }

    @Environment(value=EnvType.CLIENT)
    public static double parseDouble(String string, double fallback) {
        try {
            return Double.parseDouble(string);
        } catch (Throwable throwable) {
            return fallback;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static double parseDouble(String string, double fallback, double d) {
        return Math.max(d, MathHelper.parseDouble(string, fallback));
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    private static boolean isPowerOfTwo(int i) {
        return i != 0 && (i & i - 1) == 0;
    }

    public static int log2DeBruijn(int i) {
        i = MathHelper.isPowerOfTwo(i) ? i : MathHelper.smallestEncompassingPowerOfTwo(i);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)i * 125613361L >> 27) & 0x1F];
    }

    public static int log2(int i) {
        return MathHelper.log2DeBruijn(i) - (MathHelper.isPowerOfTwo(i) ? 0 : 1);
    }

    public static int roundUp(int i, int j) {
        int k;
        if (j == 0) {
            return 0;
        }
        if (i == 0) {
            return j;
        }
        if (i < 0) {
            j *= -1;
        }
        if ((k = i % j) == 0) {
            return i;
        }
        return i + j - k;
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(float r, float g, float b) {
        return MathHelper.packRgb(MathHelper.floor(r * 255.0f), MathHelper.floor(g * 255.0f), MathHelper.floor(b * 255.0f));
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(int r, int g, int b) {
        int i = r;
        i = (i << 8) + g;
        i = (i << 8) + b;
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fractionalPart(float value) {
        return value - (float)MathHelper.floor(value);
    }

    public static double fractionalPart(double value) {
        return value - (double)MathHelper.lfloor(value);
    }

    public static long hashCode(Vec3i vec) {
        return MathHelper.hashCode(vec.getX(), vec.getY(), vec.getZ());
    }

    public static long hashCode(int x, int y, int z) {
        long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID randomUuid(Random random) {
        long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long m = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(l, m);
    }

    public static UUID randomUUID() {
        return MathHelper.randomUuid(RANDOM);
    }

    public static double minusDiv(double numerator, double delta, double denominator) {
        return (numerator - delta) / (denominator - delta);
    }

    public static double atan2(double d, double e) {
        double g;
        boolean bl3;
        boolean bl2;
        boolean bl;
        double f = e * e + d * d;
        if (Double.isNaN(f)) {
            return Double.NaN;
        }
        boolean bl4 = bl = d < 0.0;
        if (bl) {
            d = -d;
        }
        boolean bl5 = bl2 = e < 0.0;
        if (bl2) {
            e = -e;
        }
        boolean bl6 = bl3 = d > e;
        if (bl3) {
            g = e;
            e = d;
            d = g;
        }
        g = MathHelper.fastInverseSqrt(f);
        e *= g;
        double h = SMALLEST_FRACTION_FREE_DOUBLE + (d *= g);
        int i = (int)Double.doubleToRawLongBits(h);
        double j = ARCSINE_TABLE[i];
        double k = COSINE_TABLE[i];
        double l = h - SMALLEST_FRACTION_FREE_DOUBLE;
        double m = d * k - e * l;
        double n = (6.0 + m * m) * m * 0.16666666666666666;
        double o = j + n;
        if (bl3) {
            o = 1.5707963267948966 - o;
        }
        if (bl2) {
            o = Math.PI - o;
        }
        if (bl) {
            o = -o;
        }
        return o;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fastInverseSqrt(float x) {
        float f = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 1597463007 - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= 1.5f - f * x * x;
        return x;
    }

    public static double fastInverseSqrt(double x) {
        double d = 0.5 * x;
        long l = Double.doubleToRawLongBits(x);
        l = 6910469410427058090L - (l >> 1);
        x = Double.longBitsToDouble(l);
        x *= 1.5 - d * x * x;
        return x;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fastInverseCbrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        return f;
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        float m;
        float l;
        float k;
        int i = (int)(hue * 6.0f) % 6;
        float f = hue * 6.0f - (float)i;
        float g = value * (1.0f - saturation);
        float h = value * (1.0f - f * saturation);
        float j = value * (1.0f - (1.0f - f) * saturation);
        switch (i) {
            case 0: {
                k = value;
                l = j;
                m = g;
                break;
            }
            case 1: {
                k = h;
                l = value;
                m = g;
                break;
            }
            case 2: {
                k = g;
                l = value;
                m = j;
                break;
            }
            case 3: {
                k = g;
                l = h;
                m = value;
                break;
            }
            case 4: {
                k = j;
                l = g;
                m = value;
                break;
            }
            case 5: {
                k = value;
                l = g;
                m = h;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
            }
        }
        int n = MathHelper.clamp((int)(k * 255.0f), 0, 255);
        int o = MathHelper.clamp((int)(l * 255.0f), 0, 255);
        int p = MathHelper.clamp((int)(m * 255.0f), 0, 255);
        return n << 16 | o << 8 | p;
    }

    public static int idealHash(int i) {
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return i;
    }

    public static int binarySearch(int start, int end, IntPredicate leftPredicate) {
        int i = end - start;
        while (i > 0) {
            int j = i / 2;
            int k = start + j;
            if (leftPredicate.test(k)) {
                i = j;
                continue;
            }
            start = k + 1;
            i -= j + 1;
        }
        return start;
    }

    public static float lerp(float delta, float first, float second) {
        return first + delta * (second - first);
    }

    public static double lerp(double delta, double first, double second) {
        return first + delta * (second - first);
    }

    public static double lerp2(double deltaX, double deltaY, double d, double e, double f, double g) {
        return MathHelper.lerp(deltaY, MathHelper.lerp(deltaX, d, e), MathHelper.lerp(deltaX, f, g));
    }

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double d, double e, double f, double g, double h, double i, double j, double k) {
        return MathHelper.lerp(deltaZ, MathHelper.lerp2(deltaX, deltaY, d, e, f, g), MathHelper.lerp2(deltaX, deltaY, h, i, j, k));
    }

    public static double perlinFade(double d) {
        return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
    }

    public static int sign(double d) {
        if (d == 0.0) {
            return 0;
        }
        return d > 0.0 ? 1 : -1;
    }

    @Environment(value=EnvType.CLIENT)
    public static float lerpAngleDegrees(float delta, float first, float second) {
        return first + delta * MathHelper.wrapDegrees(second - first);
    }

    @Deprecated
    public static float method_22859(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static float method_22860(double d) {
        while (d >= 180.0) {
            d -= 360.0;
        }
        while (d < -180.0) {
            d += 360.0;
        }
        return (float)d;
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double)i / 256.0;
            double e = Math.asin(d);
            MathHelper.COSINE_TABLE[i] = Math.cos(e);
            MathHelper.ARCSINE_TABLE[i] = e;
        }
    }
}

