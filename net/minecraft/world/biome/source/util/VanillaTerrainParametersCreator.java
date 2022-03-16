/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Spline;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class VanillaTerrainParametersCreator {
    private static final float field_38024 = -0.51f;
    private static final float field_38025 = -0.4f;
    private static final float field_38026 = 0.1f;
    private static final float field_38027 = -0.15f;
    private static final ToFloatFunction<Float> field_38028 = ToFloatFunction.field_37409;
    private static final ToFloatFunction<Float> field_38029 = ToFloatFunction.method_41308(f -> f < 0.0f ? f : f * 2.0f);
    private static final ToFloatFunction<Float> field_38030 = ToFloatFunction.method_41308(f -> 1.25f - 6.25f / (f + 5.0f));
    private static final ToFloatFunction<Float> field_38031 = ToFloatFunction.method_41308(f -> f * 2.0f);

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42056(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, boolean bl) {
        ToFloatFunction<Float> toFloatFunction4 = bl ? field_38029 : field_38028;
        Spline<C, I> spline = VanillaTerrainParametersCreator.method_42051(toFloatFunction2, toFloatFunction3, -0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, toFloatFunction4);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.method_42051(toFloatFunction2, toFloatFunction3, -0.1f, 0.03f, 0.1f, 0.1f, 0.01f, -0.03f, false, false, toFloatFunction4);
        Spline<C, I> spline3 = VanillaTerrainParametersCreator.method_42051(toFloatFunction2, toFloatFunction3, -0.1f, 0.03f, 0.1f, 0.7f, 0.01f, -0.03f, true, true, toFloatFunction4);
        Spline<C, I> spline4 = VanillaTerrainParametersCreator.method_42051(toFloatFunction2, toFloatFunction3, -0.05f, 0.03f, 0.1f, 1.0f, 0.01f, 0.01f, true, true, toFloatFunction4);
        return Spline.builder(toFloatFunction, toFloatFunction4).method_41294(-1.1f, 0.044f).method_41294(-1.02f, -0.2222f).method_41294(-0.51f, -0.2222f).method_41294(-0.44f, -0.12f).method_41294(-0.18f, -0.12f).method_41295(-0.16f, spline).method_41295(-0.15f, spline).method_41295(-0.1f, spline2).method_41295(0.25f, spline3).method_41295(1.0f, spline4).build();
    }

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42055(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, I toFloatFunction4, boolean bl) {
        ToFloatFunction<Float> toFloatFunction5 = bl ? field_38030 : field_38028;
        return Spline.builder(toFloatFunction, field_38028).method_41294(-0.19f, 3.95f).method_41295(-0.15f, VanillaTerrainParametersCreator.method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 6.25f, true, field_38028)).method_41295(-0.1f, VanillaTerrainParametersCreator.method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 5.47f, true, toFloatFunction5)).method_41295(0.03f, VanillaTerrainParametersCreator.method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 5.08f, true, toFloatFunction5)).method_41295(0.06f, VanillaTerrainParametersCreator.method_42054(toFloatFunction2, toFloatFunction3, toFloatFunction4, 4.69f, false, toFloatFunction5)).build();
    }

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42058(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, I toFloatFunction4, boolean bl) {
        ToFloatFunction<Float> toFloatFunction5 = bl ? field_38031 : field_38028;
        float f = 0.65f;
        return Spline.builder(toFloatFunction, toFloatFunction5).method_41294(-0.11f, 0.0f).method_41295(0.03f, VanillaTerrainParametersCreator.method_42053(toFloatFunction2, toFloatFunction3, toFloatFunction4, 1.0f, 0.5f, 0.0f, 0.0f, toFloatFunction5)).method_41295(0.65f, VanillaTerrainParametersCreator.method_42053(toFloatFunction2, toFloatFunction3, toFloatFunction4, 1.0f, 1.0f, 1.0f, 0.0f, toFloatFunction5)).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42053(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, float f, float g, float h, float i, ToFloatFunction<Float> toFloatFunction4) {
        float j = -0.5775f;
        Spline<C, I> spline = VanillaTerrainParametersCreator.method_42052(toFloatFunction2, toFloatFunction3, f, h, toFloatFunction4);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.method_42052(toFloatFunction2, toFloatFunction3, g, i, toFloatFunction4);
        return Spline.builder(toFloatFunction, toFloatFunction4).method_41295(-1.0f, spline).method_41295(-0.78f, spline2).method_41295(-0.5775f, spline2).method_41294(-0.375f, 0.0f).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42052(I toFloatFunction, I toFloatFunction2, float f, float g, ToFloatFunction<Float> toFloatFunction3) {
        float h = DensityFunctions.method_41546(0.4f);
        float i = DensityFunctions.method_41546(0.56666666f);
        float j = (h + i) / 2.0f;
        Spline.Builder<C, I> builder = Spline.builder(toFloatFunction2, toFloatFunction3);
        builder.method_41294(h, 0.0f);
        if (g > 0.0f) {
            builder.method_41295(j, VanillaTerrainParametersCreator.method_42049(toFloatFunction, g, toFloatFunction3));
        } else {
            builder.method_41294(j, 0.0f);
        }
        if (f > 0.0f) {
            builder.method_41295(1.0f, VanillaTerrainParametersCreator.method_42049(toFloatFunction, f, toFloatFunction3));
        } else {
            builder.method_41294(1.0f, 0.0f);
        }
        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42049(I toFloatFunction, float f, ToFloatFunction<Float> toFloatFunction2) {
        float g = 0.63f * f;
        float h = 0.3f * f;
        return Spline.builder(toFloatFunction, toFloatFunction2).method_41294(-0.01f, g).method_41294(0.01f, h).build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42054(I toFloatFunction, I toFloatFunction2, I toFloatFunction3, float f, boolean bl, ToFloatFunction<Float> toFloatFunction4) {
        Spline spline = Spline.builder(toFloatFunction2, toFloatFunction4).method_41294(-0.2f, 6.3f).method_41294(0.2f, f).build();
        Spline.Builder builder = Spline.builder(toFloatFunction, toFloatFunction4).method_41295(-0.6f, spline).method_41295(-0.5f, Spline.builder(toFloatFunction2, toFloatFunction4).method_41294(-0.05f, 6.3f).method_41294(0.05f, 2.67f).build()).method_41295(-0.35f, spline).method_41295(-0.25f, spline).method_41295(-0.1f, Spline.builder(toFloatFunction2, toFloatFunction4).method_41294(-0.05f, 2.67f).method_41294(0.05f, 6.3f).build()).method_41295(0.03f, spline);
        if (bl) {
            Spline spline2 = Spline.builder(toFloatFunction2, toFloatFunction4).method_41294(0.0f, f).method_41294(0.1f, 0.625f).build();
            Spline spline3 = Spline.builder(toFloatFunction3, toFloatFunction4).method_41294(-0.9f, f).method_41295(-0.69f, spline2).build();
            builder.method_41294(0.35f, f).method_41295(0.45f, spline3).method_41295(0.55f, spline3).method_41294(0.62f, f);
        } else {
            Spline spline2 = Spline.builder(toFloatFunction3, toFloatFunction4).method_41295(-0.7f, spline).method_41294(-0.15f, 1.37f).build();
            Spline spline3 = Spline.builder(toFloatFunction3, toFloatFunction4).method_41295(0.45f, spline).method_41294(0.7f, 1.56f).build();
            builder.method_41295(0.05f, spline3).method_41295(0.4f, spline3).method_41295(0.45f, spline2).method_41295(0.55f, spline2).method_41294(0.58f, f);
        }
        return builder.build();
    }

    private static float method_42047(float f, float g, float h, float i) {
        return (g - f) / (i - h);
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42050(I toFloatFunction, float f, boolean bl, ToFloatFunction<Float> toFloatFunction2) {
        Spline.Builder builder = Spline.builder(toFloatFunction, toFloatFunction2);
        float g = -0.7f;
        float h = -1.0f;
        float i = VanillaTerrainParametersCreator.method_42046(-1.0f, f, -0.7f);
        float j = 1.0f;
        float k = VanillaTerrainParametersCreator.method_42046(1.0f, f, -0.7f);
        float l = VanillaTerrainParametersCreator.method_42045(f);
        float m = -0.65f;
        if (-0.65f < l && l < 1.0f) {
            float n = VanillaTerrainParametersCreator.method_42046(-0.65f, f, -0.7f);
            float o = -0.75f;
            float p = VanillaTerrainParametersCreator.method_42046(-0.75f, f, -0.7f);
            float q = VanillaTerrainParametersCreator.method_42047(i, p, -1.0f, -0.75f);
            builder.add(-1.0f, i, q);
            builder.method_41294(-0.75f, p);
            builder.method_41294(-0.65f, n);
            float r = VanillaTerrainParametersCreator.method_42046(l, f, -0.7f);
            float s = VanillaTerrainParametersCreator.method_42047(r, k, l, 1.0f);
            float t = 0.01f;
            builder.method_41294(l - 0.01f, r);
            builder.add(l, r, s);
            builder.add(1.0f, k, s);
        } else {
            float n = VanillaTerrainParametersCreator.method_42047(i, k, -1.0f, 1.0f);
            if (bl) {
                builder.method_41294(-1.0f, Math.max(0.2f, i));
                builder.add(0.0f, MathHelper.lerp(0.5f, i, k), n);
            } else {
                builder.add(-1.0f, i, n);
            }
            builder.add(1.0f, k, n);
        }
        return builder.build();
    }

    private static float method_42046(float f, float g, float h) {
        float i = 1.17f;
        float j = 0.46082947f;
        float k = 1.0f - (1.0f - g) * 0.5f;
        float l = 0.5f * (1.0f - g);
        float m = (f + 1.17f) * 0.46082947f;
        float n = m * k - l;
        if (f < h) {
            return Math.max(n, -0.2222f);
        }
        return Math.max(n, 0.0f);
    }

    private static float method_42045(float f) {
        float g = 1.17f;
        float h = 0.46082947f;
        float i = 1.0f - (1.0f - f) * 0.5f;
        float j = 0.5f * (1.0f - f);
        return j / (0.46082947f * i) - 1.17f;
    }

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42051(I toFloatFunction, I toFloatFunction2, float f, float g, float h, float i, float j, float k, boolean bl, boolean bl2, ToFloatFunction<Float> toFloatFunction3) {
        float l = 0.6f;
        float m = 0.5f;
        float n = 0.5f;
        Spline<C, I> spline = VanillaTerrainParametersCreator.method_42050(toFloatFunction2, MathHelper.lerp(i, 0.6f, 1.5f), bl2, toFloatFunction3);
        Spline<C, I> spline2 = VanillaTerrainParametersCreator.method_42050(toFloatFunction2, MathHelper.lerp(i, 0.6f, 1.0f), bl2, toFloatFunction3);
        Spline<C, I> spline3 = VanillaTerrainParametersCreator.method_42050(toFloatFunction2, i, bl2, toFloatFunction3);
        Spline<C, I> spline4 = VanillaTerrainParametersCreator.method_42048(toFloatFunction2, f - 0.15f, 0.5f * i, MathHelper.lerp(0.5f, 0.5f, 0.5f) * i, 0.5f * i, 0.6f * i, 0.5f, toFloatFunction3);
        Spline<C, I> spline5 = VanillaTerrainParametersCreator.method_42048(toFloatFunction2, f, j * i, g * i, 0.5f * i, 0.6f * i, 0.5f, toFloatFunction3);
        Spline<C, I> spline6 = VanillaTerrainParametersCreator.method_42048(toFloatFunction2, f, j, j, g, h, 0.5f, toFloatFunction3);
        Spline<C, I> spline7 = VanillaTerrainParametersCreator.method_42048(toFloatFunction2, f, j, j, g, h, 0.5f, toFloatFunction3);
        Spline spline8 = Spline.builder(toFloatFunction2, toFloatFunction3).method_41294(-1.0f, f).method_41295(-0.4f, spline6).method_41294(0.0f, h + 0.07f).build();
        Spline<C, I> spline9 = VanillaTerrainParametersCreator.method_42048(toFloatFunction2, -0.02f, k, k, g, h, 0.0f, toFloatFunction3);
        Spline.Builder<C, I> builder = Spline.builder(toFloatFunction, toFloatFunction3).method_41295(-0.85f, spline).method_41295(-0.7f, spline2).method_41295(-0.4f, spline3).method_41295(-0.35f, spline4).method_41295(-0.1f, spline5).method_41295(0.2f, spline6);
        if (bl) {
            builder.method_41295(0.4f, spline7).method_41295(0.45f, spline8).method_41295(0.55f, spline8).method_41295(0.58f, spline7);
        }
        builder.method_41295(0.7f, spline9);
        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> Spline<C, I> method_42048(I toFloatFunction, float f, float g, float h, float i, float j, float k, ToFloatFunction<Float> toFloatFunction2) {
        float l = Math.max(0.5f * (g - f), k);
        float m = 5.0f * (h - g);
        return Spline.builder(toFloatFunction, toFloatFunction2).add(-1.0f, f, l).add(-0.4f, g, Math.min(l, m)).add(0.0f, h, m).add(0.4f, i, 2.0f * (i - h)).add(1.0f, j, 0.7f * (j - i)).build();
    }
}

