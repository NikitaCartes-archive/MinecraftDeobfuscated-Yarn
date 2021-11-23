package net.minecraft.world.biome.source.util;

import net.minecraft.util.math.Spline;

public class VanillaTerrainParametersCreator {
	public static VanillaTerrainParameters createSurfaceParameters(boolean amplified) {
		return VanillaTerrainParameters.createSurfaceParameters(amplified);
	}

	public static VanillaTerrainParameters createCavesParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters createFloatingIslandsParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters createNetherParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(0.0F));
	}

	public static VanillaTerrainParameters createEndParameters() {
		return new VanillaTerrainParameters(Spline.fixedFloatFunction(0.0F), Spline.fixedFloatFunction(1.0F), Spline.fixedFloatFunction(0.0F));
	}
}
