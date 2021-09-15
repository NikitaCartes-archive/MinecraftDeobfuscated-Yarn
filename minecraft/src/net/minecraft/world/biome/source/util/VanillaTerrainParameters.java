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
	static final ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> field_34524 = new ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint>() {
		public float apply(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint.continentalnessNoise;
		}

		public String toString() {
			return "continents";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> field_34525 = new ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint>() {
		public float apply(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint.erosionNoise;
		}

		public String toString() {
			return "erosion";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> field_34526 = new ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint>() {
		public float apply(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint.field_34535;
		}

		public String toString() {
			return "weirdness";
		}
	};
	static final ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint> field_34527 = new ToFloatFunction<VanillaTerrainParameters.TerrainNoisePoint>() {
		public float apply(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
			return terrainNoisePoint.weirdnessNoise;
		}

		public String toString() {
			return "ridges";
		}
	};
	@Debug
	public Spline<VanillaTerrainParameters.TerrainNoisePoint> offsetSpline;
	@Debug
	public Spline<VanillaTerrainParameters.TerrainNoisePoint> factorSpline;
	@Debug
	public Spline<VanillaTerrainParameters.TerrainNoisePoint> field_34530;

	public VanillaTerrainParameters() {
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = createLandSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = createLandSpline(-0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = createLandSpline(-0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline4 = createLandSpline(-0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true);
		float f = -0.51F;
		float g = -0.4F;
		float h = 0.1F;
		float i = -0.15F;
		this.offsetSpline = Spline.builder(field_34524)
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
		this.factorSpline = Spline.builder(field_34524)
			.add(-0.19F, 3.95F, 0.0F)
			.add(-0.15F, buildErosionFactorSpline(6.25F, true), 0.0F)
			.add(-0.1F, buildErosionFactorSpline(5.47F, true), 0.0F)
			.add(0.03F, buildErosionFactorSpline(5.08F, true), 0.0F)
			.add(0.06F, buildErosionFactorSpline(4.69F, false), 0.0F)
			.build();
		this.field_34530 = Spline.builder(field_34524)
			.add(0.1F, 0.0F, 0.0F)
			.add(
				0.2F,
				Spline.builder(field_34525)
					.add(
						-0.8F,
						Spline.builder(field_34527)
							.add(-1.0F, 0.0F, 0.0F)
							.add(0.2F, 0.0F, 0.0F)
							.add(1.0F, Spline.builder(field_34526).add(-0.01F, 0.625F, 0.0F).add(0.01F, 0.15625F, 0.0F).build(), 0.0F)
							.build(),
						0.0F
					)
					.add(-0.4F, 0.0F, 0.0F)
					.build(),
				0.0F
			)
			.build();
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> buildErosionFactorSpline(float f, boolean bl) {
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = Spline.builder(field_34526).add(-0.2F, 6.3F, 0.0F).add(0.2F, f, 0.0F).build();
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(field_34525)
			.add(-0.6F, spline, 0.0F)
			.add(-0.5F, Spline.builder(field_34526).add(-0.05F, 6.3F, 0.0F).add(0.05F, 2.67F, 0.0F).build(), 0.0F)
			.add(-0.35F, spline, 0.0F)
			.add(-0.25F, spline, 0.0F)
			.add(-0.1F, Spline.builder(field_34526).add(-0.05F, 2.67F, 0.0F).add(0.05F, 6.3F, 0.0F).build(), 0.0F)
			.add(0.03F, spline, 0.0F);
		if (bl) {
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = Spline.builder(field_34526).add(0.0F, f, 0.0F).add(0.1F, 0.625F, 0.0F).build();
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = Spline.builder(field_34527).add(-0.9F, f, 0.0F).add(-0.69F, spline2, 0.0F).build();
			builder.add(0.35F, f, 0.0F).add(0.45F, spline3, 0.0F).add(0.55F, spline3, 0.0F).add(0.62F, f, 0.0F);
		} else {
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = Spline.builder(field_34527).add(-0.7F, spline, 0.0F).add(-0.15F, 1.37F, 0.0F).build();
			Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = Spline.builder(field_34527).add(0.45F, spline, 0.0F).add(0.7F, 1.56F, 0.0F).build();
			builder.add(0.05F, spline3, 0.0F).add(0.4F, spline3, 0.0F).add(0.45F, spline2, 0.0F).add(0.55F, spline2, 0.0F).add(0.58F, f, 0.0F);
		}

		return builder.build();
	}

	private static float method_38210(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> method_38219(float f, boolean bl) {
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(field_34527);
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

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> createLandSpline(
		float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2
	) {
		float l = 0.6F;
		float m = 0.5F;
		float n = 0.5F;
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = method_38219(MathHelper.lerp(i, 0.6F, 1.5F), bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline2 = method_38219(MathHelper.lerp(i, 0.6F, 1.0F), bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline3 = method_38219(i, bl2);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline4 = createFlatOffsetSpline(
			f - 0.15F, 0.5F * i, MathHelper.lerp(0.5F, 0.5F, 0.5F) * i, 0.5F * i, 0.6F * i, 0.5F
		);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline5 = createFlatOffsetSpline(f, j * i, g * i, 0.5F * i, 0.6F * i, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline6 = createFlatOffsetSpline(f, j, j, g, h, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline7 = createFlatOffsetSpline(f, j, j, g, h, 0.5F);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline8 = Spline.builder(field_34527)
			.add(-1.0F, f, 0.0F)
			.add(-0.4F, spline6, 0.0F)
			.add(0.0F, h + 0.07F, 0.0F)
			.build();
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline9 = createFlatOffsetSpline(-0.02F, k, k, g, h, 0.0F);
		Spline.Builder<VanillaTerrainParameters.TerrainNoisePoint> builder = Spline.builder(field_34525)
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

	private static Spline<VanillaTerrainParameters.TerrainNoisePoint> createFlatOffsetSpline(float f, float g, float h, float i, float j, float k) {
		float l = Math.max(0.5F * (g - f), k);
		float m = 5.0F * (h - g);
		return Spline.builder(field_34527)
			.add(-1.0F, f, l)
			.add(-0.4F, g, Math.min(l, m))
			.add(0.0F, h, m)
			.add(0.4F, i, 2.0F * (i - h))
			.add(1.0F, j, 0.7F * (j - i))
			.build();
	}

	public void method_38215(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
		MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.method_38121(-1.0F, 1.0F);
		consumer.accept(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(
					parameterRange, parameterRange, parameterRange, parameterRange, MultiNoiseUtil.ParameterRange.method_38120(0.0F), parameterRange, 0.01F
				),
				BiomeKeys.PLAINS
			)
		);
		Spline<VanillaTerrainParameters.TerrainNoisePoint> spline = createLandSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false);
		RegistryKey<Biome> registryKey = BiomeKeys.DESERT;

		for (Float float_ : spline.method_37921()) {
			consumer.accept(
				Pair.of(
					MultiNoiseUtil.createNoiseHypercube(
						parameterRange,
						parameterRange,
						parameterRange,
						MultiNoiseUtil.ParameterRange.method_38120(float_),
						MultiNoiseUtil.ParameterRange.method_38120(0.0F),
						parameterRange,
						0.0F
					),
					registryKey
				)
			);
			registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
		}

		for (Float float_ : this.offsetSpline.method_37921()) {
			consumer.accept(
				Pair.of(
					MultiNoiseUtil.createNoiseHypercube(
						parameterRange,
						parameterRange,
						MultiNoiseUtil.ParameterRange.method_38120(float_),
						parameterRange,
						MultiNoiseUtil.ParameterRange.method_38120(0.0F),
						parameterRange,
						0.0F
					),
					BiomeKeys.SNOWY_TAIGA
				)
			);
		}
	}

	@Debug
	public Spline<VanillaTerrainParameters.TerrainNoisePoint> method_38207() {
		return this.offsetSpline;
	}

	@Debug
	public Spline<VanillaTerrainParameters.TerrainNoisePoint> method_38216() {
		return this.factorSpline;
	}

	public float getOffset(VanillaTerrainParameters.TerrainNoisePoint point) {
		return this.offsetSpline.apply(point) + 0.015F;
	}

	public float getFactor(VanillaTerrainParameters.TerrainNoisePoint point) {
		return this.factorSpline.apply(point);
	}

	public float method_38221(VanillaTerrainParameters.TerrainNoisePoint terrainNoisePoint) {
		return this.field_34530.apply(terrainNoisePoint);
	}

	public VanillaTerrainParameters.TerrainNoisePoint createTerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise) {
		return new VanillaTerrainParameters.TerrainNoisePoint(continentalnessNoise, erosionNoise, getNormalizedWeirdness(weirdnessNoise), weirdnessNoise);
	}

	public static float getNormalizedWeirdness(float weirdness) {
		return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	public static final class TerrainNoisePoint {
		final float continentalnessNoise;
		final float erosionNoise;
		final float weirdnessNoise;
		final float field_34535;

		public TerrainNoisePoint(float continentalnessNoise, float erosionNoise, float weirdnessNoise, float f) {
			this.continentalnessNoise = continentalnessNoise;
			this.erosionNoise = erosionNoise;
			this.weirdnessNoise = weirdnessNoise;
			this.field_34535 = f;
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

		public float method_38229() {
			return this.field_34535;
		}
	}
}
