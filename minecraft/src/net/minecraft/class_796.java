package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_796 {
	private static final float field_4260 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float field_4259 = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;
	private static final class_796.class_797[] field_4264 = new class_796.class_797[class_1086.values().length * class_2350.values().length];
	private static final class_796.class_797 field_4258 = new class_796.class_797() {
		@Override
		class_787 method_3470(float f, float g, float h, float i) {
			return new class_787(new float[]{f, g, h, i}, 0);
		}
	};
	private static final class_796.class_797 field_4261 = new class_796.class_797() {
		@Override
		class_787 method_3470(float f, float g, float h, float i) {
			return new class_787(new float[]{i, 16.0F - f, g, 16.0F - h}, 270);
		}
	};
	private static final class_796.class_797 field_4262 = new class_796.class_797() {
		@Override
		class_787 method_3470(float f, float g, float h, float i) {
			return new class_787(new float[]{16.0F - f, 16.0F - g, 16.0F - h, 16.0F - i}, 0);
		}
	};
	private static final class_796.class_797 field_4263 = new class_796.class_797() {
		@Override
		class_787 method_3470(float f, float g, float h, float i) {
			return new class_787(new float[]{16.0F - g, h, 16.0F - i, f}, 90);
		}
	};

	public class_777 method_3468(
		class_1160 arg, class_1160 arg2, class_783 arg3, class_1058 arg4, class_2350 arg5, class_3665 arg6, @Nullable class_789 arg7, boolean bl
	) {
		class_787 lv = arg3.field_4227;
		if (arg6.method_3512()) {
			lv = this.method_3454(arg3.field_4227, arg5, arg6.method_3509());
		}

		float[] fs = new float[lv.field_4235.length];
		System.arraycopy(lv.field_4235, 0, fs, 0, fs.length);
		float f = (float)arg4.method_4578() / (arg4.method_4577() - arg4.method_4594());
		float g = (float)arg4.method_4595() / (arg4.method_4575() - arg4.method_4593());
		float h = 4.0F / Math.max(g, f);
		float i = (lv.field_4235[0] + lv.field_4235[0] + lv.field_4235[2] + lv.field_4235[2]) / 4.0F;
		float j = (lv.field_4235[1] + lv.field_4235[1] + lv.field_4235[3] + lv.field_4235[3]) / 4.0F;
		lv.field_4235[0] = class_3532.method_16439(h, lv.field_4235[0], i);
		lv.field_4235[2] = class_3532.method_16439(h, lv.field_4235[2], i);
		lv.field_4235[1] = class_3532.method_16439(h, lv.field_4235[1], j);
		lv.field_4235[3] = class_3532.method_16439(h, lv.field_4235[3], j);
		int[] is = this.method_3458(lv, arg4, arg5, this.method_3459(arg, arg2), arg6.method_3509(), arg7, bl);
		class_2350 lv2 = method_3467(is);
		System.arraycopy(fs, 0, lv.field_4235, 0, fs.length);
		if (arg7 == null) {
			this.method_3462(is, lv2);
		}

		return new class_777(is, arg3.field_4226, lv2, arg4);
	}

	private class_787 method_3454(class_787 arg, class_2350 arg2, class_1086 arg3) {
		return field_4264[method_3465(arg3, arg2)].method_3469(arg);
	}

	private int[] method_3458(class_787 arg, class_1058 arg2, class_2350 arg3, float[] fs, class_1086 arg4, @Nullable class_789 arg5, boolean bl) {
		int[] is = new int[28];

		for (int i = 0; i < 4; i++) {
			this.method_3461(is, i, arg3, arg, fs, arg2, arg4, arg5, bl);
		}

		return is;
	}

	private int method_3457(class_2350 arg) {
		float f = this.method_3456(arg);
		int i = class_3532.method_15340((int)(f * 255.0F), 0, 255);
		return 0xFF000000 | i << 16 | i << 8 | i;
	}

	private float method_3456(class_2350 arg) {
		switch (arg) {
			case field_11033:
				return 0.5F;
			case field_11036:
				return 1.0F;
			case field_11043:
			case field_11035:
				return 0.8F;
			case field_11039:
			case field_11034:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	private float[] method_3459(class_1160 arg, class_1160 arg2) {
		float[] fs = new float[class_2350.values().length];
		fs[class_753.class_754.field_3967] = arg.method_4943() / 16.0F;
		fs[class_753.class_754.field_3968] = arg.method_4945() / 16.0F;
		fs[class_753.class_754.field_3969] = arg.method_4947() / 16.0F;
		fs[class_753.class_754.field_3970] = arg2.method_4943() / 16.0F;
		fs[class_753.class_754.field_3971] = arg2.method_4945() / 16.0F;
		fs[class_753.class_754.field_3972] = arg2.method_4947() / 16.0F;
		return fs;
	}

	private void method_3461(int[] is, int i, class_2350 arg, class_787 arg2, float[] fs, class_1058 arg3, class_1086 arg4, @Nullable class_789 arg5, boolean bl) {
		class_2350 lv = arg4.method_4705(arg);
		int j = bl ? this.method_3457(lv) : -1;
		class_753.class_755 lv2 = class_753.method_3163(arg).method_3162(i);
		class_1160 lv3 = new class_1160(fs[lv2.field_3975], fs[lv2.field_3974], fs[lv2.field_3973]);
		this.method_3463(lv3, arg5);
		int k = this.method_3455(lv3, arg, i, arg4);
		this.method_3460(is, k, i, lv3, j, arg3, arg2);
	}

	private void method_3460(int[] is, int i, int j, class_1160 arg, int k, class_1058 arg2, class_787 arg3) {
		int l = i * 7;
		is[l] = Float.floatToRawIntBits(arg.method_4943());
		is[l + 1] = Float.floatToRawIntBits(arg.method_4945());
		is[l + 2] = Float.floatToRawIntBits(arg.method_4947());
		is[l + 3] = k;
		is[l + 4] = Float.floatToRawIntBits(arg2.method_4580((double)arg3.method_3415(j)));
		is[l + 4 + 1] = Float.floatToRawIntBits(arg2.method_4570((double)arg3.method_3416(j)));
	}

	private void method_3463(class_1160 arg, @Nullable class_789 arg2) {
		if (arg2 != null) {
			class_1160 lv;
			class_1160 lv2;
			switch (arg2.field_4239) {
				case field_11048:
					lv = new class_1160(1.0F, 0.0F, 0.0F);
					lv2 = new class_1160(0.0F, 1.0F, 1.0F);
					break;
				case field_11052:
					lv = new class_1160(0.0F, 1.0F, 0.0F);
					lv2 = new class_1160(1.0F, 0.0F, 1.0F);
					break;
				case field_11051:
					lv = new class_1160(0.0F, 0.0F, 1.0F);
					lv2 = new class_1160(1.0F, 1.0F, 0.0F);
					break;
				default:
					throw new IllegalArgumentException("There are only 3 axes");
			}

			class_1158 lv3 = new class_1158(lv, arg2.field_4237, true);
			if (arg2.field_4238) {
				if (Math.abs(arg2.field_4237) == 22.5F) {
					lv2.method_4942(field_4260);
				} else {
					lv2.method_4942(field_4259);
				}

				lv2.method_4948(1.0F, 1.0F, 1.0F);
			} else {
				lv2.method_4949(1.0F, 1.0F, 1.0F);
			}

			this.method_3464(arg, new class_1160(arg2.field_4236), lv3, lv2);
		}
	}

	public int method_3455(class_1160 arg, class_2350 arg2, int i, class_1086 arg3) {
		if (arg3 == class_1086.field_5350) {
			return i;
		} else {
			this.method_3464(arg, new class_1160(0.5F, 0.5F, 0.5F), arg3.method_4704(), new class_1160(1.0F, 1.0F, 1.0F));
			return arg3.method_4706(arg2, i);
		}
	}

	private void method_3464(class_1160 arg, class_1160 arg2, class_1158 arg3, class_1160 arg4) {
		class_1162 lv = new class_1162(arg.method_4943() - arg2.method_4943(), arg.method_4945() - arg2.method_4945(), arg.method_4947() - arg2.method_4947(), 1.0F);
		lv.method_4959(arg3);
		lv.method_4954(arg4);
		arg.method_4949(lv.method_4953() + arg2.method_4943(), lv.method_4956() + arg2.method_4945(), lv.method_4957() + arg2.method_4947());
	}

	public static class_2350 method_3467(int[] is) {
		class_1160 lv = new class_1160(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
		class_1160 lv2 = new class_1160(Float.intBitsToFloat(is[7]), Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]));
		class_1160 lv3 = new class_1160(Float.intBitsToFloat(is[14]), Float.intBitsToFloat(is[15]), Float.intBitsToFloat(is[16]));
		class_1160 lv4 = new class_1160(lv);
		lv4.method_4944(lv2);
		class_1160 lv5 = new class_1160(lv3);
		lv5.method_4944(lv2);
		class_1160 lv6 = new class_1160(lv5);
		lv6.method_4951(lv4);
		lv6.method_4952();
		class_2350 lv7 = null;
		float f = 0.0F;

		for (class_2350 lv8 : class_2350.values()) {
			class_2382 lv9 = lv8.method_10163();
			class_1160 lv10 = new class_1160((float)lv9.method_10263(), (float)lv9.method_10264(), (float)lv9.method_10260());
			float g = lv6.method_4950(lv10);
			if (g >= 0.0F && g > f) {
				f = g;
				lv7 = lv8;
			}
		}

		return lv7 == null ? class_2350.field_11036 : lv7;
	}

	private void method_3462(int[] is, class_2350 arg) {
		int[] js = new int[is.length];
		System.arraycopy(is, 0, js, 0, is.length);
		float[] fs = new float[class_2350.values().length];
		fs[class_753.class_754.field_3967] = 999.0F;
		fs[class_753.class_754.field_3968] = 999.0F;
		fs[class_753.class_754.field_3969] = 999.0F;
		fs[class_753.class_754.field_3970] = -999.0F;
		fs[class_753.class_754.field_3971] = -999.0F;
		fs[class_753.class_754.field_3972] = -999.0F;

		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			float f = Float.intBitsToFloat(js[j]);
			float g = Float.intBitsToFloat(js[j + 1]);
			float h = Float.intBitsToFloat(js[j + 2]);
			if (f < fs[class_753.class_754.field_3967]) {
				fs[class_753.class_754.field_3967] = f;
			}

			if (g < fs[class_753.class_754.field_3968]) {
				fs[class_753.class_754.field_3968] = g;
			}

			if (h < fs[class_753.class_754.field_3969]) {
				fs[class_753.class_754.field_3969] = h;
			}

			if (f > fs[class_753.class_754.field_3970]) {
				fs[class_753.class_754.field_3970] = f;
			}

			if (g > fs[class_753.class_754.field_3971]) {
				fs[class_753.class_754.field_3971] = g;
			}

			if (h > fs[class_753.class_754.field_3972]) {
				fs[class_753.class_754.field_3972] = h;
			}
		}

		class_753 lv = class_753.method_3163(arg);

		for (int jx = 0; jx < 4; jx++) {
			int k = 7 * jx;
			class_753.class_755 lv2 = lv.method_3162(jx);
			float hx = fs[lv2.field_3975];
			float l = fs[lv2.field_3974];
			float m = fs[lv2.field_3973];
			is[k] = Float.floatToRawIntBits(hx);
			is[k + 1] = Float.floatToRawIntBits(l);
			is[k + 2] = Float.floatToRawIntBits(m);

			for (int n = 0; n < 4; n++) {
				int o = 7 * n;
				float p = Float.intBitsToFloat(js[o]);
				float q = Float.intBitsToFloat(js[o + 1]);
				float r = Float.intBitsToFloat(js[o + 2]);
				if (class_3532.method_15347(hx, p) && class_3532.method_15347(l, q) && class_3532.method_15347(m, r)) {
					is[k + 4] = js[o + 4];
					is[k + 4 + 1] = js[o + 4 + 1];
				}
			}
		}
	}

	private static void method_3466(class_1086 arg, class_2350 arg2, class_796.class_797 arg3) {
		field_4264[method_3465(arg, arg2)] = arg3;
	}

	private static int method_3465(class_1086 arg, class_2350 arg2) {
		return class_1086.values().length * arg2.ordinal() + arg.ordinal();
	}

	static {
		method_3466(class_1086.field_5350, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5350, class_2350.field_11034, field_4258);
		method_3466(class_1086.field_5350, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5350, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5350, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5350, class_2350.field_11039, field_4258);
		method_3466(class_1086.field_5366, class_2350.field_11034, field_4258);
		method_3466(class_1086.field_5366, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5366, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5366, class_2350.field_11039, field_4258);
		method_3466(class_1086.field_5355, class_2350.field_11034, field_4258);
		method_3466(class_1086.field_5355, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5355, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5355, class_2350.field_11039, field_4258);
		method_3466(class_1086.field_5347, class_2350.field_11034, field_4258);
		method_3466(class_1086.field_5347, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5347, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5347, class_2350.field_11039, field_4258);
		method_3466(class_1086.field_5351, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5351, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5360, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5367, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5367, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5354, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5358, class_2350.field_11033, field_4258);
		method_3466(class_1086.field_5358, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5353, class_2350.field_11035, field_4258);
		method_3466(class_1086.field_5353, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5349, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5361, class_2350.field_11043, field_4258);
		method_3466(class_1086.field_5361, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5352, class_2350.field_11036, field_4258);
		method_3466(class_1086.field_5347, class_2350.field_11036, field_4261);
		method_3466(class_1086.field_5366, class_2350.field_11033, field_4261);
		method_3466(class_1086.field_5351, class_2350.field_11039, field_4261);
		method_3466(class_1086.field_5360, class_2350.field_11039, field_4261);
		method_3466(class_1086.field_5367, class_2350.field_11039, field_4261);
		method_3466(class_1086.field_5354, class_2350.field_11043, field_4261);
		method_3466(class_1086.field_5354, class_2350.field_11035, field_4261);
		method_3466(class_1086.field_5354, class_2350.field_11039, field_4261);
		method_3466(class_1086.field_5348, class_2350.field_11036, field_4261);
		method_3466(class_1086.field_5359, class_2350.field_11033, field_4261);
		method_3466(class_1086.field_5353, class_2350.field_11034, field_4261);
		method_3466(class_1086.field_5349, class_2350.field_11034, field_4261);
		method_3466(class_1086.field_5349, class_2350.field_11043, field_4261);
		method_3466(class_1086.field_5349, class_2350.field_11035, field_4261);
		method_3466(class_1086.field_5361, class_2350.field_11034, field_4261);
		method_3466(class_1086.field_5352, class_2350.field_11034, field_4261);
		method_3466(class_1086.field_5355, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5355, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5351, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5351, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5360, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5367, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5367, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5354, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5358, class_2350.field_11034, field_4262);
		method_3466(class_1086.field_5358, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5358, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5358, class_2350.field_11039, field_4262);
		method_3466(class_1086.field_5348, class_2350.field_11034, field_4262);
		method_3466(class_1086.field_5348, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5348, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5348, class_2350.field_11039, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11034, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11036, field_4262);
		method_3466(class_1086.field_5356, class_2350.field_11039, field_4262);
		method_3466(class_1086.field_5359, class_2350.field_11034, field_4262);
		method_3466(class_1086.field_5359, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5359, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5359, class_2350.field_11039, field_4262);
		method_3466(class_1086.field_5353, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5353, class_2350.field_11043, field_4262);
		method_3466(class_1086.field_5349, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5361, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5361, class_2350.field_11035, field_4262);
		method_3466(class_1086.field_5352, class_2350.field_11033, field_4262);
		method_3466(class_1086.field_5366, class_2350.field_11036, field_4263);
		method_3466(class_1086.field_5347, class_2350.field_11033, field_4263);
		method_3466(class_1086.field_5351, class_2350.field_11034, field_4263);
		method_3466(class_1086.field_5360, class_2350.field_11034, field_4263);
		method_3466(class_1086.field_5360, class_2350.field_11043, field_4263);
		method_3466(class_1086.field_5360, class_2350.field_11035, field_4263);
		method_3466(class_1086.field_5367, class_2350.field_11034, field_4263);
		method_3466(class_1086.field_5354, class_2350.field_11034, field_4263);
		method_3466(class_1086.field_5353, class_2350.field_11039, field_4263);
		method_3466(class_1086.field_5348, class_2350.field_11033, field_4263);
		method_3466(class_1086.field_5359, class_2350.field_11036, field_4263);
		method_3466(class_1086.field_5349, class_2350.field_11039, field_4263);
		method_3466(class_1086.field_5361, class_2350.field_11039, field_4263);
		method_3466(class_1086.field_5352, class_2350.field_11043, field_4263);
		method_3466(class_1086.field_5352, class_2350.field_11035, field_4263);
		method_3466(class_1086.field_5352, class_2350.field_11039, field_4263);
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_797 {
		private class_797() {
		}

		public class_787 method_3469(class_787 arg) {
			float f = arg.method_3415(arg.method_3414(0));
			float g = arg.method_3416(arg.method_3414(0));
			float h = arg.method_3415(arg.method_3414(2));
			float i = arg.method_3416(arg.method_3414(2));
			return this.method_3470(f, g, h, i);
		}

		abstract class_787 method_3470(float f, float g, float h, float i);
	}
}
