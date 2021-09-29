package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class VanillaTerrainParameters {
	private static final float OFFSET_VALUE_OFFSET = 0.015F;
	static final ToFloatFunction<VanillaTerrainParameters.NoisePoint> CONTINENTALNESS = new ToFloatFunction<VanillaTerrainParameters.NoisePoint>() {
		public float apply(VanillaTerrainParameters.NoisePoint noisePoint) {
			return noisePoint.continentalnessNoise;
		}

		public String toString() {
			return "continents";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.NoisePoint> EROSION = new ToFloatFunction<VanillaTerrainParameters.NoisePoint>() {
		public float apply(VanillaTerrainParameters.NoisePoint noisePoint) {
			return noisePoint.erosionNoise;
		}

		public String toString() {
			return "erosion";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.NoisePoint> WEIRDNESS = new ToFloatFunction<VanillaTerrainParameters.NoisePoint>() {
		public float apply(VanillaTerrainParameters.NoisePoint noisePoint) {
			return noisePoint.weirdnessNoise;
		}

		public String toString() {
			return "weirdness";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.NoisePoint> NORMALIZED_WEIRDNESS = new ToFloatFunction<VanillaTerrainParameters.NoisePoint>() {
		public float apply(VanillaTerrainParameters.NoisePoint noisePoint) {
			return noisePoint.normalizedWeirdness;
		}

		public String toString() {
			return "ridges";
		}
	};
	@Debug
	public Spline<VanillaTerrainParameters.NoisePoint> offsetSpline;
	@Debug
	public Spline<VanillaTerrainParameters.NoisePoint> factorSpline;
	@Debug
	public Spline<VanillaTerrainParameters.NoisePoint> peakSpline;

	public VanillaTerrainParameters() {
		Spline<VanillaTerrainParameters.NoisePoint> spline = createLandSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.NoisePoint> spline2 = createLandSpline(-0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.NoisePoint> spline3 = createLandSpline(-0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true);
		Spline<VanillaTerrainParameters.NoisePoint> spline4 = createLandSpline(-0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true);
		float f = -0.51F;
		float g = -0.4F;
		float h = 0.1F;
		float i = -0.15F;
		this.offsetSpline = Spline.builder(CONTINENTALNESS)
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
			.build();
		this.factorSpline = Spline.builder(CONTINENTALNESS)
			.add(-0.19F, 3.95F, 0.0F)
			.add(-0.15F, buildErosionFactorSpline(6.25F, true), 0.0F)
			.add(-0.1F, buildErosionFactorSpline(5.47F, true), 0.0F)
			.add(0.03F, buildErosionFactorSpline(5.08F, true), 0.0F)
			.add(0.06F, buildErosionFactorSpline(4.69F, false), 0.0F)
			.build();
		float j = 0.65F;
		this.peakSpline = Spline.builder(CONTINENTALNESS)
			.add(-0.11F, 0.0F, 0.0F)
			.add(0.03F, this.method_38856(1.0F, 0.5F, 0.0F, 0.0F), 0.0F)
			.add(0.65F, this.method_38856(1.0F, 1.0F, 1.0F, 0.0F), 0.0F)
			.build();
	}

	private Spline<VanillaTerrainParameters.NoisePoint> method_38856(float f, float g, float h, float i) {
		float j = -0.5775F;
		Spline<VanillaTerrainParameters.NoisePoint> spline = this.method_38855(f, h);
		Spline<VanillaTerrainParameters.NoisePoint> spline2 = this.method_38855(g, i);
		return Spline.builder(EROSION).add(-1.0F, spline, 0.0F).add(-0.78F, spline2, 0.0F).add(-0.5775F, spline2, 0.0F).add(-0.375F, 0.0F, 0.0F).build();
	}

	private Spline<VanillaTerrainParameters.NoisePoint> method_38855(float f, float g) {
		float h = getNormalizedWeirdness(0.4F);
		float i = getNormalizedWeirdness(0.56666666F);
		float j = (h + i) / 2.0F;
		Spline.Builder<VanillaTerrainParameters.NoisePoint> builder = Spline.builder(NORMALIZED_WEIRDNESS);
		builder.add(h, 0.0F, 0.0F);
		if (g > 0.0F) {
			builder.add(j, this.method_38857(g), 0.0F);
		} else {
			builder.add(j, 0.0F, 0.0F);
		}

		if (f > 0.0F) {
			builder.add(1.0F, this.method_38857(f), 0.0F);
		} else {
			builder.add(1.0F, 0.0F, 0.0F);
		}

		return builder.build();
	}

	private Spline<VanillaTerrainParameters.NoisePoint> method_38857(float f) {
		float g = 0.63F * f;
		float h = 0.3F * f;
		return Spline.builder(WEIRDNESS).add(-0.01F, g, 0.0F).add(0.01F, h, 0.0F).build();
	}

	private static Spline<VanillaTerrainParameters.NoisePoint> buildErosionFactorSpline(float value, boolean bl) {
		Spline<VanillaTerrainParameters.NoisePoint> spline = Spline.builder(WEIRDNESS).add(-0.2F, 6.3F, 0.0F).add(0.2F, value, 0.0F).build();
		Spline.Builder<VanillaTerrainParameters.NoisePoint> builder = Spline.builder(EROSION)
			.add(-0.6F, spline, 0.0F)
			.add(-0.5F, Spline.builder(WEIRDNESS).add(-0.05F, 6.3F, 0.0F).add(0.05F, 2.67F, 0.0F).build(), 0.0F)
			.add(-0.35F, spline, 0.0F)
			.add(-0.25F, spline, 0.0F)
			.add(-0.1F, Spline.builder(WEIRDNESS).add(-0.05F, 2.67F, 0.0F).add(0.05F, 6.3F, 0.0F).build(), 0.0F)
			.add(0.03F, spline, 0.0F);
		if (bl) {
			Spline<VanillaTerrainParameters.NoisePoint> spline2 = Spline.builder(WEIRDNESS).add(0.0F, value, 0.0F).add(0.1F, 0.625F, 0.0F).build();
			Spline<VanillaTerrainParameters.NoisePoint> spline3 = Spline.builder(NORMALIZED_WEIRDNESS).add(-0.9F, value, 0.0F).add(-0.69F, spline2, 0.0F).build();
			builder.add(0.35F, value, 0.0F).add(0.45F, spline3, 0.0F).add(0.55F, spline3, 0.0F).add(0.62F, value, 0.0F);
		} else {
			Spline<VanillaTerrainParameters.NoisePoint> spline2 = Spline.builder(NORMALIZED_WEIRDNESS).add(-0.7F, spline, 0.0F).add(-0.15F, 1.37F, 0.0F).build();
			Spline<VanillaTerrainParameters.NoisePoint> spline3 = Spline.builder(NORMALIZED_WEIRDNESS).add(0.45F, spline, 0.0F).add(0.7F, 1.56F, 0.0F).build();
			builder.add(0.05F, spline3, 0.0F).add(0.4F, spline3, 0.0F).add(0.45F, spline2, 0.0F).add(0.55F, spline2, 0.0F).add(0.58F, value, 0.0F);
		}

		return builder.build();
	}

	private static float method_38210(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static Spline<VanillaTerrainParameters.NoisePoint> method_38219(float f, boolean bl) {
		Spline.Builder<VanillaTerrainParameters.NoisePoint> builder = Spline.builder(NORMALIZED_WEIRDNESS);
		float g = -0.7F;
		float h = -1.0F;
		float i = getOffsetValue(-1.0F, f, -0.7F);
		float j = 1.0F;
		float k = getOffsetValue(1.0F, f, -0.7F);
		float l = method_38217(f);
		float m = -0.65F;
		if (-0.65F < l && l < 1.0F) {
			float n = getOffsetValue(-0.65F, f, -0.7F);
			float o = -0.75F;
			float p = getOffsetValue(-0.75F, f, -0.7F);
			float q = method_38210(i, p, -1.0F, -0.75F);
			builder.add(-1.0F, i, q);
			builder.add(-0.75F, p, 0.0F);
			builder.add(-0.65F, n, 0.0F);
			float r = getOffsetValue(l, f, -0.7F);
			float s = method_38210(r, k, l, 1.0F);
			float t = 0.01F;
			builder.add(l - 0.01F, r, 0.0F);
			builder.add(l, r, s);
			builder.add(1.0F, k, s);
		} else {
			float n = method_38210(i, k, -1.0F, 1.0F);
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

	private static float getOffsetValue(float weirdness, float continentalness, float weirdnessThreshold) {
		float f = 1.17F;
		float g = 0.46082947F;
		float h = 1.0F - (1.0F - continentalness) * 0.5F;
		float i = 0.5F * (1.0F - continentalness);
		float j = (weirdness + 1.17F) * 0.46082947F;
		float k = j * h - i;
		return weirdness < weirdnessThreshold ? Math.max(k, -0.2222F) : Math.max(k, 0.0F);
	}

	private static float method_38217(float continentalness) {
		float f = 1.17F;
		float g = 0.46082947F;
		float h = 1.0F - (1.0F - continentalness) * 0.5F;
		float i = 0.5F * (1.0F - continentalness);
		return i / (0.46082947F * h) - 1.17F;
	}

	private static Spline<VanillaTerrainParameters.NoisePoint> createLandSpline(float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2) {
		float l = 0.6F;
		float m = 0.5F;
		float n = 0.5F;
		Spline<VanillaTerrainParameters.NoisePoint> spline = method_38219(MathHelper.lerp(i, 0.6F, 1.5F), bl2);
		Spline<VanillaTerrainParameters.NoisePoint> spline2 = method_38219(MathHelper.lerp(i, 0.6F, 1.0F), bl2);
		Spline<VanillaTerrainParameters.NoisePoint> spline3 = method_38219(i, bl2);
		Spline<VanillaTerrainParameters.NoisePoint> spline4 = createFlatOffsetSpline(
			f - 0.15F, 0.5F * i, MathHelper.lerp(0.5F, 0.5F, 0.5F) * i, 0.5F * i, 0.6F * i, 0.5F
		);
		Spline<VanillaTerrainParameters.NoisePoint> spline5 = createFlatOffsetSpline(f, j * i, g * i, 0.5F * i, 0.6F * i, 0.5F);
		Spline<VanillaTerrainParameters.NoisePoint> spline6 = createFlatOffsetSpline(f, j, j, g, h, 0.5F);
		Spline<VanillaTerrainParameters.NoisePoint> spline7 = createFlatOffsetSpline(f, j, j, g, h, 0.5F);
		Spline<VanillaTerrainParameters.NoisePoint> spline8 = Spline.builder(NORMALIZED_WEIRDNESS)
			.add(-1.0F, f, 0.0F)
			.add(-0.4F, spline6, 0.0F)
			.add(0.0F, h + 0.07F, 0.0F)
			.build();
		Spline<VanillaTerrainParameters.NoisePoint> spline9 = createFlatOffsetSpline(-0.02F, k, k, g, h, 0.0F);
		Spline.Builder<VanillaTerrainParameters.NoisePoint> builder = Spline.builder(EROSION)
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

	private static Spline<VanillaTerrainParameters.NoisePoint> createFlatOffsetSpline(float f, float g, float h, float i, float j, float k) {
		float l = Math.max(0.5F * (g - f), k);
		float m = 5.0F * (h - g);
		return Spline.builder(NORMALIZED_WEIRDNESS)
			.add(-1.0F, f, l)
			.add(-0.4F, g, Math.min(l, m))
			.add(0.0F, h, m)
			.add(0.4F, i, 2.0F * (i - h))
			.add(1.0F, j, 0.7F * (j - i))
			.build();
	}

	public void writeDebugBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F);
		parameters.accept(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(
					parameterRange, parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(0.0F), parameterRange, 0.01F
				),
				BiomeKeys.PLAINS
			)
		);
		Spline<VanillaTerrainParameters.NoisePoint> spline = createLandSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false);
		RegistryKey<Biome> registryKey = BiomeKeys.DESERT;

		for (Float float_ : spline.getLocations()) {
			parameters.accept(
				Pair.of(
					MultiNoiseUtil.createNoiseHypercube(
						parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_), MultiNoiseUtil.ParameterRange.of(0.0F), parameterRange, 0.0F
					),
					registryKey
				)
			);
			registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
		}

		for (Float float_ : this.offsetSpline.getLocations()) {
			parameters.accept(
				Pair.of(
					MultiNoiseUtil.createNoiseHypercube(
						parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.of(float_), parameterRange, MultiNoiseUtil.ParameterRange.of(0.0F), parameterRange, 0.0F
					),
					BiomeKeys.SNOWY_TAIGA
				)
			);
		}
	}

	@Debug
	public Spline<VanillaTerrainParameters.NoisePoint> getOffsetSpline() {
		return this.offsetSpline;
	}

	@Debug
	public Spline<VanillaTerrainParameters.NoisePoint> getFactorSpline() {
		return this.factorSpline;
	}

	public float getOffset(VanillaTerrainParameters.NoisePoint point) {
		return this.offsetSpline.apply(point) + 0.015F;
	}

	public float getFactor(VanillaTerrainParameters.NoisePoint point) {
		return this.factorSpline.apply(point);
	}

	public float getPeak(VanillaTerrainParameters.NoisePoint point) {
		return this.peakSpline.apply(point);
	}

	public VanillaTerrainParameters.NoisePoint createNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
		return new VanillaTerrainParameters.NoisePoint(continentalnessNoise, erosionNoise, getNormalizedWeirdness(weirdnessNoise), weirdnessNoise);
	}

	public static float getNormalizedWeirdness(float weirdness) {
		return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	public static final class NoisePoint {
		final float continentalnessNoise;
		final float erosionNoise;
		final float normalizedWeirdness;
		final float weirdnessNoise;

		public NoisePoint(float continentalnessNoise, float erosionNoise, float normalizedWeirdness, float weirdnessNoise) {
			this.continentalnessNoise = continentalnessNoise;
			this.erosionNoise = erosionNoise;
			this.normalizedWeirdness = normalizedWeirdness;
			this.weirdnessNoise = weirdnessNoise;
		}

		public float getContinentalnessNoise() {
			return this.continentalnessNoise;
		}

		public float getErosionNoise() {
			return this.erosionNoise;
		}

		public float getNormalizedWeirdness() {
			return this.normalizedWeirdness;
		}

		public float getWeirdnessNoise() {
			return this.weirdnessNoise;
		}
	}
}
