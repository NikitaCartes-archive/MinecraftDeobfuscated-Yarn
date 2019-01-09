package net.minecraft;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.math.NumberUtils;

public class class_3532 {
	public static final float field_15724 = method_15355(2.0F);
	private static final float[] field_15725 = class_156.method_654(new float[65536], fs -> {
		for (int ix = 0; ix < fs.length; ix++) {
			fs[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
		}
	});
	private static final Random field_15726 = new Random();
	private static final int[] field_15723 = new int[]{
		0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
	};
	private static final double field_15728 = Double.longBitsToDouble(4805340802404319232L);
	private static final double[] field_15727 = new double[257];
	private static final double[] field_15722 = new double[257];

	public static float method_15374(float f) {
		return field_15725[(int)(f * 10430.378F) & 65535];
	}

	public static float method_15362(float f) {
		return field_15725[(int)(f * 10430.378F + 16384.0F) & 65535];
	}

	public static float method_15355(float f) {
		return (float)Math.sqrt((double)f);
	}

	public static float method_15368(double d) {
		return (float)Math.sqrt(d);
	}

	public static int method_15375(float f) {
		int i = (int)f;
		return f < (float)i ? i - 1 : i;
	}

	@Environment(EnvType.CLIENT)
	public static int method_15365(double d) {
		return (int)(d + 1024.0) - 1024;
	}

	public static int method_15357(double d) {
		int i = (int)d;
		return d < (double)i ? i - 1 : i;
	}

	public static long method_15372(double d) {
		long l = (long)d;
		return d < (double)l ? l - 1L : l;
	}

	@Environment(EnvType.CLIENT)
	public static int method_15380(double d) {
		return (int)(d >= 0.0 ? d : -d + 1.0);
	}

	public static float method_15379(float f) {
		return f >= 0.0F ? f : -f;
	}

	public static int method_15382(int i) {
		return i >= 0 ? i : -i;
	}

	public static int method_15386(float f) {
		int i = (int)f;
		return f > (float)i ? i + 1 : i;
	}

	public static int method_15384(double d) {
		int i = (int)d;
		return d > (double)i ? i + 1 : i;
	}

	public static int method_15340(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	public static float method_15363(float f, float g, float h) {
		if (f < g) {
			return g;
		} else {
			return f > h ? h : f;
		}
	}

	public static double method_15350(double d, double e, double f) {
		if (d < e) {
			return e;
		} else {
			return d > f ? f : d;
		}
	}

	public static double method_15390(double d, double e, double f) {
		if (f < 0.0) {
			return d;
		} else {
			return f > 1.0 ? e : method_16436(f, d, e);
		}
	}

	public static double method_15391(double d, double e) {
		if (d < 0.0) {
			d = -d;
		}

		if (e < 0.0) {
			e = -e;
		}

		return d > e ? d : e;
	}

	public static int method_15346(int i, int j) {
		return Math.floorDiv(i, j);
	}

	public static int method_15395(Random random, int i, int j) {
		return i >= j ? i : random.nextInt(j - i + 1) + i;
	}

	public static float method_15344(Random random, float f, float g) {
		return f >= g ? f : random.nextFloat() * (g - f) + f;
	}

	public static double method_15366(Random random, double d, double e) {
		return d >= e ? d : random.nextDouble() * (e - d) + d;
	}

	public static double method_15373(long[] ls) {
		long l = 0L;

		for (long m : ls) {
			l += m;
		}

		return (double)l / (double)ls.length;
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_15347(float f, float g) {
		return method_15379(g - f) < 1.0E-5F;
	}

	public static int method_15387(int i, int j) {
		return Math.floorMod(i, j);
	}

	@Environment(EnvType.CLIENT)
	public static float method_15341(float f, float g) {
		return (f % g + g) % g;
	}

	@Environment(EnvType.CLIENT)
	public static double method_15367(double d, double e) {
		return (d % e + e) % e;
	}

	public static float method_15393(float f) {
		f %= 360.0F;
		if (f >= 180.0F) {
			f -= 360.0F;
		}

		if (f < -180.0F) {
			f += 360.0F;
		}

		return f;
	}

	public static double method_15338(double d) {
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
	public static int method_15392(int i) {
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
		float h = method_15393(f - g);
		return h < 180.0F ? h : h - 360.0F;
	}

	public static float method_15356(float f, float g) {
		float h = method_15393(f - g);
		return h < 180.0F ? method_15379(h) : method_15379(h - 360.0F);
	}

	public static float method_15348(float f, float g, float h) {
		h = method_15379(h);
		return f < g ? method_15363(f + h, f, g) : method_15363(f - h, g, f);
	}

	public static float method_15388(float f, float g, float h) {
		float i = method_15381(g, f);
		return method_15348(f, f + i, h);
	}

	@Environment(EnvType.CLIENT)
	public static int method_15343(String string, int i) {
		return NumberUtils.toInt(string, i);
	}

	@Environment(EnvType.CLIENT)
	public static int method_15364(String string, int i, int j) {
		return Math.max(j, method_15343(string, i));
	}

	@Environment(EnvType.CLIENT)
	public static double method_15361(String string, double d) {
		try {
			return Double.parseDouble(string);
		} catch (Throwable var4) {
			return d;
		}
	}

	@Environment(EnvType.CLIENT)
	public static double method_15358(String string, double d, double e) {
		return Math.max(e, method_15361(string, d));
	}

	public static int method_15339(int i) {
		int j = i - 1;
		j |= j >> 1;
		j |= j >> 2;
		j |= j >> 4;
		j |= j >> 8;
		j |= j >> 16;
		return j + 1;
	}

	private static boolean method_15352(int i) {
		return i != 0 && (i & i - 1) == 0;
	}

	public static int method_15342(int i) {
		i = method_15352(i) ? i : method_15339(i);
		return field_15723[(int)((long)i * 125613361L >> 27) & 31];
	}

	public static int method_15351(int i) {
		return method_15342(i) - (method_15352(i) ? 0 : 1);
	}

	public static int method_15359(int i, int j) {
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
	public static int method_15353(float f, float g, float h) {
		return method_15383(method_15375(f * 255.0F), method_15375(g * 255.0F), method_15375(h * 255.0F));
	}

	@Environment(EnvType.CLIENT)
	public static int method_15383(int i, int j, int k) {
		int l = (i << 8) + j;
		return (l << 8) + k;
	}

	@Environment(EnvType.CLIENT)
	public static int method_15377(int i, int j) {
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

	@Environment(EnvType.CLIENT)
	public static double method_15385(double d) {
		return d - Math.floor(d);
	}

	public static long method_15389(class_2382 arg) {
		return method_15371(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public static long method_15371(int i, int j, int k) {
		long l = (long)(i * 3129871) ^ (long)k * 116129781L ^ (long)j;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

	public static UUID method_15378(Random random) {
		long l = random.nextLong() & -61441L | 16384L;
		long m = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	public static UUID method_15394() {
		return method_15378(field_15726);
	}

	public static double method_15370(double d, double e, double f) {
		return (d - e) / (f - e);
	}

	public static double method_15349(double d, double e) {
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

			double g = method_15345(f);
			e *= g;
			d *= g;
			double h = field_15728 + d;
			int i = (int)Double.doubleToRawLongBits(h);
			double j = field_15727[i];
			double k = field_15722[i];
			double l = h - field_15728;
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

	public static double method_15345(double d) {
		double e = 0.5 * d;
		long l = Double.doubleToRawLongBits(d);
		l = 6910469410427058090L - (l >> 1);
		d = Double.longBitsToDouble(l);
		return d * (1.5 - e * d * d);
	}

	@Environment(EnvType.CLIENT)
	public static int method_15369(float f, float g, float h) {
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

		int q = method_15340((int)(n * 255.0F), 0, 255);
		int r = method_15340((int)(o * 255.0F), 0, 255);
		int s = method_15340((int)(p * 255.0F), 0, 255);
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

	public static float method_16439(float f, float g, float h) {
		return g + f * (h - g);
	}

	public static double method_16436(double d, double e, double f) {
		return e + d * (f - e);
	}

	public static double method_16437(double d, double e, double f, double g, double h, double i) {
		return method_16436(e, method_16436(d, f, g), method_16436(d, h, i));
	}

	public static double method_16438(double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n) {
		return method_16436(f, method_16437(d, e, g, h, i, j), method_16437(d, e, k, l, m, n));
	}

	public static double method_16435(double d) {
		return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
	}

	static {
		for (int i = 0; i < 257; i++) {
			double d = (double)i / 256.0;
			double e = Math.asin(d);
			field_15722[i] = Math.cos(e);
			field_15727[i] = e;
		}
	}
}
