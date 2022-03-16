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
	private static final ToFloatFunction<Float> field_38028 = ToFloatFunction.field_37409;
	private static final ToFloatFunction<Float> field_38029 = ToFloatFunction.method_41308(f -> f < 0.0F ? f : f * 2.0F);
	private static final ToFloatFunction<Float> field_38030 = ToFloatFunction.method_41308(f -> 1.25F - 6.25F / (f + 5.0F));
	private static final ToFloatFunction<Float> field_38031 = ToFloatFunction.method_41308(f -> f * 2.0F);

	public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42056(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, boolean bl) {
		ToFloatFunction<Float> toFloatFunction4 = bl ? field_38029 : field_38028;
		Spline<C, I> spline = method_42051(toFloatFunction2, toFloatFunction3, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, toFloatFunction4);
		Spline<C, I> spline2 = method_42051(toFloatFunction2, toFloatFunction3, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, toFloatFunction4);
		Spline<C, I> spline3 = method_42051(toFloatFunction2, toFloatFunction3, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, toFloatFunction4);
		Spline<C, I> spline4 = method_42051(toFloatFunction2, toFloatFunction3, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, toFloatFunction4);
		return Spline.<C, I>builder(toFloatFunction, toFloatFunction4)
			.method_41294(-1.1F, 0.044F)
			.method_41294(-1.02F, -0.2222F)
			.method_41294(-0.51F, -0.2222F)
			.method_41294(-0.44F, -0.12F)
			.method_41294(-0.18F, -0.12F)
			.method_41295(-0.16F, spline)
			.method_41295(-0.15F, spline)
			.method_41295(-0.1F, spline2)
			.method_41295(0.25F, spline3)
			.method_41295(1.0F, spline4)
			.build();
	}

	public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42055(
		I toFloatFunction, I toFloatFunction2, I toFloatFunction3, I toFloatFunction4, boolean bl
	) {
		ToFloatFunction<Float> toFloatFunction5 = bl ? field_38030 : field_38028;
		return Spline.<C, I>builder(toFloatFunction, field_38028)
			.method_41294(-0.19F, 3.95F)
			.method_41295(-0.15F, method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 6.25F, true, field_38028))
			.method_41295(-0.1F, method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 5.47F, true, toFloatFunction5))
			.method_41295(0.03F, method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 5.08F, true, toFloatFunction5))
			.method_41295(0.06F, method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 4.69F, false, toFloatFunction5))
			.build();
	}

	public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42058(
		I toFloatFunction, I toFloatFunction2, I toFloatFunction3, I toFloatFunction4, boolean bl
	) {
		ToFloatFunction<Float> toFloatFunction5 = bl ? field_38031 : field_38028;
		float f = 0.65F;
		return Spline.<C, I>builder(toFloatFunction, toFloatFunction5)
			.method_41294(-0.11F, 0.0F)
			.method_41295(0.03F, method_42053(toFloatFunction2, toFloatFunction3, toFloatFunction4, 1.0F, 0.5F, 0.0F, 0.0F, toFloatFunction5))
			.method_41295(0.65F, method_42053(toFloatFunction2, toFloatFunction3, toFloatFunction4, 1.0F, 1.0F, 1.0F, 0.0F, toFloatFunction5))
			.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42053(
		I toFloatFunction, I toFloatFunction2, I toFloatFunction3, float f, float g, float h, float i, ToFloatFunction<Float> toFloatFunction4
	) {
		float j = -0.5775F;
		Spline<C, I> spline = method_42052(toFloatFunction2, toFloatFunction3, f, h, toFloatFunction4);
		Spline<C, I> spline2 = method_42052(toFloatFunction2, toFloatFunction3, g, i, toFloatFunction4);
		return Spline.<C, I>builder(toFloatFunction, toFloatFunction4)
			.method_41295(-1.0F, spline)
			.method_41295(-0.78F, spline2)
			.method_41295(-0.5775F, spline2)
			.method_41294(-0.375F, 0.0F)
			.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42052(
		I toFloatFunction, I toFloatFunction2, float f, float g, ToFloatFunction<Float> toFloatFunction3
	) {
		float h = DensityFunctions.method_41546(0.4F);
		float i = DensityFunctions.method_41546(0.56666666F);
		float j = (h + i) / 2.0F;
		Spline.Builder<C, I> builder = Spline.builder(toFloatFunction2, toFloatFunction3);
		builder.method_41294(h, 0.0F);
		if (g > 0.0F) {
			builder.method_41295(j, method_42049(toFloatFunction, g, toFloatFunction3));
		} else {
			builder.method_41294(j, 0.0F);
		}

		if (f > 0.0F) {
			builder.method_41295(1.0F, method_42049(toFloatFunction, f, toFloatFunction3));
		} else {
			builder.method_41294(1.0F, 0.0F);
		}

		return builder.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42049(I toFloatFunction, float f, ToFloatFunction<Float> toFloatFunction2) {
		float g = 0.63F * f;
		float h = 0.3F * f;
		return Spline.<C, I>builder(toFloatFunction, toFloatFunction2).method_41294(-0.01F, g).method_41294(0.01F, h).build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42054(
		I toFloatFunction, I toFloatFunction2, I toFloatFunction3, float f, boolean bl, ToFloatFunction<Float> toFloatFunction4
	) {
		Spline<C, I> spline = Spline.<C, I>builder(toFloatFunction2, toFloatFunction4).method_41294(-0.2F, 6.3F).method_41294(0.2F, f).build();
		Spline.Builder<C, I> builder = Spline.<C, I>builder(toFloatFunction, toFloatFunction4)
			.method_41295(-0.6F, spline)
			.method_41295(-0.5F, Spline.<C, I>builder(toFloatFunction2, toFloatFunction4).method_41294(-0.05F, 6.3F).method_41294(0.05F, 2.67F).build())
			.method_41295(-0.35F, spline)
			.method_41295(-0.25F, spline)
			.method_41295(-0.1F, Spline.<C, I>builder(toFloatFunction2, toFloatFunction4).method_41294(-0.05F, 2.67F).method_41294(0.05F, 6.3F).build())
			.method_41295(0.03F, spline);
		if (bl) {
			Spline<C, I> spline2 = Spline.<C, I>builder(toFloatFunction2, toFloatFunction4).method_41294(0.0F, f).method_41294(0.1F, 0.625F).build();
			Spline<C, I> spline3 = Spline.<C, I>builder(toFloatFunction3, toFloatFunction4).method_41294(-0.9F, f).method_41295(-0.69F, spline2).build();
			builder.method_41294(0.35F, f).method_41295(0.45F, spline3).method_41295(0.55F, spline3).method_41294(0.62F, f);
		} else {
			Spline<C, I> spline2 = Spline.<C, I>builder(toFloatFunction3, toFloatFunction4).method_41295(-0.7F, spline).method_41294(-0.15F, 1.37F).build();
			Spline<C, I> spline3 = Spline.<C, I>builder(toFloatFunction3, toFloatFunction4).method_41295(0.45F, spline).method_41294(0.7F, 1.56F).build();
			builder.method_41295(0.05F, spline3).method_41295(0.4F, spline3).method_41295(0.45F, spline2).method_41295(0.55F, spline2).method_41294(0.58F, f);
		}

		return builder.build();
	}

	private static float method_42047(float f, float g, float h, float i) {
		return (g - f) / (i - h);
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42050(I toFloatFunction, float f, boolean bl, ToFloatFunction<Float> toFloatFunction2) {
		Spline.Builder<C, I> builder = Spline.builder(toFloatFunction, toFloatFunction2);
		float g = -0.7F;
		float h = -1.0F;
		float i = method_42046(-1.0F, f, -0.7F);
		float j = 1.0F;
		float k = method_42046(1.0F, f, -0.7F);
		float l = method_42045(f);
		float m = -0.65F;
		if (-0.65F < l && l < 1.0F) {
			float n = method_42046(-0.65F, f, -0.7F);
			float o = -0.75F;
			float p = method_42046(-0.75F, f, -0.7F);
			float q = method_42047(i, p, -1.0F, -0.75F);
			builder.add(-1.0F, i, q);
			builder.method_41294(-0.75F, p);
			builder.method_41294(-0.65F, n);
			float r = method_42046(l, f, -0.7F);
			float s = method_42047(r, k, l, 1.0F);
			float t = 0.01F;
			builder.method_41294(l - 0.01F, r);
			builder.add(l, r, s);
			builder.add(1.0F, k, s);
		} else {
			float n = method_42047(i, k, -1.0F, 1.0F);
			if (bl) {
				builder.method_41294(-1.0F, Math.max(0.2F, i));
				builder.add(0.0F, MathHelper.lerp(0.5F, i, k), n);
			} else {
				builder.add(-1.0F, i, n);
			}

			builder.add(1.0F, k, n);
		}

		return builder.build();
	}

	private static float method_42046(float f, float g, float h) {
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

	public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42051(
		I toFloatFunction, I toFloatFunction2, float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2, ToFloatFunction<Float> toFloatFunction3
	) {
		float l = 0.6F;
		float m = 0.5F;
		float n = 0.5F;
		Spline<C, I> spline = method_42050(toFloatFunction2, MathHelper.lerp(i, 0.6F, 1.5F), bl2, toFloatFunction3);
		Spline<C, I> spline2 = method_42050(toFloatFunction2, MathHelper.lerp(i, 0.6F, 1.0F), bl2, toFloatFunction3);
		Spline<C, I> spline3 = method_42050(toFloatFunction2, i, bl2, toFloatFunction3);
		Spline<C, I> spline4 = method_42048(toFloatFunction2, f - 0.15F, 0.5F * i, MathHelper.lerp(0.5F, 0.5F, 0.5F) * i, 0.5F * i, 0.6F * i, 0.5F, toFloatFunction3);
		Spline<C, I> spline5 = method_42048(toFloatFunction2, f, j * i, g * i, 0.5F * i, 0.6F * i, 0.5F, toFloatFunction3);
		Spline<C, I> spline6 = method_42048(toFloatFunction2, f, j, j, g, h, 0.5F, toFloatFunction3);
		Spline<C, I> spline7 = method_42048(toFloatFunction2, f, j, j, g, h, 0.5F, toFloatFunction3);
		Spline<C, I> spline8 = Spline.<C, I>builder(toFloatFunction2, toFloatFunction3)
			.method_41294(-1.0F, f)
			.method_41295(-0.4F, spline6)
			.method_41294(0.0F, h + 0.07F)
			.build();
		Spline<C, I> spline9 = method_42048(toFloatFunction2, -0.02F, k, k, g, h, 0.0F, toFloatFunction3);
		Spline.Builder<C, I> builder = Spline.<C, I>builder(toFloatFunction, toFloatFunction3)
			.method_41295(-0.85F, spline)
			.method_41295(-0.7F, spline2)
			.method_41295(-0.4F, spline3)
			.method_41295(-0.35F, spline4)
			.method_41295(-0.1F, spline5)
			.method_41295(0.2F, spline6);
		if (bl) {
			builder.method_41295(0.4F, spline7).method_41295(0.45F, spline8).method_41295(0.55F, spline8).method_41295(0.58F, spline7);
		}

		builder.method_41295(0.7F, spline9);
		return builder.build();
	}

	private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42048(
		I toFloatFunction, float f, float g, float h, float i, float j, float k, ToFloatFunction<Float> toFloatFunction2
	) {
		float l = Math.max(0.5F * (g - f), k);
		float m = 5.0F * (h - g);
		return Spline.<C, I>builder(toFloatFunction, toFloatFunction2)
			.add(-1.0F, f, l)
			.add(-0.4F, g, Math.min(l, m))
			.add(0.0F, h, m)
			.add(0.4F, i, 2.0F * (i - h))
			.add(1.0F, j, 0.7F * (j - i))
			.build();
	}
}
