package net.minecraft.world.biome.source.util;

import net.minecraft.util.math.Spline;

public class VanillaTerrainParametersCreator {
	public static VanillaTerrainParameters createSurfaceParameters(boolean bl) {
		return VanillaTerrainParameters.method_39457(bl);
	}

	public static VanillaTerrainParameters method_39923() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters method_39924() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters createUndergroundParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters createIslandParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(1.0F), Spline.fixedFloatFunction(0.0F));
	}
}
