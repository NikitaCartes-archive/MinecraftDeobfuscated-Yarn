/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import net.minecraft.util.math.Spline;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;

public class VanillaTerrainParametersCreator {
    public static VanillaTerrainParameters createSurfaceParameters(boolean bl) {
        return VanillaTerrainParameters.method_39457(bl);
    }

    public static VanillaTerrainParameters method_39923() {
        return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f));
    }

    public static VanillaTerrainParameters method_39924() {
        return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f));
    }

    public static VanillaTerrainParameters createUndergroundParameters() {
        return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(0.0f));
    }

    public static VanillaTerrainParameters createIslandParameters() {
        return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0f), Spline.fixedFloatFunction(1.0f), Spline.fixedFloatFunction(0.0f));
    }
}

