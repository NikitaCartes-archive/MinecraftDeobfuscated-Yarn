package net.minecraft;

import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;

public final class class_6466 {
	public static final float field_34228 = 0.015F;
	static ToFloatFunction<class_6466.TerrainNoisePoint> offsetSpline;
	static ToFloatFunction<class_6466.TerrainNoisePoint> factorSpline;

	public class_6466() {
		init();
	}

	public static void init() {
		Spline<class_6466.TerrainNoisePoint> spline = method_37736("beachSpline", -0.15F, -0.05F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F);
		Spline<class_6466.TerrainNoisePoint> spline2 = method_37736("lowSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F);
		Spline<class_6466.TerrainNoisePoint> spline3 = method_37736("midSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F);
		Spline<class_6466.TerrainNoisePoint> spline4 = method_37736("highSpline", -0.1F, 0.3F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F);
		float f = -0.51F;
		float g = -0.4F;
		float h = 0.1F;
		float i = -0.15F;
		offsetSpline = Spline.builder(class_6466.TerrainNoisePoint::getContinentalnessNoise)
			.setName("offsetSampler")
			.add(-1.1F, 0.044F, 0.0F)
			.add(-1.005F, -0.2222F, 0.0F)
			.add(-0.51F, -0.2222F, 0.0F)
			.add(-0.44F, -0.12F, 0.0F)
			.add(-0.18F, -0.12F, 0.0F)
			.add(-0.16F, spline, 0.0F)
			.add(-0.15F, spline, 0.0F)
			.add(-0.1F, spline2, 0.0F)
			.add(0.25F, spline3, 0.0F)
			.add(1.0F, spline4, 0.0F)
			.build()
			.getThis();
		factorSpline = Spline.builder(class_6466.TerrainNoisePoint::getContinentalnessNoise)
			.setName("Factor-Continents")
			.add(-0.19F, 505.0F, 0.0F)
			.add(-0.15F, buildErosionFactorSpline("erosionCoast", 800.0F, true, "ridgeCoast-OldMountains"), 0.0F)
			.add(-0.1F, buildErosionFactorSpline("erosionInland", 700.0F, true, "ridgeInland-OldMountains"), 0.0F)
			.add(0.03F, buildErosionFactorSpline("erosionMidInland", 650.0F, true, "ridgeInland-OldMountains"), 0.0F)
			.add(0.06F, buildErosionFactorSpline("erosionFarInland", 600.0F, false, "ridgeInland-OldMountains"), 0.0F)
			.build()
			.getThis();
	}

	private static Spline<class_6466.TerrainNoisePoint> buildErosionFactorSpline(String erosionName, float value, boolean addShatteredRidges, String weirdnessName) {
		Spline.Builder<class_6466.TerrainNoisePoint> builder = Spline.builder(class_6466.TerrainNoisePoint::getErosionNoise)
			.setName(erosionName)
			.add(-0.6F, value, 0.0F)
			.add(-0.5F, 342.0F, 0.0F)
			.add(-0.35F, value, 0.0F)
			.add(-0.25F, value, 0.0F)
			.add(-0.1F, 342.0F, 0.0F)
			.add(0.05F, value, 0.0F);
		Spline<class_6466.TerrainNoisePoint> spline = Spline.builder(class_6466.TerrainNoisePoint::getWeirdnessNoise)
			.setName(weirdnessName)
			.add(0.45F, value, 0.0F)
			.add(0.6F, 175.0F, 0.0F)
			.build();
		if (addShatteredRidges) {
			Spline<class_6466.TerrainNoisePoint> spline2 = Spline.builder(class_6466.TerrainNoisePoint::getWeirdnessNoise)
				.setName("ridgesShattered")
				.add(-0.72F, value, 0.0F)
				.add(-0.69F, 80.0F, 0.0F)
				.build();
			builder.add(0.051F, spline, 0.0F).add(0.45F, spline, 0.0F).add(0.51F, spline2, 0.0F).add(0.59F, spline2, 0.0F).add(0.65F, spline, 0.0F);
		} else {
			builder.add(0.051F, spline, 0.0F);
		}

		return builder.build();
	}

	private Spline<class_6466.TerrainNoisePoint> method_37739() {
		Spline.Builder<class_6466.TerrainNoisePoint> builder = Spline.builder(class_6466.TerrainNoisePoint::getContinentalnessNoise);
		float f = 0.1F;

		for (float g = -1.0F; g < 1.0F + f; g += f) {
			if (g < 0.0F) {
				builder.add(g, 0.0F, 0.0F);
			} else {
				builder.add(g, method_37743(g), 0.0F);
			}
		}

		return builder.setName("").build();
	}

	private static float method_37733(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static Spline<class_6466.TerrainNoisePoint> method_37740(float f) {
		Spline.Builder<class_6466.TerrainNoisePoint> builder = Spline.builder(class_6466.TerrainNoisePoint::getWeirdnessNoise)
			.setName(String.format("M-spline for continentalness: %.02f", f));
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
			builder.add(-1.0F, i, q);
			builder.add(-0.75F, p, 0.0F);
			builder.add(-0.65F, n, 0.0F);
			float r = method_37741(l, f, -0.7F);
			float s = method_37733(r, k, l, 1.0F);
			float t = 0.01F;
			builder.add(l - 0.01F, r, 0.0F);
			builder.add(l, r, s);
			builder.add(1.0F, k, s);
		} else {
			float n = method_37733(i, k, -1.0F, 1.0F);
			builder.add(-1.0F, i, n);
			builder.add(1.0F, k, n);
		}

		return builder.build();
	}

	private static Spline<class_6466.TerrainNoisePoint> method_37743(float continentalness) {
		Spline.Builder<class_6466.TerrainNoisePoint> builder = Spline.builder(class_6466.TerrainNoisePoint::getWeirdnessNoise)
			.setName(String.format("M-spline for continentalness: %.02f", continentalness));
		float f = 0.1F;
		float g = 0.7F;

		for (float h = -1.0F; h < 1.1F; h += 0.1F) {
			builder.add(h, method_37741(h, continentalness, 0.7F), 0.0F);
		}

		return builder.build();
	}

	private static float method_37741(float f, float continentalness, float g) {
		float h = 1.17F;
		float i = 0.46082947F;
		float j = 1.0F - (1.0F - continentalness) * 0.5F;
		float k = 0.5F * (1.0F - continentalness);
		float l = (f + 1.17F) * 0.46082947F;
		float m = l * j - k;
		return f < g ? Math.max(m, -0.2222F) : Math.max(m, 0.0F);
	}

	private static float method_37744(float f) {
		float g = 1.17F;
		float h = 0.46082947F;
		float i = 1.0F - (1.0F - f) * 0.5F;
		float j = 0.5F * (1.0F - f);
		return j / (0.46082947F * i) - 1.17F;
	}

	public static Spline<class_6466.TerrainNoisePoint> method_37736(String name, float f, float g, float h, float i, float j, float k, float l) {
		float m = 0.6F;
		float n = 1.5F;
		float o = 0.5F;
		float p = 0.5F;
		float q = 1.2F;
		Spline<class_6466.TerrainNoisePoint> spline = method_37740(MathHelper.lerp(j, 0.6F, 2.0F));
		Spline<class_6466.TerrainNoisePoint> spline2 = method_37740(MathHelper.lerp(j, 0.6F, 1.0F));
		Spline<class_6466.TerrainNoisePoint> spline3 = method_37740(j);
		Spline<class_6466.TerrainNoisePoint> spline4 = method_37735(
			name + "-widePlateau", MathHelper.lerp(0.5F, f, g), 0.5F * j, MathHelper.lerp(0.5F, 0.5F, 0.5F) * j, 0.5F * j, 0.6F * j
		);
		Spline<class_6466.TerrainNoisePoint> spline5 = method_37735(name + "-narrowPlateau", f, k * j, h * j, 0.5F * j, 0.6F * j);
		Spline<class_6466.TerrainNoisePoint> spline6 = method_37735(name + "-plains", f, k, k, h, i);
		Spline<class_6466.TerrainNoisePoint> spline7 = method_37735(name + "-swamps", f, l, l, h, i);
		return Spline.builder(class_6466.TerrainNoisePoint::getErosionNoise)
			.setName(name)
			.add(-0.9F, spline, 0.0F)
			.add(-0.7F, spline2, 0.0F)
			.add(-0.4F, spline3, 0.0F)
			.add(-0.35F, spline4, 0.0F)
			.add(-0.1F, spline5, 0.0F)
			.add(0.2F, spline6, 0.0F)
			.add(1.0F, spline7, 0.0F)
			.build();
	}

	private static Spline<class_6466.TerrainNoisePoint> method_37735(String name, float f, float g, float h, float i, float j) {
		float k = Math.max(0.5F * (g - f), 0.7F);
		float l = 5.0F * (h - g);
		return Spline.builder(class_6466.TerrainNoisePoint::getWeirdnessNoise)
			.setName(name)
			.add(-1.0F, f, k)
			.add(-0.4F, g, Math.min(k, l))
			.add(0.0F, h, l)
			.add(0.4F, i, 2.0F * (i - h))
			.add(1.0F, j, 0.7F * (j - i))
			.build();
	}

	public float getOffset(class_6466.TerrainNoisePoint point) {
		return offsetSpline.apply(point) + 0.015F;
	}

	public float getFactor(class_6466.TerrainNoisePoint point) {
		return factorSpline.apply(point);
	}

	public class_6466.TerrainNoisePoint createTerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
		return new class_6466.TerrainNoisePoint(continentalnessNoise, erosionNoise, method_37731(weirdnessNoise));
	}

	public static float method_37731(float weirdness) {
		return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	public static void method_37738(String[] strings) {
		init();
		System.out.println(offsetSpline.toString());
	}

	public static final class TerrainNoisePoint {
		private final float continentalnessNoise;
		private final float erosionNoise;
		private final float weirdnessNoise;

		public TerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
			this.continentalnessNoise = continentalnessNoise;
			this.erosionNoise = erosionNoise;
			this.weirdnessNoise = weirdnessNoise;
		}

		public float getContinentalnessNoise() {
			return this.continentalnessNoise;
		}

		public float getErosionNoise() {
			return this.erosionNoise;
		}

		public float getWeirdnessNoise() {
			return this.weirdnessNoise;
		}
	}
}
