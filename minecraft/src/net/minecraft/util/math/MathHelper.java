package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
	public static final float SQUARE_ROOT_OF_TWO = sqrt(2.0F);
	private static final float[] sinTable = SystemUtil.consume(new float[65536], fs -> {
		for (int ix = 0; ix < fs.length; ix++) {
			fs[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
		}
	});
	private static final Random RANDOM = new Random();
	private static final int[] MULTIPLY_DE_BRUJIN_BIT_POSITION = new int[]{
		0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
	};
	private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
	private static final double[] asinTable = new double[257];
	private static final double[] cosTable = new double[257];

	public static float sin(float f) {
		return sinTable[(int)(f * 10430.378F) & 65535];
	}

	public static float cos(float f) {
		return sinTable[(int)(f * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float f) {
		return (float)Math.sqrt((double)f);
	}

	public static float sqrt(double d) {
		return (float)Math.sqrt(d);
	}

	public static int floor(float f) {
		int i = (int)f;
		return f < (float)i ? i - 1 : i;
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public static int absFloor(double d) {
		return (int)(d >= 0.0 ? d : -d + 1.0);
	}

	public static float abs(float f) {
		return f >= 0.0F ? f : -f;
	}

	public static int abs(int i) {
		return i >= 0 ? i : -i;
	}

	public static int ceil(float f) {
		int i = (int)f;
		return f > (float)i ? i + 1 : i;
	}

	public static int ceil(double d) {
		int i = (int)d;
		return d > (double)i ? i + 1 : i;
	}

	public static int clamp(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	public static float clamp(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	public static double clamp(double d, double e, double f) {
		if (d < e) {
			return e;
		} else {
			return d > f ? f : d;
		}
	}

	public static double lerpClamped(double d, double e, double f) {
		if (f < 0.0) {
			return d;
		} else {
			return f > 1.0 ? e : lerp(f, d, e);
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

	public static int nextInt(Random random, int i, int j) {
		return i >= j ? i : random.nextInt(j - i + 1) + i;
	}

	public static float nextFloat(Random random, float f, float g) {
		return f >= g ? f : random.nextFloat() * (g - f) + f;
	}

	public static double nextDouble(Random random, double d, double e) {
		return d >= e ? d : random.nextDouble() * (e - d) + d;
	}

	public static double average(long[] ls) {
		long l = 0L;

		for (long m : ls) {
			l += m;
		}

		return (double)l / (double)ls.length;
	}

	@Environment(EnvType.CLIENT)
	public static boolean equalsApproximate(float f, float g) {
		return abs(g - f) < 1.0E-5F;
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

	public static float wrapDegrees(float f) {
		f %= 360.0F;
		if (f >= 180.0F) {
			f -= 360.0F;
		}

		if (f < -180.0F) {
			f += 360.0F;
		}

		return f;
	}

	public static double wrapDegrees(double d) {
		d %= 360.0;
		if (d >= 180.0) {
			d -= 360.0;
		}

		if (d < -180.0) {
			d += 360.0;
		}

		return d;
	}

	@Environment(EnvType.CLIENT)
	public static int wrapDegrees(int i) {
		i %= 360;
		if (i >= 180) {
			i -= 360;
		}

		if (i < -180) {
			i += 360;
		}

		return i;
	}

	public static float method_15381(float f, float g) {
		float h = wrapDegrees(f - g);
		return h < 180.0F ? h : h - 360.0F;
	}

	public static float method_15356(float f, float g) {
		float h = wrapDegrees(f - g);
		return h < 180.0F ? abs(h) : abs(h - 360.0F);
	}

	public static float method_15348(float f, float g, float h) {
		h = abs(h);
		return f < g ? clamp(f + h, f, g) : clamp(f - h, g, f);
	}

	public static float method_15388(float f, float g, float h) {
		float i = method_15381(g, f);
		return method_15348(f, f + i, h);
	}

	@Environment(EnvType.CLIENT)
	public static int parseInt(String string, int i) {
		return NumberUtils.toInt(string, i);
	}

	@Environment(EnvType.CLIENT)
	public static int parseInt(String string, int i, int j) {
		return Math.max(j, parseInt(string, i));
	}

	@Environment(EnvType.CLIENT)
	public static double parseDouble(String string, double d) {
		try {
			return Double.parseDouble(string);
		} catch (Throwable var4) {
			return d;
		}
	}

	@Environment(EnvType.CLIENT)
	public static double parseDouble(String string, double d, double e) {
		return Math.max(e, parseDouble(string, d));
	}

	public static int smallestEncompassingPowerOfTwo(int i) {
		int j = i - 1;
		j |= j >> 1;
		j |= j >> 2;
		j |= j >> 4;
		j |= j >> 8;
		j |= j >> 16;
		return j + 1;
	}

	private static boolean isPowerOfTwo(int i) {
		return i != 0 && (i & i - 1) == 0;
	}

	public static int log2DeBrujin(int i) {
		i = isPowerOfTwo(i) ? i : smallestEncompassingPowerOfTwo(i);
		return MULTIPLY_DE_BRUJIN_BIT_POSITION[(int)((long)i * 125613361L >> 27) & 31];
	}

	public static int log2(int i) {
		return log2DeBrujin(i) - (isPowerOfTwo(i) ? 0 : 1);
	}

	public static int roundUp(int i, int j) {
		if (j == 0) {
			return 0;
		} else if (i == 0) {
			return j;
		} else {
			if (i < 0) {
				j *= -1;
			}

			int k = i % j;
			return k == 0 ? i : i + j - k;
		}
	}

	@Environment(EnvType.CLIENT)
	public static int packRgb(float f, float g, float h) {
		return packRgb(floor(f * 255.0F), floor(g * 255.0F), floor(h * 255.0F));
	}

	@Environment(EnvType.CLIENT)
	public static int packRgb(int i, int j, int k) {
		int l = (i << 8) + j;
		return (l << 8) + k;
	}

	@Environment(EnvType.CLIENT)
	public static int multiplyColors(int i, int j) {
		int k = (i & 0xFF0000) >> 16;
		int l = (j & 0xFF0000) >> 16;
		int m = (i & 0xFF00) >> 8;
		int n = (j & 0xFF00) >> 8;
		int o = (i & 0xFF) >> 0;
		int p = (j & 0xFF) >> 0;
		int q = (int)((float)k * (float)l / 255.0F);
		int r = (int)((float)m * (float)n / 255.0F);
		int s = (int)((float)o * (float)p / 255.0F);
		return i & 0xFF000000 | q << 16 | r << 8 | s;
	}

	public static double fractionalPart(double d) {
		return d - (double)lfloor(d);
	}

	public static long hashCode(Vec3i vec3i) {
		return hashCode(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public static long hashCode(int i, int j, int k) {
		long l = (long)(i * 3129871) ^ (long)k * 116129781L ^ (long)j;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

	public static UUID randomUuid(Random random) {
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	public static UUID randomUUID() {
		return randomUuid(RANDOM);
	}

	public static double method_15370(double d, double e, double f) {
		return (d - e) / (f - e);
	}

	public static double atan2(double d, double e) {
		double f = e * e + d * d;
		if (Double.isNaN(f)) {
			return Double.NaN;
		} else {
			boolean bl = d < 0.0;
			if (bl) {
				d = -d;
			}

			boolean bl2 = e < 0.0;
			if (bl2) {
				e = -e;
			}

			boolean bl3 = d > e;
			if (bl3) {
				double g = e;
				e = d;
				d = g;
			}

			double g = fastInverseSqrt(f);
			e *= g;
			d *= g;
			double h = SMALLEST_FRACTION_FREE_DOUBLE + d;
			int i = (int)Double.doubleToRawLongBits(h);
			double j = asinTable[i];
			double k = cosTable[i];
			double l = h - SMALLEST_FRACTION_FREE_DOUBLE;
			double m = d * k - e * l;
			double n = (6.0 + m * m) * m * 0.16666666666666666;
			double o = j + n;
			if (bl3) {
				o = (Math.PI / 2) - o;
			}

			if (bl2) {
				o = Math.PI - o;
			}

			if (bl) {
				o = -o;
			}

			return o;
		}
	}

	public static double fastInverseSqrt(double d) {
		double e = 0.5 * d;
		long l = Double.doubleToRawLongBits(d);
		l = 6910469410427058090L - (l >> 1);
		d = Double.longBitsToDouble(l);
		return d * (1.5 - e * d * d);
	}

	@Environment(EnvType.CLIENT)
	public static int hsvToRgb(float f, float g, float h) {
		int i = (int)(f * 6.0F) % 6;
		float j = f * 6.0F - (float)i;
		float k = h * (1.0F - g);
		float l = h * (1.0F - j * g);
		float m = h * (1.0F - (1.0F - j) * g);
		float n;
		float o;
		float p;
		switch (i) {
			case 0:
				n = h;
				o = m;
				p = k;
				break;
			case 1:
				n = l;
				o = h;
				p = k;
				break;
			case 2:
				n = k;
				o = h;
				p = m;
				break;
			case 3:
				n = k;
				o = l;
				p = h;
				break;
			case 4:
				n = m;
				o = k;
				p = h;
				break;
			case 5:
				n = h;
				o = k;
				p = l;
				break;
			default:
				throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + g + ", " + h);
		}

		int q = clamp((int)(n * 255.0F), 0, 255);
		int r = clamp((int)(o * 255.0F), 0, 255);
		int s = clamp((int)(p * 255.0F), 0, 255);
		return q << 16 | r << 8 | s;
	}

	public static int method_15354(int i) {
		i ^= i >>> 16;
		i *= -2048144789;
		i ^= i >>> 13;
		i *= -1028477387;
		return i ^ i >>> 16;
	}

	public static int method_15360(int i, int j, IntPredicate intPredicate) {
		int k = j - i;

		while (k > 0) {
			int l = k / 2;
			int m = i + l;
			if (intPredicate.test(m)) {
				k = l;
			} else {
				i = m + 1;
				k -= l + 1;
			}
		}

		return i;
	}

	public static float lerp(float f, float g, float h) {
		return g + f * (h - g);
	}

	public static double lerp(double d, double e, double f) {
		return e + d * (f - e);
	}

	public static double lerp2(double d, double e, double f, double g, double h, double i) {
		return lerp(e, lerp(d, f, g), lerp(d, h, i));
	}

	public static double lerp3(double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n) {
		return lerp(f, lerp2(d, e, g, h, i, j), lerp2(d, e, k, l, m, n));
	}

	public static double ease(double d) {
		return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
	}

	public static int method_17822(double d) {
		if (d == 0.0) {
			return 0;
		} else {
			return d > 0.0 ? 1 : -1;
		}
	}

	@Environment(EnvType.CLIENT)
	public static float method_17821(float f, float g, float h) {
		return g + f * wrapDegrees(h - g);
	}

	static {
		for (int i = 0; i < 257; i++) {
			double d = (double)i / 256.0;
			double e = Math.asin(d);
			cosTable[i] = Math.cos(e);
			asinTable[i] = e;
		}
	}
}
