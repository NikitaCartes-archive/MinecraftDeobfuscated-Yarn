package net.minecraft.world.biome.source.util;

import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class VanillaTerrainParametersCreator {
	private static final float field_38024 = -0.51F;
	private static final float field_38025 = -0.4F;
	private static final float field_38026 = 0.1F;
	private static final float field_38027 = -0.15F;
	private static final ToFloatFunction<Float> IDENTITY = ToFloatFunction.IDENTITY;
	private static final ToFloatFunction<Float> OFFSET_AMPLIFIER = ToFloatFunction.fromFloat(value -> value < 0.0F ? value : value * 2.0F);
	private static final ToFloatFunction<Float> FACTOR_AMPLIFIER = ToFloatFunction.fromFloat(value -> 1.25F - 6.25F / (value + 5.0F));
	private static final ToFloatFunction<Float> JAGGEDNESS_AMPLIFIER = ToFloatFunction.fromFloat(value -> value * 2.0F);

	/**
	 * Creates the spline for terrain offset.
	 * 
	 * Offset roughly correlates to surface height.
	 */
	public static <C, I extends ToFloatFunction<C>> Spline<C, I> createOffsetSpline(I continents, I erosion, I ridgesFolded, boolean amplified) {
		ToFloatFunction<Float> toFloatFunction = amplified ? OFFSET_AMPLIFIER : IDENTITY;
		Spline<C, I> spline = createContinentalOffsetSpline(erosion, ridgesFolded, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, toFloatFunction);
		Spline<C, I> spline2 = createContinentalOffsetSpline(erosion, ridgesFolded, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, toFloatFunction);
		Spline<C, I> spline3 = createContinentalOffsetSpline(erosion, ridgesFolded, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, toFloatFunction);
		Spline<C, I> spline4 = createContinentalOffsetSpline(erosion, ridgesFolded, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, toFloatFunction);
		return Spline.<C, I>builder(continents, toFloatFunction)
			.add(-1.1F, 0.044F)
			.add(-1.02F, -0.2222F)
			.add(-0.51F, -0.2222F)
			.add(-0.44F, -0.12F)
			.add(-0.18F, -0.12F)
			.add(-0.16F, spline)
			.add(-0.15F, spline)
			.add(-0.1F, spline2)
			.add(0.25F, spline3)
			.add(1.0F, spline4)
			.build();
	}

	/**
	 * Creates the spline for terrain factor.
	 * 
	 * Higher factor values generally result in flatter terrain,
	 * while lower values generally result in more shattered terrain.
	 */
	public static <C, I extends ToFloatFunction<C>> Spline<C, I> createFactorSpline(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
		ToFloatFunction<Float> toFloatFunction = amplified ? FACTOR_AMPLIFIER : IDENTITY;
		return Spline.<C, I>builder(continents, IDENTITY)
			.add(-0.19F, 3.95F)
			.add(-0.15F, method_42054(erosion, ridges, ridgesFolded, 6.25F, true, IDENTITY))
			.add(-0.1F, method_42054(erosion, ridges, ridgesFolded, 5.47F, true, toFloatFunction))
			.add(0.03F, method_42054(erosion, ridges, ridgesFolded, 5.08F, true, toFloatFunction))
			.add(0.06F, method_42054(erosion, ridges, ridgesFolded, 4.69F, false, toFloatFunction))
			.build();
	}

	/**
	 * Creates the spline for terrain jaggedness.
	 * 
	 * This is used for the peaks in the jagged peaks biome, for example.
	 */
	public static <C, I extends ToFloatFunction<C>> Spline<C, I> createJaggednessSpline(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
		ToFloatFunction<Float> toFloatFunction = amplified ? JAGGEDNESS_AMPLIFIER : IDENTITY;
		float f = 0.65F;
		return Spline.<C, I>builder(continents, toFloatFunction)
			.add(-0.11F, 0.0F)
			.add(0.03F, method_42053(erosion, ridges, ridgesFolded, 1.0F, 0.5F, 0.0F, 0.0F, toFloatFunction))
			.add(0.65F, method_42053(erosion, ridges, ridgesFolded, 1.0F, 1.0F, 1.0F, 0.0F, toFloatFunction))
			.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42053(
		I erosion, I ridges, I ridgesFolded, float f, float g, float h, float i, ToFloatFunction<Float> amplifier
	) {
		float j = -0.5775F;
		Spline<C, I> spline = method_42052(ridges, ridgesFolded, f, h, amplifier);
		Spline<C, I> spline2 = method_42052(ridges, ridgesFolded, g, i, amplifier);
		return Spline.<C, I>builder(erosion, amplifier).add(-1.0F, spline).add(-0.78F, spline2).add(-0.5775F, spline2).add(-0.375F, 0.0F).build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42052(I ridges, I ridgesFolded, float f, float g, ToFloatFunction<Float> amplifier) {
		float h = DensityFunctions.getPeaksValleysNoise(0.4F);
		float i = DensityFunctions.getPeaksValleysNoise(0.56666666F);
		float j = (h + i) / 2.0F;
		Spline.Builder<C, I> builder = Spline.builder(ridgesFolded, amplifier);
		builder.add(h, 0.0F);
		if (g > 0.0F) {
			builder.add(j, method_42049(ridges, g, amplifier));
		} else {
			builder.add(j, 0.0F);
		}

		if (f > 0.0F) {
			builder.add(1.0F, method_42049(ridges, f, amplifier));
		} else {
			builder.add(1.0F, 0.0F);
		}

		return builder.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42049(I ridges, float f, ToFloatFunction<Float> amplifier) {
		float g = 0.63F * f;
		float h = 0.3F * f;
		return Spline.<C, I>builder(ridges, amplifier).add(-0.01F, g).add(0.01F, h).build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42054(
		I erosion, I ridges, I ridgesFolded, float f, boolean bl, ToFloatFunction<Float> amplifier
	) {
		Spline<C, I> spline = Spline.<C, I>builder(ridges, amplifier).add(-0.2F, 6.3F).add(0.2F, f).build();
		Spline.Builder<C, I> builder = Spline.<C, I>builder(erosion, amplifier)
			.add(-0.6F, spline)
			.add(-0.5F, Spline.<C, I>builder(ridges, amplifier).add(-0.05F, 6.3F).add(0.05F, 2.67F).build())
			.add(-0.35F, spline)
			.add(-0.25F, spline)
			.add(-0.1F, Spline.<C, I>builder(ridges, amplifier).add(-0.05F, 2.67F).add(0.05F, 6.3F).build())
			.add(0.03F, spline);
		if (bl) {
			Spline<C, I> spline2 = Spline.<C, I>builder(ridges, amplifier).add(0.0F, f).add(0.1F, 0.625F).build();
			Spline<C, I> spline3 = Spline.<C, I>builder(ridgesFolded, amplifier).add(-0.9F, f).add(-0.69F, spline2).build();
			builder.add(0.35F, f).add(0.45F, spline3).add(0.55F, spline3).add(0.62F, f);
		} else {
			Spline<C, I> spline2 = Spline.<C, I>builder(ridgesFolded, amplifier).add(-0.7F, spline).add(-0.15F, 1.37F).build();
			Spline<C, I> spline3 = Spline.<C, I>builder(ridgesFolded, amplifier).add(0.45F, spline).add(0.7F, 1.56F).build();
			builder.add(0.05F, spline3).add(0.4F, spline3).add(0.45F, spline2).add(0.55F, spline2).add(0.58F, f);
		}

		return builder.build();
	}

	private static float method_42047(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42050(I ridgesFolded, float f, boolean bl, ToFloatFunction<Float> amplifier) {
		Spline.Builder<C, I> builder = Spline.builder(ridgesFolded, amplifier);
		float g = -0.7F;
		float h = -1.0F;
		float i = getOffsetValue(-1.0F, f, -0.7F);
		float j = 1.0F;
		float k = getOffsetValue(1.0F, f, -0.7F);
		float l = method_42045(f);
		float m = -0.65F;
		if (-0.65F < l && l < 1.0F) {
			float n = getOffsetValue(-0.65F, f, -0.7F);
			float o = -0.75F;
			float p = getOffsetValue(-0.75F, f, -0.7F);
			float q = method_42047(i, p, -1.0F, -0.75F);
			builder.add(-1.0F, i, q);
			builder.add(-0.75F, p);
			builder.add(-0.65F, n);
			float r = getOffsetValue(l, f, -0.7F);
			float s = method_42047(r, k, l, 1.0F);
			float t = 0.01F;
			builder.add(l - 0.01F, r);
			builder.add(l, r, s);
			builder.add(1.0F, k, s);
		} else {
			float n = method_42047(i, k, -1.0F, 1.0F);
			if (bl) {
				builder.add(-1.0F, Math.max(0.2F, i));
				builder.add(0.0F, MathHelper.lerp(0.5F, i, k), n);
			} else {
				builder.add(-1.0F, i, n);
			}

			builder.add(1.0F, k, n);
		}

		return builder.build();
	}

	private static float getOffsetValue(float f, float g, float h) {
		float i = 1.17F;
		float j = 0.46082947F;
		float k = 1.0F - (1.0F - g) * 0.5F;
		float l = 0.5F * (1.0F - g);
		float m = (f + 1.17F) * 0.46082947F;
		float n = m * k - l;
		return f < h ? Math.max(n, -0.2222F) : Math.max(n, 0.0F);
	}

	private static float method_42045(float f) {
		float g = 1.17F;
		float h = 0.46082947F;
		float i = 1.0F - (1.0F - f) * 0.5F;
		float j = 0.5F * (1.0F - f);
		return j / (0.46082947F * i) - 1.17F;
	}

	public static <C, I extends ToFloatFunction<C>> Spline<C, I> createContinentalOffsetSpline(
		I erosion, I ridgesFolded, float continentalness, float f, float g, float h, float i, float j, boolean bl, boolean bl2, ToFloatFunction<Float> amplifier
	) {
		float k = 0.6F;
		float l = 0.5F;
		float m = 0.5F;
		Spline<C, I> spline = method_42050(ridgesFolded, MathHelper.lerp(h, 0.6F, 1.5F), bl2, amplifier);
		Spline<C, I> spline2 = method_42050(ridgesFolded, MathHelper.lerp(h, 0.6F, 1.0F), bl2, amplifier);
		Spline<C, I> spline3 = method_42050(ridgesFolded, h, bl2, amplifier);
		Spline<C, I> spline4 = method_42048(
			ridgesFolded, continentalness - 0.15F, 0.5F * h, MathHelper.lerp(0.5F, 0.5F, 0.5F) * h, 0.5F * h, 0.6F * h, 0.5F, amplifier
		);
		Spline<C, I> spline5 = method_42048(ridgesFolded, continentalness, i * h, f * h, 0.5F * h, 0.6F * h, 0.5F, amplifier);
		Spline<C, I> spline6 = method_42048(ridgesFolded, continentalness, i, i, f, g, 0.5F, amplifier);
		Spline<C, I> spline7 = method_42048(ridgesFolded, continentalness, i, i, f, g, 0.5F, amplifier);
		Spline<C, I> spline8 = Spline.<C, I>builder(ridgesFolded, amplifier).add(-1.0F, continentalness).add(-0.4F, spline6).add(0.0F, g + 0.07F).build();
		Spline<C, I> spline9 = method_42048(ridgesFolded, -0.02F, j, j, f, g, 0.0F, amplifier);
		Spline.Builder<C, I> builder = Spline.<C, I>builder(erosion, amplifier)
			.add(-0.85F, spline)
			.add(-0.7F, spline2)
			.add(-0.4F, spline3)
			.add(-0.35F, spline4)
			.add(-0.1F, spline5)
			.add(0.2F, spline6);
		if (bl) {
			builder.add(0.4F, spline7).add(0.45F, spline8).add(0.55F, spline8).add(0.58F, spline7);
		}

		builder.add(0.7F, spline9);
		return builder.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42048(
		I ridgesFolded, float continentalness, float f, float g, float h, float i, float j, ToFloatFunction<Float> amplifier
	) {
		float k = Math.max(0.5F * (f - continentalness), j);
		float l = 5.0F * (g - f);
		return Spline.<C, I>builder(ridgesFolded, amplifier)
			.add(-1.0F, continentalness, k)
			.add(-0.4F, f, Math.min(k, l))
			.add(0.0F, g, l)
			.add(0.4F, h, 2.0F * (h - g))
			.add(1.0F, i, 0.7F * (i - h))
			.build();
	}
}
