package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
	public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
	private static final float[] SINE_TABLE = Util.make(new float[65536], fs -> {
		for (int ix = 0; ix < fs.length; ix++) {
			fs[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
		}
	});
	private static final Random RANDOM = new Random();
	private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
		0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
	};
	private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
	private static final double[] ARCSINE_TABLE = new double[257];
	private static final double[] COSINE_TABLE = new double[257];

	public static float sin(float value) {
		return SINE_TABLE[(int)(value * 10430.378F) & 65535];
	}

	public static float cos(float value) {
		return SINE_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float value) {
		return (float)Math.sqrt((double)value);
	}

	public static float sqrt(double value) {
		return (float)Math.sqrt(value);
	}

	public static int floor(float value) {
		int i = (int)value;
		return value < (float)i ? i - 1 : i;
	}

	@Environment(EnvType.CLIENT)
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

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	@Environment(EnvType.CLIENT)
	public static long clamp(long value, long min, long max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public static double clampedLerp(double start, double end, double delta) {
		if (delta < 0.0) {
			return start;
		} else {
			return delta > 1.0 ? end : lerp(delta, start, end);
		}
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
		return min >= max ? min : random.nextInt(max - min + 1) + min;
	}

	public static float nextFloat(Random random, float min, float max) {
		return min >= max ? min : random.nextFloat() * (max - min) + min;
	}

	public static double nextDouble(Random random, double min, double max) {
		return min >= max ? min : random.nextDouble() * (max - min) + min;
	}

	public static double average(long[] array) {
		long l = 0L;

		for (long m : array) {
			l += m;
		}

		return (double)l / (double)array.length;
	}

	@Environment(EnvType.CLIENT)
	public static boolean approximatelyEquals(float a, float b) {
		return Math.abs(b - a) < 1.0E-5F;
	}

	public static boolean approximatelyEquals(double a, double b) {
		return Math.abs(b - a) < 1.0E-5F;
	}

	public static int floorMod(int i, int j) {
		return Math.floorMod(i, j);
	}

	@Environment(EnvType.CLIENT)
	public static float floorMod(float f, float g) {
		return (f % g + g) % g;
	}

	@Environment(EnvType.CLIENT)
	public static double floorMod(double d, double e) {
		return (d % e + e) % e;
	}

	@Environment(EnvType.CLIENT)
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
		float g = f % 360.0F;
		if (g >= 180.0F) {
			g -= 360.0F;
		}

		if (g < -180.0F) {
			g += 360.0F;
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
		return wrapDegrees(end - start);
	}

	public static float angleBetween(float first, float second) {
		return abs(subtractAngles(first, second));
	}

	/**
	 * Steps from {@code from} degrees towards {@code to} degrees, changing the value by at most {@code step} degrees.
	 */
	public static float stepAngleTowards(float from, float to, float step) {
		float f = subtractAngles(from, to);
		float g = clamp(f, -step, step);
		return to - g;
	}

	/**
	 * Steps from {@code from} towards {@code to}, changing the value by at most {@code step}.
	 */
	public static float stepTowards(float from, float to, float step) {
		step = abs(step);
		return from < to ? clamp(from + step, from, to) : clamp(from - step, to, from);
	}

	/**
	 * Steps from {@code from} degrees towards {@code to} degrees, changing the value by at most {@code step} degrees.
	 * 
	 * <p>This method does not wrap the resulting angle, so {@link #stepAngleTowards(float, float, float)} should be used in preference.</p>
	 */
	public static float stepUnwrappedAngleTowards(float from, float to, float step) {
		float f = subtractAngles(from, to);
		return stepTowards(from, from + f, step);
	}

	@Environment(EnvType.CLIENT)
	public static int parseInt(String string, int fallback) {
		return NumberUtils.toInt(string, fallback);
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

	public static int log2DeBruijn(int value) {
		value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
		return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 31];
	}

	public static int log2(int value) {
		return log2DeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
	}

	public static int packRgb(float r, float g, float b) {
		return packRgb(floor(r * 255.0F), floor(g * 255.0F), floor(b * 255.0F));
	}

	public static int packRgb(int r, int g, int b) {
		int i = (r << 8) + g;
		return (i << 8) + b;
	}

	public static float fractionalPart(float value) {
		return value - (float)floor(value);
	}

	public static double fractionalPart(double value) {
		return value - (double)lfloor(value);
	}

	public static long hashCode(Vec3i vec) {
		return hashCode(vec.getX(), vec.getY(), vec.getZ());
	}

	public static long hashCode(int x, int y, int z) {
		long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

	public static UUID randomUuid(Random random) {
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	public static UUID randomUuid() {
		return randomUuid(RANDOM);
	}

	/**
	 * Gets the fraction of the way that {@code value} is between {@code start} and {@code end}.
	 * This is the delta value needed to lerp between {@code start} and {@code end} to get {@code value}.
	 * In other words, {@code getLerpProgress(lerp(delta, start, end), start, end) == delta}.
	 * 
	 * @param value The result of the lerp function
	 * @param start The value interpolated from
	 * @param end The value interpolated to
	 */
	public static double getLerpProgress(double value, double start, double end) {
		return (value - start) / (end - start);
	}

	public static double atan2(double y, double x) {
		double d = x * x + y * y;
		if (Double.isNaN(d)) {
			return Double.NaN;
		} else {
			boolean bl = y < 0.0;
			if (bl) {
				y = -y;
			}

			boolean bl2 = x < 0.0;
			if (bl2) {
				x = -x;
			}

			boolean bl3 = y > x;
			if (bl3) {
				double e = x;
				x = y;
				y = e;
			}

			double e = fastInverseSqrt(d);
			x *= e;
			y *= e;
			double f = SMALLEST_FRACTION_FREE_DOUBLE + y;
			int i = (int)Double.doubleToRawLongBits(f);
			double g = ARCSINE_TABLE[i];
			double h = COSINE_TABLE[i];
			double j = f - SMALLEST_FRACTION_FREE_DOUBLE;
			double k = y * h - x * j;
			double l = (6.0 + k * k) * k * 0.16666666666666666;
			double m = g + l;
			if (bl3) {
				m = (Math.PI / 2) - m;
			}

			if (bl2) {
				m = Math.PI - m;
			}

			if (bl) {
				m = -m;
			}

			return m;
		}
	}

	@Environment(EnvType.CLIENT)
	public static float fastInverseSqrt(float x) {
		float f = 0.5F * x;
		int i = Float.floatToIntBits(x);
		i = 1597463007 - (i >> 1);
		x = Float.intBitsToFloat(i);
		return x * (1.5F - f * x * x);
	}

	public static double fastInverseSqrt(double x) {
		double d = 0.5 * x;
		long l = Double.doubleToRawLongBits(x);
		l = 6910469410427058090L - (l >> 1);
		x = Double.longBitsToDouble(l);
		return x * (1.5 - d * x * x);
	}

	@Environment(EnvType.CLIENT)
	public static float fastInverseCbrt(float x) {
		int i = Float.floatToIntBits(x);
		i = 1419967116 - i / 3;
		float f = Float.intBitsToFloat(i);
		f = 0.6666667F * f + 1.0F / (3.0F * f * f * x);
		return 0.6666667F * f + 1.0F / (3.0F * f * f * x);
	}

	public static int hsvToRgb(float hue, float saturation, float value) {
		int i = (int)(hue * 6.0F) % 6;
		float f = hue * 6.0F - (float)i;
		float g = value * (1.0F - saturation);
		float h = value * (1.0F - f * saturation);
		float j = value * (1.0F - (1.0F - f) * saturation);
		float k;
		float l;
		float m;
		switch (i) {
			case 0:
				k = value;
				l = j;
				m = g;
				break;
			case 1:
				k = h;
				l = value;
				m = g;
				break;
			case 2:
				k = g;
				l = value;
				m = j;
				break;
			case 3:
				k = g;
				l = h;
				m = value;
				break;
			case 4:
				k = j;
				l = g;
				m = value;
				break;
			case 5:
				k = value;
				l = g;
				m = h;
				break;
			default:
				throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
		}

		int n = clamp((int)(k * 255.0F), 0, 255);
		int o = clamp((int)(l * 255.0F), 0, 255);
		int p = clamp((int)(m * 255.0F), 0, 255);
		return n << 16 | o << 8 | p;
	}

	public static int idealHash(int i) {
		i ^= i >>> 16;
		i *= -2048144789;
		i ^= i >>> 13;
		i *= -1028477387;
		return i ^ i >>> 16;
	}

	public static int binarySearch(int start, int end, IntPredicate leftPredicate) {
		int i = end - start;

		while (i > 0) {
			int j = i / 2;
			int k = start + j;
			if (leftPredicate.test(k)) {
				i = j;
			} else {
				start = k + 1;
				i -= j + 1;
			}
		}

		return start;
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
	 * @param deltaX The x-coordinate on the unit square
	 * @param deltaY The y-coordinate on the unit square
	 * @param val00 The output if {@code deltaX} is 0 and {@code deltaY} is 0
	 * @param val10 The output if {@code deltaX} is 1 and {@code deltaY} is 0
	 * @param val01 The output if {@code deltaX} is 0 and {@code deltaY} is 1
	 * @param val11 The output if {@code deltaX} is 1 and {@code deltaY} is 1
	 */
	public static double lerp2(double deltaX, double deltaY, double val00, double val10, double val01, double val11) {
		return lerp(deltaY, lerp(deltaX, val00, val10), lerp(deltaX, val01, val11));
	}

	/**
	 * A three-dimensional lerp between values on the 8 corners of the unit cube. Arbitrary values are specified for the corners and the output is interpolated between them.
	 * 
	 * @param deltaX The x-coordinate on the unit cube
	 * @param deltaY The y-coordinate on the unit cube
	 * @param deltaZ The z-coordinate on the unit cube
	 * @param val000 The output if {@code deltaX} is 0, {@code deltaY} is 0 and {@code deltaZ} is 0
	 * @param val100 The output if {@code deltaX} is 1, {@code deltaY} is 0 and {@code deltaZ} is 0
	 * @param val010 The output if {@code deltaX} is 0, {@code deltaY} is 1 and {@code deltaZ} is 0
	 * @param val110 The output if {@code deltaX} is 1, {@code deltaY} is 1 and {@code deltaZ} is 0
	 * @param val001 The output if {@code deltaX} is 0, {@code deltaY} is 0 and {@code deltaZ} is 1
	 * @param val101 The output if {@code deltaX} is 1, {@code deltaY} is 0 and {@code deltaZ} is 1
	 * @param val011 The output if {@code deltaX} is 0, {@code deltaY} is 1 and {@code deltaZ} is 1
	 * @param val111 The output if {@code deltaX} is 1, {@code deltaY} is 1 and {@code deltaZ} is 1
	 */
	public static double lerp3(
		double deltaX,
		double deltaY,
		double deltaZ,
		double val000,
		double val100,
		double val010,
		double val110,
		double val001,
		double val101,
		double val011,
		double val111
	) {
		return lerp(deltaZ, lerp2(deltaX, deltaY, val000, val100, val010, val110), lerp2(deltaX, deltaY, val001, val101, val011, val111));
	}

	public static double perlinFade(double value) {
		return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
	}

	public static int sign(double value) {
		if (value == 0.0) {
			return 0;
		} else {
			return value > 0.0 ? 1 : -1;
		}
	}

	@Environment(EnvType.CLIENT)
	public static float lerpAngleDegrees(float delta, float start, float end) {
		return start + delta * wrapDegrees(end - start);
	}

	@Deprecated
	public static float lerpAngle(float start, float end, float delta) {
		float f = end - start;

		while (f < -180.0F) {
			f += 360.0F;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return start + delta * f;
	}

	@Deprecated
	@Environment(EnvType.CLIENT)
	public static float fwrapDegrees(double degrees) {
		while (degrees >= 180.0) {
			degrees -= 360.0;
		}

		while (degrees < -180.0) {
			degrees += 360.0;
		}

		return (float)degrees;
	}

	@Environment(EnvType.CLIENT)
	public static float wrap(float value, float maxDeviation) {
		return (Math.abs(value % maxDeviation - maxDeviation * 0.5F) - maxDeviation * 0.25F) / (maxDeviation * 0.25F);
	}

	public static float square(float n) {
		return n * n;
	}

	public static double square(double n) {
		return n * n;
	}

	public static double clampedLerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
		return clampedLerp(start, end, getLerpProgress(lerpValue, lerpStart, lerpEnd));
	}

	public static double lerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
		return lerp(getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
	}

	/**
	 * Returns a value farther than or as far as {@code value} from zero that
	 * is a multiple of {@code divisor}.
	 */
	public static int roundUpToMultiple(int value, int divisor) {
		return (value + divisor - 1) / divisor * divisor;
	}

	public static int nextBetween(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public static float nextBetween(Random random, float min, float max) {
		return random.nextFloat() * (max - min) + min;
	}

	public static float nextGaussian(Random random, float mean, float deviation) {
		return mean + (float)random.nextGaussian() * deviation;
	}

	public static double magnitude(int x, double y, int z) {
		return (double)sqrt((double)(x * x) + y * y + (double)(z * z));
	}

	static {
		for (int i = 0; i < 257; i++) {
			double d = (double)i / 256.0;
			double e = Math.asin(d);
			COSINE_TABLE[i] = Math.cos(e);
			ARCSINE_TABLE[i] = e;
		}
	}
}
