/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
    private static final int field_29850 = 1024;
    private static final float field_29851 = 1024.0f;
    private static final long field_29852 = 61440L;
    private static final long HALF_PI_RADIANS_SINE_TABLE_INDEX = 16384L;
    private static final long field_29854 = -4611686018427387904L;
    private static final long field_29855 = Long.MIN_VALUE;
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = 1.5707964f;
    /**
     * Tau is equal to {@code 2 * PI}.
     */
    public static final float TAU = (float)Math.PI * 2;
    public static final float RADIANS_PER_DEGREE = (float)Math.PI / 180;
    public static final float DEGREES_PER_RADIAN = 57.295776f;
    public static final float EPSILON = 1.0E-5f;
    public static final float SQUARE_ROOT_OF_TWO = MathHelper.sqrt(2.0f);
    private static final float DEGREES_TO_SINE_TABLE_INDEX = 10430.378f;
    private static final float[] SINE_TABLE = Util.make(new float[65536], sineTable -> {
        for (int i = 0; i < ((float[])sineTable).length; ++i) {
            sineTable[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
    });
    private static final Random RANDOM = new Random();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double field_29857 = 0.16666666666666666;
    private static final int field_29858 = 8;
    private static final int field_29859 = 257;
    private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ARCSINE_TABLE = new double[257];
    private static final double[] COSINE_TABLE = new double[257];

    public static float sin(float value) {
        return SINE_TABLE[(int)(value * 10430.378f) & 0xFFFF];
    }

    public static float cos(float value) {
        return SINE_TABLE[(int)(value * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float value) {
        return (float)Math.sqrt(value);
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int fastFloor(double value) {
        return (int)(value + 1024.0) - 1024;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static long lfloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public static int absFloor(double value) {
        return (int)(value >= 0.0 ? value : -value + 1.0);
    }

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static byte clamp(byte value, byte min, byte max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
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

    public static double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        if (delta > 1.0) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
    }

    public static float clampedLerp(float start, float end, float delta) {
        if (delta < 0.0f) {
            return start;
        }
        if (delta > 1.0f) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
    }

    public static double absMax(double a, double b) {
        if (a < 0.0) {
            a = -a;
        }
        if (b < 0.0) {
            b = -b;
        }
        return a > b ? a : b;
    }

    public static int floorDiv(int dividend, int divisor) {
        return Math.floorDiv(dividend, divisor);
    }

    /**
     * {@return a random, uniformly distributed integer value in {@code
     * [min, max]}} If the range is empty (i.e. {@code max < min}), it
     * returns {@code min}.
     * 
     * @param max the maximum value, inclusive
     * @param min the minimum value, inclusive
     */
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

    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < 1.0E-5f;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < (double)1.0E-5f;
    }

    public static int floorMod(int dividend, int divisor) {
        return Math.floorMod(dividend, divisor);
    }

    public static float floorMod(float dividend, float divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    public static double floorMod(double dividend, double divisor) {
        return (dividend % divisor + divisor) % divisor;
    }

    /**
     * Wraps an angle in degrees to the interval {@code [-180, 180)}.
     */
    public static int wrapDegrees(int degrees) {
        int i = degrees % 360;
        if (i >= 180) {
            i -= 360;
        }
        if (i < -180) {
            i += 360;
        }
        return i;
    }

    /**
     * Wraps an angle in degrees to the interval {@code [-180, 180)}.
     */
    public static float wrapDegrees(float degrees) {
        float f = degrees % 360.0f;
        if (f >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    /**
     * Wraps an angle in degrees to the interval {@code [-180, 180)}.
     */
    public static double wrapDegrees(double degrees) {
        double d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static float subtractAngles(float start, float end) {
        return MathHelper.wrapDegrees(end - start);
    }

    public static float angleBetween(float first, float second) {
        return MathHelper.abs(MathHelper.subtractAngles(first, second));
    }

    /**
     * Clamps {@code value}, as an angle, between {@code mean - delta} and {@code
     * mean + delta} degrees.
     * 
     * @return the clamped {@code value}
     * 
     * @param delta the maximum difference allowed from the mean, must not be negative
     * @param mean the mean value of the clamp angle range
     * @param value the value to clamp
     */
    public static float clampAngle(float value, float mean, float delta) {
        float f = MathHelper.subtractAngles(value, mean);
        float g = MathHelper.clamp(f, -delta, delta);
        return mean - g;
    }

    /**
     * Steps from {@code from} towards {@code to}, changing the value by at most {@code step}.
     */
    public static float stepTowards(float from, float to, float step) {
        step = MathHelper.abs(step);
        if (from < to) {
            return MathHelper.clamp(from + step, from, to);
        }
        return MathHelper.clamp(from - step, to, from);
    }

    /**
     * Steps from {@code from} degrees towards {@code to} degrees, changing the value by at most {@code step} degrees.
     */
    public static float stepUnwrappedAngleTowards(float from, float to, float step) {
        float f = MathHelper.subtractAngles(from, to);
        return MathHelper.stepTowards(from, from + f, step);
    }

    public static int parseInt(String string, int fallback) {
        return NumberUtils.toInt(string, fallback);
    }

    /**
     * {@return the parsed integer; {@code fallback} if {@code string} is not an
     * integer; or {@code min} if the parsed integer is too small}
     * 
     * @param min the minimum if the parsed value is too small
     * @param string the string to parse
     * @param fallback the fallback for unparsable {@code string}
     */
    public static int parseInt(String string, int fallback, int min) {
        return Math.max(min, MathHelper.parseInt(string, fallback));
    }

    public static double parseDouble(String string, double fallback) {
        try {
            return Double.parseDouble(string);
        } catch (Throwable throwable) {
            return fallback;
        }
    }

    /**
     * {@return the parsed double; {@code fallback} if {@code string} is not an
     * double; or {@code min} if the parsed double is too small}
     * 
     * @param min the minimum if the parsed value is too small
     * @param fallback the fallback for unparsable {@code string}
     * @param string the string to parse
     */
    public static double parseDouble(String string, double fallback, double min) {
        return Math.max(min, MathHelper.parseDouble(string, fallback));
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

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * {@return ceil(log<sub>2</sub>({@code value}))}
     * 
     * <p>The vanilla implementation uses the de Bruijn sequence.
     * 
     * @see Integer#numberOfLeadingZeros(int)
     * 
     * @param value the input value
     */
    public static int ceilLog2(int value) {
        value = MathHelper.isPowerOfTwo(value) ? value : MathHelper.smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 0x1F];
    }

    /**
     * {@return floor(log<sub>2</sub>({@code value}))}
     * 
     * <p>The vanilla implementation uses the de Bruijn sequence.
     * 
     * @see Integer#numberOfLeadingZeros(int)
     * 
     * @param value the input value
     */
    public static int floorLog2(int value) {
        return MathHelper.ceilLog2(value) - (MathHelper.isPowerOfTwo(value) ? 0 : 1);
    }

    public static int packRgb(float r, float g, float b) {
        return MathHelper.packRgb(MathHelper.floor(r * 255.0f), MathHelper.floor(g * 255.0f), MathHelper.floor(b * 255.0f));
    }

    public static int packRgb(int r, int g, int b) {
        int i = r;
        i = (i << 8) + g;
        i = (i << 8) + b;
        return i;
    }

    public static int multiplyColors(int a, int b) {
        int i = (a & 0xFF0000) >> 16;
        int j = (b & 0xFF0000) >> 16;
        int k = (a & 0xFF00) >> 8;
        int l = (b & 0xFF00) >> 8;
        int m = (a & 0xFF) >> 0;
        int n = (b & 0xFF) >> 0;
        int o = (int)((float)i * (float)j / 255.0f);
        int p = (int)((float)k * (float)l / 255.0f);
        int q = (int)((float)m * (float)n / 255.0f);
        return a & 0xFF000000 | o << 16 | p << 8 | q;
    }

    public static int multiplyColors(int color, float r, float g, float b) {
        int i = (color & 0xFF0000) >> 16;
        int j = (color & 0xFF00) >> 8;
        int k = (color & 0xFF) >> 0;
        int l = (int)((float)i * r);
        int m = (int)((float)j * g);
        int n = (int)((float)k * b);
        return color & 0xFF000000 | l << 16 | m << 8 | n;
    }

    public static float fractionalPart(float value) {
        return value - (float)MathHelper.floor(value);
    }

    public static double fractionalPart(double value) {
        return value - (double)MathHelper.lfloor(value);
    }

    public static Vec3d method_34946(Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3, Vec3d vec3d4, double d) {
        double e = ((-d + 2.0) * d - 1.0) * d * 0.5;
        double f = ((3.0 * d - 5.0) * d * d + 2.0) * 0.5;
        double g = ((-3.0 * d + 4.0) * d + 1.0) * d * 0.5;
        double h = (d - 1.0) * d * d * 0.5;
        return new Vec3d(vec3d.x * e + vec3d2.x * f + vec3d3.x * g + vec3d4.x * h, vec3d.y * e + vec3d2.y * f + vec3d3.y * g + vec3d4.y * h, vec3d.z * e + vec3d2.z * f + vec3d3.z * g + vec3d4.z * h);
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

    public static UUID randomUuid() {
        return MathHelper.randomUuid(RANDOM);
    }

    /**
     * Gets the fraction of the way that {@code value} is between {@code start} and {@code end}.
     * This is the delta value needed to lerp between {@code start} and {@code end} to get {@code value}.
     * In other words, {@code getLerpProgress(lerp(delta, start, end), start, end) == delta}.
     * 
     * @param value the result of the lerp function
     * @param end the value interpolated to
     * @param start the value interpolated from
     */
    public static double getLerpProgress(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public static float getLerpProgress(float value, float start, float end) {
        return (value - start) / (end - start);
    }

    public static boolean method_34945(Vec3d vec3d, Vec3d vec3d2, Box box) {
        double d = (box.minX + box.maxX) * 0.5;
        double e = (box.maxX - box.minX) * 0.5;
        double f = vec3d.x - d;
        if (Math.abs(f) > e && f * vec3d2.x >= 0.0) {
            return false;
        }
        double g = (box.minY + box.maxY) * 0.5;
        double h = (box.maxY - box.minY) * 0.5;
        double i = vec3d.y - g;
        if (Math.abs(i) > h && i * vec3d2.y >= 0.0) {
            return false;
        }
        double j = (box.minZ + box.maxZ) * 0.5;
        double k = (box.maxZ - box.minZ) * 0.5;
        double l = vec3d.z - j;
        if (Math.abs(l) > k && l * vec3d2.z >= 0.0) {
            return false;
        }
        double m = Math.abs(vec3d2.x);
        double n = Math.abs(vec3d2.y);
        double o = Math.abs(vec3d2.z);
        double p = vec3d2.y * l - vec3d2.z * i;
        if (Math.abs(p) > h * o + k * n) {
            return false;
        }
        p = vec3d2.z * f - vec3d2.x * l;
        if (Math.abs(p) > e * o + k * m) {
            return false;
        }
        p = vec3d2.x * i - vec3d2.y * f;
        return Math.abs(p) < e * n + h * m;
    }

    public static double atan2(double y, double x) {
        double e;
        boolean bl3;
        boolean bl2;
        boolean bl;
        double d = x * x + y * y;
        if (Double.isNaN(d)) {
            return Double.NaN;
        }
        boolean bl4 = bl = y < 0.0;
        if (bl) {
            y = -y;
        }
        boolean bl5 = bl2 = x < 0.0;
        if (bl2) {
            x = -x;
        }
        boolean bl6 = bl3 = y > x;
        if (bl3) {
            e = x;
            x = y;
            y = e;
        }
        e = MathHelper.fastInverseSqrt(d);
        x *= e;
        double f = SMALLEST_FRACTION_FREE_DOUBLE + (y *= e);
        int i = (int)Double.doubleToRawLongBits(f);
        double g = ARCSINE_TABLE[i];
        double h = COSINE_TABLE[i];
        double j = f - SMALLEST_FRACTION_FREE_DOUBLE;
        double k = y * h - x * j;
        double l = (6.0 + k * k) * k * 0.16666666666666666;
        double m = g + l;
        if (bl3) {
            m = 1.5707963267948966 - m;
        }
        if (bl2) {
            m = Math.PI - m;
        }
        if (bl) {
            m = -m;
        }
        return m;
    }

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

    public static float fastInverseCbrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        f = 0.6666667f * f + 1.0f / (3.0f * f * f * x);
        return f;
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        float l;
        float k;
        int i = (int)(hue * 6.0f) % 6;
        float f = hue * 6.0f - (float)i;
        float g = value * (1.0f - saturation);
        float h = value * (1.0f - f * saturation);
        float j = value * (1.0f - (1.0f - f) * saturation);
        float m = switch (i) {
            case 0 -> {
                k = value;
                l = j;
                yield g;
            }
            case 1 -> {
                k = h;
                l = value;
                yield g;
            }
            case 2 -> {
                k = g;
                l = value;
                yield j;
            }
            case 3 -> {
                k = g;
                l = h;
                yield value;
            }
            case 4 -> {
                k = j;
                l = g;
                yield value;
            }
            case 5 -> {
                k = value;
                l = g;
                yield h;
            }
            default -> throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        };
        int n = MathHelper.clamp((int)(k * 255.0f), 0, 255);
        int o = MathHelper.clamp((int)(l * 255.0f), 0, 255);
        int p = MathHelper.clamp((int)(m * 255.0f), 0, 255);
        return n << 16 | o << 8 | p;
    }

    public static int idealHash(int value) {
        value ^= value >>> 16;
        value *= -2048144789;
        value ^= value >>> 13;
        value *= -1028477387;
        value ^= value >>> 16;
        return value;
    }

    public static long murmurHash(long value) {
        value ^= value >>> 33;
        value *= -49064778989728563L;
        value ^= value >>> 33;
        value *= -4265267296055464877L;
        value ^= value >>> 33;
        return value;
    }

    public static double[] getCumulativeDistribution(double ... values) {
        double d = 0.0;
        for (double e : values) {
            d += e;
        }
        int i = 0;
        while (i < values.length) {
            int n = i++;
            values[n] = values[n] / d;
        }
        for (i = 0; i < values.length; ++i) {
            values[i] = (i == 0 ? 0.0 : values[i - 1]) + values[i];
        }
        return values;
    }

    public static int method_34950(Random random, double[] ds) {
        double d = random.nextDouble();
        for (int i = 0; i < ds.length; ++i) {
            if (!(d < ds[i])) continue;
            return i;
        }
        return ds.length;
    }

    public static double[] method_34941(double d, double e, double f, int i, int j) {
        double[] ds = new double[j - i + 1];
        int k = 0;
        for (int l = i; l <= j; ++l) {
            ds[k] = Math.max(0.0, d * StrictMath.exp(-((double)l - f) * ((double)l - f) / (2.0 * e * e)));
            ++k;
        }
        return ds;
    }

    public static double[] method_34940(double d, double e, double f, double g, double h, double i, int j, int k) {
        double[] ds = new double[k - j + 1];
        int l = 0;
        for (int m = j; m <= k; ++m) {
            ds[l] = Math.max(0.0, d * StrictMath.exp(-((double)m - f) * ((double)m - f) / (2.0 * e * e)) + g * StrictMath.exp(-((double)m - i) * ((double)m - i) / (2.0 * h * h)));
            ++l;
        }
        return ds;
    }

    public static double[] method_34942(double d, double e, int i, int j) {
        double[] ds = new double[j - i + 1];
        int k = 0;
        for (int l = i; l <= j; ++l) {
            ds[k] = Math.max(d * StrictMath.log(l) + e, 0.0);
            ++k;
        }
        return ds;
    }

    /**
     * Finds the minimum value in {@code [min, max)} that satisfies the
     * monotonic {@code predicate}.
     * 
     * <p>The {@code predicate} must be monotonic, i.e. if for any {@code a},
     * {@code predicate.test(a)} is {@code true}, then for all {@code b > a},
     * {@code predicate.test(b)} must also be {@code true}.
     * 
     * <p>Examples:
     * <ul>
     *   <li>{@code binarySearch(3, 7, x -> true)} returns {@code 3}.
     *   <li>{@code binarySearch(3, 7, x -> x >= 5)} returns {@code 5}.
     *   <li>{@code binarySearch(3, 7, x -> false)} returns {@code 7}.
     * </ul>
     * 
     * @return the minimum value if such value is found, otherwise {@code max}
     * 
     * @param max the maximum value (exclusive) to be tested
     * @param min the minimum value (inclusive) to be tested
     * @param predicate the predicate that returns {@code true} for integers greater than or
     * equal to the value to be searched for
     */
    public static int binarySearch(int min, int max, IntPredicate predicate) {
        int i = max - min;
        while (i > 0) {
            int j = i / 2;
            int k = min + j;
            if (predicate.test(k)) {
                i = j;
                continue;
            }
            min = k + 1;
            i -= j + 1;
        }
        return min;
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    /**
     * A two-dimensional lerp between values on the 4 corners of the unit square. Arbitrary values are specified for the corners and the output is interpolated between them.
     * 
     * @param x1y1 the output if {@code deltaX} is 1 and {@code deltaY} is 1
     * @param deltaX the x-coordinate on the unit square
     * @param deltaY the y-coordinate on the unit square
     * @param x0y0 the output if {@code deltaX} is 0 and {@code deltaY} is 0
     * @param x1y0 the output if {@code deltaX} is 1 and {@code deltaY} is 0
     * @param x0y1 the output if {@code deltaX} is 0 and {@code deltaY} is 1
     */
    public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return MathHelper.lerp(deltaY, MathHelper.lerp(deltaX, x0y0, x1y0), MathHelper.lerp(deltaX, x0y1, x1y1));
    }

    /**
     * A three-dimensional lerp between values on the 8 corners of the unit cube. Arbitrary values are specified for the corners and the output is interpolated between them.
     * 
     * @param x1y1z0 the output if {@code deltaX} is 1, {@code deltaY} is 1 and {@code deltaZ} is 0
     * @param x0y1z0 the output if {@code deltaX} is 0, {@code deltaY} is 1 and {@code deltaZ} is 0
     * @param x1y0z0 the output if {@code deltaX} is 1, {@code deltaY} is 0 and {@code deltaZ} is 0
     * @param x0y0z0 the output if {@code deltaX} is 0, {@code deltaY} is 0 and {@code deltaZ} is 0
     * @param deltaZ the z-coordinate on the unit cube
     * @param x1y1z1 the output if {@code deltaX} is 1, {@code deltaY} is 1 and {@code deltaZ} is 1
     * @param deltaY the y-coordinate on the unit cube
     * @param x0y1z1 the output if {@code deltaX} is 0, {@code deltaY} is 1 and {@code deltaZ} is 1
     * @param deltaX the x-coordinate on the unit cube
     * @param x1y0z1 the output if {@code deltaX} is 1, {@code deltaY} is 0 and {@code deltaZ} is 1
     * @param x0y0z1 the output if {@code deltaX} is 0, {@code deltaY} is 0 and {@code deltaZ} is 1
     */
    public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return MathHelper.lerp(deltaZ, MathHelper.lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), MathHelper.lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    public static float method_41303(float f, float g, float h, float i, float j) {
        return 0.5f * (2.0f * h + (i - g) * f + (2.0f * g - 5.0f * h + 4.0f * i - j) * f * f + (3.0f * h - g - 3.0f * i + j) * f * f * f);
    }

    public static double perlinFade(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

    public static double perlinFadeDerivative(double value) {
        return 30.0 * value * value * (value - 1.0) * (value - 1.0);
    }

    public static int sign(double value) {
        if (value == 0.0) {
            return 0;
        }
        return value > 0.0 ? 1 : -1;
    }

    public static float lerpAngleDegrees(float delta, float start, float end) {
        return start + delta * MathHelper.wrapDegrees(end - start);
    }

    public static float method_34955(float f, float g, float h) {
        return Math.min(f * f * 0.6f + g * g * ((3.0f + g) / 4.0f) + h * h * 0.8f, 1.0f);
    }

    @Deprecated
    public static float lerpAngle(float start, float end, float delta) {
        float f;
        for (f = end - start; f < -180.0f; f += 360.0f) {
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return start + delta * f;
    }

    @Deprecated
    public static float fwrapDegrees(double degrees) {
        while (degrees >= 180.0) {
            degrees -= 360.0;
        }
        while (degrees < -180.0) {
            degrees += 360.0;
        }
        return (float)degrees;
    }

    public static float wrap(float value, float maxDeviation) {
        return (Math.abs(value % maxDeviation - maxDeviation * 0.5f) - maxDeviation * 0.25f) / (maxDeviation * 0.25f);
    }

    public static float square(float n) {
        return n * n;
    }

    public static double square(double n) {
        return n * n;
    }

    public static int square(int n) {
        return n * n;
    }

    public static long square(long n) {
        return n * n;
    }

    public static double clampedLerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
        return MathHelper.clampedLerp(start, end, MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd));
    }

    public static float clampedLerpFromProgress(float lerpValue, float lerpStart, float lerpEnd, float start, float end) {
        return MathHelper.clampedLerp(start, end, MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd));
    }

    public static double lerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
        return MathHelper.lerp(MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
    }

    public static float lerpFromProgress(float lerpValue, float lerpStart, float lerpEnd, float start, float end) {
        return MathHelper.lerp(MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
    }

    public static double method_34957(double d) {
        return d + (2.0 * new Random(MathHelper.floor(d * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
    }

    /**
     * Returns a value farther than or as far as {@code value} from zero that
     * is a multiple of {@code divisor}.
     */
    public static int roundUpToMultiple(int value, int divisor) {
        return MathHelper.ceilDiv(value, divisor) * divisor;
    }

    public static int ceilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    /**
     * {@return a random, uniformly distributed integer value in {@code
     * [min, max]}}
     * 
     * @throws IllegalArgumentException if the range is empty (i.e. {@code
     * max < min})
     * 
     * @param min the minimum value, inclusive
     * @param max the maximum value, inclusive
     */
    public static int nextBetween(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static float nextGaussian(Random random, float mean, float deviation) {
        return mean + (float)random.nextGaussian() * deviation;
    }

    public static double squaredHypot(double a, double b) {
        return a * a + b * b;
    }

    public static double hypot(double a, double b) {
        return Math.sqrt(MathHelper.squaredHypot(a, b));
    }

    public static double squaredMagnitude(double a, double b, double c) {
        return a * a + b * b + c * c;
    }

    public static double magnitude(double a, double b, double c) {
        return Math.sqrt(MathHelper.squaredMagnitude(a, b, c));
    }

    /**
     * {@return {@code a} rounded down to the nearest multiple of {@code b}}
     */
    public static int roundDownToMultiple(double a, int b) {
        return MathHelper.floor(a / (double)b) * b;
    }

    public static IntStream stream(int seed, int lowerBound, int upperBound) {
        return MathHelper.stream(seed, lowerBound, upperBound, 1);
    }

    public static IntStream stream(int seed, int lowerBound, int upperBound, int steps) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("upperbound %d expected to be > lowerBound %d".formatted(upperBound, lowerBound));
        }
        if (steps < 1) {
            throw new IllegalArgumentException("steps expected to be >= 1, was %d".formatted(steps));
        }
        if (seed < lowerBound || seed > upperBound) {
            return IntStream.empty();
        }
        return IntStream.iterate(seed, i -> {
            int m = Math.abs(seed - i);
            return seed - m >= lowerBound || seed + m <= upperBound;
        }, i -> {
            int o;
            boolean bl2;
            boolean bl = i <= seed;
            int n = Math.abs(seed - i);
            boolean bl3 = bl2 = seed + n + steps <= upperBound;
            if (!(bl && bl2 || (o = seed - n - (bl ? steps : 0)) < lowerBound)) {
                return o;
            }
            return seed + n + steps;
        });
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

