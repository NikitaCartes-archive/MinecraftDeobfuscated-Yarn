package net.minecraft;

import net.minecraft.util.math.MathHelper;

public final class class_6466 {
	public static final float field_34228 = 0.015F;
	static class_6468<class_6466.class_6467> field_34229;
	static class_6468<class_6466.class_6467> field_34230;

	public class_6466() {
		method_37730();
	}

	public static void method_37730() {
		class_6462<class_6466.class_6467> lv = method_37736("beachSpline", -0.15F, -0.05F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F);
		class_6462<class_6466.class_6467> lv2 = method_37736("lowSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F);
		class_6462<class_6466.class_6467> lv3 = method_37736("midSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F);
		class_6462<class_6466.class_6467> lv4 = method_37736("highSpline", -0.1F, 0.3F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F);
		float f = -0.51F;
		float g = -0.4F;
		float h = 0.1F;
		float i = -0.15F;
		field_34229 = class_6462.method_37721(class_6466.class_6467::method_37745)
			.method_37728("offsetSampler")
			.method_37725(-1.1F, 0.044F, 0.0F)
			.method_37725(-1.005F, -0.2222F, 0.0F)
			.method_37725(-0.51F, -0.2222F, 0.0F)
			.method_37725(-0.44F, -0.12F, 0.0F)
			.method_37725(-0.18F, -0.12F, 0.0F)
			.method_37726(-0.16F, lv, 0.0F)
			.method_37726(-0.15F, lv, 0.0F)
			.method_37726(-0.1F, lv2, 0.0F)
			.method_37726(0.25F, lv3, 0.0F)
			.method_37726(1.0F, lv4, 0.0F)
			.method_37724()
			.method_37719();
		field_34230 = class_6462.method_37721(class_6466.class_6467::method_37745)
			.method_37728("Factor-Continents")
			.method_37725(-0.19F, 505.0F, 0.0F)
			.method_37726(-0.15F, method_37737("erosionCoast", 783.0F, true, "ridgeCoast-OldMountains"), 0.0F)
			.method_37726(-0.1F, method_37737("erosionInland", 600.0F, true, "ridgeInlande-OldMountains"), 0.0F)
			.method_37726(0.03F, method_37737("erosionMidInland", 600.0F, true, "ridgeInlande-OldMountains"), 0.0F)
			.method_37726(0.06F, method_37737("erosionFarInland", 600.0F, false, "ridgeInlande-OldMountains"), 0.0F)
			.method_37724()
			.method_37719();
	}

	private static class_6462<class_6466.class_6467> method_37737(String string, float f, boolean bl, String string2) {
		class_6462.class_6463<class_6466.class_6467> lv = class_6462.method_37721(class_6466.class_6467::method_37746)
			.method_37728(string)
			.method_37725(-1.0F, f, 0.0F)
			.method_37725(-0.5F, 342.0F, 0.0F)
			.method_37725(0.05F, f, 0.0F);
		class_6462<class_6466.class_6467> lv2 = class_6462.method_37721(class_6466.class_6467::method_37747)
			.method_37728(string2)
			.method_37725(0.45F, f, 0.0F)
			.method_37725(0.6F, 175.0F, 0.0F)
			.method_37724();
		if (bl) {
			class_6462<class_6466.class_6467> lv3 = class_6462.method_37721(class_6466.class_6467::method_37747)
				.method_37728("ridgesShattered")
				.method_37725(-0.72F, f, 0.0F)
				.method_37725(-0.69F, 80.0F, 0.0F)
				.method_37724();
			lv.method_37726(0.051F, lv2, 0.0F)
				.method_37726(0.45F, lv2, 0.0F)
				.method_37726(0.51F, lv3, 0.0F)
				.method_37726(0.59F, lv3, 0.0F)
				.method_37726(0.65F, lv2, 0.0F);
		} else {
			lv.method_37726(0.051F, lv2, 0.0F);
		}

		return lv.method_37724();
	}

	private class_6462<class_6466.class_6467> method_37739() {
		class_6462.class_6463<class_6466.class_6467> lv = class_6462.method_37721(class_6466.class_6467::method_37745);
		float f = 0.1F;

		for (float g = -1.0F; g < 1.0F + f; g += f) {
			if (g < 0.0F) {
				lv.method_37725(g, 0.0F, 0.0F);
			} else {
				lv.method_37726(g, method_37743(g), 0.0F);
			}
		}

		return lv.method_37728("").method_37724();
	}

	private static float method_37733(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static class_6462<class_6466.class_6467> method_37740(float f) {
		class_6462.class_6463<class_6466.class_6467> lv = class_6462.method_37721(class_6466.class_6467::method_37747)
			.method_37728(String.format("M-spline for continentalness: %.02f", f));
		float g = -0.7F;
		float h = -1.0F;
		float i = method_37741(-1.0F, f, -0.7F);
		float j = 1.0F;
		float k = method_37741(1.0F, f, -0.7F);
		float l = method_37744(f);
		float m = -0.65F;
		if (-0.65F < l && l < 1.0F) {
			float n = method_37741(-0.65F, f, -0.7F);
			float o = -0.75F;
			float p = method_37741(-0.75F, f, -0.7F);
			float q = method_37733(i, p, -1.0F, -0.75F);
			lv.method_37725(-1.0F, i, q);
			lv.method_37725(-0.75F, p, 0.0F);
			lv.method_37725(-0.65F, n, 0.0F);
			float r = method_37741(l, f, -0.7F);
			float s = method_37733(r, k, l, 1.0F);
			float t = 0.01F;
			lv.method_37725(l - 0.01F, r, 0.0F);
			lv.method_37725(l, r, s);
			lv.method_37725(1.0F, k, s);
		} else {
			float n = method_37733(i, k, -1.0F, 1.0F);
			lv.method_37725(-1.0F, i, n);
			lv.method_37725(1.0F, k, n);
		}

		return lv.method_37724();
	}

	private static class_6462<class_6466.class_6467> method_37743(float f) {
		class_6462.class_6463<class_6466.class_6467> lv = class_6462.method_37721(class_6466.class_6467::method_37747)
			.method_37728(String.format("M-spline for continentalness: %.02f", f));
		float g = 0.1F;
		float h = 0.7F;

		for (float i = -1.0F; i < 1.1F; i += 0.1F) {
			lv.method_37725(i, method_37741(i, f, 0.7F), 0.0F);
		}

		return lv.method_37724();
	}

	private static float method_37741(float f, float g, float h) {
		float i = 1.17F;
		float j = 0.46082947F;
		float k = 1.0F - (1.0F - g) * 0.5F;
		float l = 0.5F * (1.0F - g);
		float m = (f + 1.17F) * 0.46082947F;
		float n = m * k - l;
		return f < h ? Math.max(n, -0.2222F) : Math.max(n, 0.0F);
	}

	private static float method_37744(float f) {
		float g = 1.17F;
		float h = 0.46082947F;
		float i = 1.0F - (1.0F - f) * 0.5F;
		float j = 0.5F * (1.0F - f);
		return j / (0.46082947F * i) - 1.17F;
	}

	public static class_6462<class_6466.class_6467> method_37736(String string, float f, float g, float h, float i, float j, float k, float l) {
		float m = 0.6F;
		float n = 1.5F;
		float o = 0.5F;
		float p = 0.5F;
		float q = 1.2F;
		float r = MathHelper.lerp(j, 0.6F, 1.0F);
		class_6462<class_6466.class_6467> lv = method_37740(r);
		class_6462<class_6466.class_6467> lv2 = method_37740(j);
		class_6462<class_6466.class_6467> lv3 = method_37735(
			string + "-widePlateau", MathHelper.lerp(0.5F, f, g), 0.5F * j, MathHelper.lerp(0.5F, 0.5F, 0.5F) * j, 0.5F * j, 0.6F * j
		);
		class_6462<class_6466.class_6467> lv4 = method_37735(string + "-narrowPlateau", f, k * j, h * j, 0.5F * j, 0.6F * j);
		class_6462<class_6466.class_6467> lv5 = method_37735(string + "-plains", f, k, k, h, i);
		class_6462<class_6466.class_6467> lv6 = method_37735(string + "-swamps", f, l, l, h, i);
		return class_6462.method_37721(class_6466.class_6467::method_37746)
			.method_37728(string)
			.method_37726(-0.9F, lv, 0.0F)
			.method_37726(-0.4F, lv2, 0.0F)
			.method_37726(-0.35F, lv3, 0.0F)
			.method_37726(-0.1F, lv4, 0.0F)
			.method_37726(0.2F, lv5, 0.0F)
			.method_37726(1.0F, lv6, 0.0F)
			.method_37724();
	}

	private static class_6462<class_6466.class_6467> method_37735(String string, float f, float g, float h, float i, float j) {
		float k = Math.max(0.5F * (g - f), 0.7F);
		float l = 5.0F * (h - g);
		return class_6462.method_37721(class_6466.class_6467::method_37747)
			.method_37728(string)
			.method_37725(-1.0F, f, k)
			.method_37725(-0.4F, g, Math.min(k, l))
			.method_37725(0.0F, h, l)
			.method_37725(0.4F, i, 2.0F * (i - h))
			.method_37725(1.0F, j, 0.7F * (j - i))
			.method_37724();
	}

	public float method_37734(class_6466.class_6467 arg) {
		return field_34229.apply(arg) + 0.015F;
	}

	public float method_37742(class_6466.class_6467 arg) {
		return field_34230.apply(arg);
	}

	public class_6466.class_6467 method_37732(float f, float g, float h) {
		return new class_6466.class_6467(f, g, method_37731(h));
	}

	public static float method_37731(float f) {
		return -(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	public static void method_37738(String[] strings) {
		method_37730();
		System.out.println(field_34229.toString());
	}

	public static final class class_6467 {
		private final float field_34231;
		private final float field_34232;
		private final float field_34233;

		public class_6467(float f, float g, float h) {
			this.field_34231 = f;
			this.field_34232 = g;
			this.field_34233 = h;
		}

		public float method_37745() {
			return this.field_34231;
		}

		public float method_37746() {
			return this.field_34232;
		}

		public float method_37747() {
			return this.field_34233;
		}
	}
}
