package net.minecraft.world.biome.source.util;

import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;

public final class VanillaTerrainParameters {
	public static final float OFFSET_VALUE_OFFSET = 0.015F;
	public static final float field_34305 = -0.05F;
	public static final float field_34306 = 0.15F;
	static ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> offsetSpline;
	static ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> factorSpline;
	static ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> field_34342;

	public VanillaTerrainParameters() {
		init();
	}

	public static void init() {
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = createLandSpline("beachSpline", -0.15F, -0.05F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = createLandSpline("lowSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = createLandSpline("midSpline", -0.1F, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline4 = createLandSpline("highSpline", -0.05F, 0.3F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true);
		float f = -0.51F;
		float g = -0.4F;
		float h = 0.1F;
		float i = -0.15F;
		offsetSpline = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getContinentalnessNoise)
			.setName("offsetSampler")
			.add(-1.1F, 0.044F, 0.0F)
			.add(-1.02F, -0.2222F, 0.0F)
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
		factorSpline = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getContinentalnessNoise)
			.setName("Factor-Continents")
			.add(-0.19F, 505.0F, 0.0F)
			.add(-0.15F, buildErosionFactorSpline("erosionCoast", 800.0F, true, "ridgeCoast-OldMountains"), 0.0F)
			.add(-0.1F, buildErosionFactorSpline("erosionInland", 700.0F, true, "ridgeInland-OldMountains"), 0.0F)
			.add(0.03F, buildErosionFactorSpline("erosionMidInland", 650.0F, true, "ridgeMidInland-OldMountains"), 0.0F)
			.add(0.06F, buildErosionFactorSpline("erosionFarInland", 600.0F, false, "ridgeFarInland-OldMountains"), 0.0F)
			.build()
			.getThis();
		field_34342 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getContinentalnessNoise)
			.setName("Peaks")
			.add(0.1F, 0.0F, 0.0F)
			.add(
				0.2F,
				Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getErosionNoise)
					.setName("Peaks-erosion")
					.add(
						-0.8F,
						Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
							.setName("Peaks-erosion-ridges")
							.add(-1.0F, 0.0F, 0.0F)
							.add(0.2F, 0.0F, 0.0F)
							.add(
								1.0F,
								Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::method_37872)
									.setName("Peaks-erosion-ridges-weirdness")
									.add(-0.01F, 80.0F, 0.0F)
									.add(0.01F, 20.0F, 0.0F)
									.build(),
								0.0F
							)
							.build(),
						0.0F
					)
					.add(-0.4F, 0.0F, 0.0F)
					.build(),
				0.0F
			)
			.build();
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> buildErosionFactorSpline(
		String erosionName, float value, boolean addShatteredRidges, String weirdnessName
	) {
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::method_37872)
			.setName("-base")
			.add(-0.2F, 800.0F, 0.0F)
			.add(0.2F, value, 0.0F)
			.build();
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getErosionNoise)
			.setName(erosionName)
			.add(-0.6F, spline, 0.0F)
			.add(
				-0.5F,
				Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::method_37872)
					.setName(erosionName + "-variation-1")
					.add(-0.05F, 800.0F, 0.0F)
					.add(0.05F, 342.0F, 0.0F)
					.build(),
				0.0F
			)
			.add(-0.35F, spline, 0.0F)
			.add(-0.25F, spline, 0.0F)
			.add(
				-0.1F,
				Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::method_37872)
					.setName(erosionName + "-variation-2")
					.add(-0.05F, 342.0F, 0.0F)
					.add(0.05F, 800.0F, 0.0F)
					.build(),
				0.0F
			)
			.add(0.03F, spline, 0.0F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(weirdnessName)
			.add(-0.7F, spline, 0.0F)
			.add(-0.15F, 175.0F, 0.0F)
			.build();
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(weirdnessName)
			.add(0.45F, spline, 0.0F)
			.add(0.7F, 200.0F, 0.0F)
			.build();
		if (addShatteredRidges) {
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline4 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::method_37872)
				.setName("weirdnessShattered")
				.add(0.0F, value, 0.0F)
				.add(0.1F, 80.0F, 0.0F)
				.build();
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline5 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
				.setName("ridgesShattered")
				.add(-0.9F, value, 0.0F)
				.add(-0.69F, spline4, 0.0F)
				.build();
			builder.add(0.35F, value, 0.0F).add(0.45F, spline5, 0.0F).add(0.55F, spline5, 0.0F).add(0.62F, value, 0.0F);
		} else {
			builder.add(0.05F, spline3, 0.0F).add(0.4F, spline3, 0.0F).add(0.45F, spline2, 0.0F).add(0.55F, spline2, 0.0F).add(0.58F, value, 0.0F);
		}

		return builder.build();
	}

	private Spline<VanillaTerrainParameters.TerrainNoisePoint> createSimpleOffsetSpline() {
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getContinentalnessNoise);
		float f = 0.1F;

		for (float g = -1.0F; g < 1.0F + f; g += f) {
			if (g < 0.0F) {
				builder.add(g, 0.0F, 0.0F);
			} else {
				builder.add(g, createLandSpline(g), 0.0F);
			}
		}

		return builder.setName("").build();
	}

	private static float method_37733(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> method_37740(float f, boolean bl) {
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(String.format("M-spline for continentalness: %.02f", f));
		float g = -0.7F;
		float h = -1.0F;
		float i = getOffsetValue(-1.0F, f, -0.7F);
		float j = 1.0F;
		float k = getOffsetValue(1.0F, f, -0.7F);
		float l = method_37744(f);
		float m = -0.65F;
		if (-0.65F < l && l < 1.0F) {
			float n = getOffsetValue(-0.65F, f, -0.7F);
			float o = -0.75F;
			float p = getOffsetValue(-0.75F, f, -0.7F);
			float q = method_37733(i, p, -1.0F, -0.75F);
			builder.add(-1.0F, i, q);
			builder.add(-0.75F, p, 0.0F);
			builder.add(-0.65F, n, 0.0F);
			float r = getOffsetValue(l, f, -0.7F);
			float s = method_37733(r, k, l, 1.0F);
			float t = 0.01F;
			builder.add(l - 0.01F, r, 0.0F);
			builder.add(l, r, s);
			builder.add(1.0F, k, s);
		} else {
			float n = method_37733(i, k, -1.0F, 1.0F);
			if (bl) {
				builder.add(-1.0F, Math.max(0.2F, i), 0.0F);
				builder.add(0.0F, MathHelper.lerp(0.5F, i, k), n);
			} else {
				builder.add(-1.0F, i, n);
			}

			builder.add(1.0F, k, n);
		}

		return builder.build();
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> createLandSpline(float continentalness) {
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(String.format("M-spline for continentalness: %.02f", continentalness));
		float f = 0.1F;
		float g = 0.7F;

		for (float h = -1.0F; h < 1.1F; h += 0.1F) {
			builder.add(h, getOffsetValue(h, continentalness, 0.7F), 0.0F);
		}

		return builder.build();
	}

	private static float getOffsetValue(float weirdness, float continentalness, float weirdnessThreshold) {
		float f = 1.17F;
		float g = 0.46082947F;
		float h = 1.0F - (1.0F - continentalness) * 0.5F;
		float i = 0.5F * (1.0F - continentalness);
		float j = (weirdness + 1.17F) * 0.46082947F;
		float k = j * h - i;
		return weirdness < weirdnessThreshold ? Math.max(k, -0.2222F) : Math.max(k, 0.0F);
	}

	private static float method_37744(float continentalness) {
		float f = 1.17F;
		float g = 0.46082947F;
		float h = 1.0F - (1.0F - continentalness) * 0.5F;
		float i = 0.5F * (1.0F - continentalness);
		return i / (0.46082947F * h) - 1.17F;
	}

	public static Spline<VanillaTerrainParameters.TerrainNoisePoint> createLandSpline(
		String name, float f, float g, float h, float i, float j, float k, float l, boolean bl, boolean bl2
	) {
		float m = 0.6F;
		float n = 1.5F;
		float o = 0.5F;
		float p = 0.5F;
		float q = 1.2F;
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = method_37740(MathHelper.lerp(j, 0.6F, 1.5F), bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = method_37740(MathHelper.lerp(j, 0.6F, 1.0F), bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = method_37740(j, bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline4 = createFlatOffsetSpline(
			name + "-widePlateau", f - 0.15F, 0.5F * j, MathHelper.lerp(0.5F, 0.5F, 0.5F) * j, 0.5F * j, 0.6F * j, 0.5F
		);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline5 = createFlatOffsetSpline(name + "-narrowPlateau", f, k * j, h * j, 0.5F * j, 0.6F * j, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline6 = createFlatOffsetSpline(name + "-plains", f, k, k, h, i, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline7 = createFlatOffsetSpline(name + "-plainsFarInland", f, k, k, h, i, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline8 = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(name)
			.add(-1.0F, f, 0.0F)
			.add(-0.4F, spline6, 0.0F)
			.add(0.0F, i + 0.07F, 0.0F)
			.build();
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline9 = createFlatOffsetSpline(name + "-swamps", -0.02F, l, l, h, i, 0.0F);
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getErosionNoise)
			.setName(name)
			.add(-0.85F, spline, 0.0F)
			.add(-0.7F, spline2, 0.0F)
			.add(-0.4F, spline3, 0.0F)
			.add(-0.35F, spline4, 0.0F)
			.add(-0.1F, spline5, 0.0F)
			.add(0.2F, spline6, 0.0F);
		if (bl) {
			builder.add(0.4F, spline7, 0.0F).add(0.45F, spline8, 0.0F).add(0.55F, spline8, 0.0F).add(0.58F, spline7, 0.0F);
		}

		builder.add(0.7F, spline9, 0.0F);
		return builder.build();
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> createFlatOffsetSpline(String name, float f, float g, float h, float i, float j, float k) {
		float l = Math.max(0.5F * (g - f), k);
		float m = 5.0F * (h - g);
		return Spline.builder(VanillaTerrainParameters.TerrainNoisePoint::getWeirdnessNoise)
			.setName(name)
			.add(-1.0F, f, l)
			.add(-0.4F, g, Math.min(l, m))
			.add(0.0F, h, m)
			.add(0.4F, i, 2.0F * (i - h))
			.add(1.0F, j, 0.7F * (j - i))
			.build();
	}

	public float getOffset(VanillaTerrainParameters.TerrainNoisePoint point) {
		return offsetSpline.apply(point) + 0.015F;
	}

	public float getFactor(VanillaTerrainParameters.TerrainNoisePoint point) {
		return factorSpline.apply(point);
	}

	public float method_37871(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
		return field_34342.apply(terrainNoisePoint);
	}

	public VanillaTerrainParameters.TerrainNoisePoint createTerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
		return new VanillaTerrainParameters.TerrainNoisePoint(continentalnessNoise, erosionNoise, getNormalizedWeirdness(weirdnessNoise), weirdnessNoise);
	}

	public static boolean method_37848(float f, float g) {
		return f < -0.05F ? true : Math.abs(g) < 0.15F;
	}

	public static float getNormalizedWeirdness(float weirdness) {
		return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	public static void debug(String[] strings) {
		init();
		System.out.println(offsetSpline.toString());
	}

	public static final class TerrainNoisePoint {
		private final float continentalnessNoise;
		private final float erosionNoise;
		private final float weirdnessNoise;
		private final float field_34343;

		public TerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise, float f) {
			this.continentalnessNoise = continentalnessNoise;
			this.erosionNoise = erosionNoise;
			this.weirdnessNoise = weirdnessNoise;
			this.field_34343 = f;
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

		public float method_37872() {
			return this.field_34343;
		}
	}
}
