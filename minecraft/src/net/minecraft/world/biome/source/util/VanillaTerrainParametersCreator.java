package net.minecraft.world.biome.source.util;

import net.minecraft.util.math.Spline;

public class VanillaTerrainParametersCreator {
	public static VanillaTerrainParameters createSurfaceParameters() {
		return VanillaTerrainParameters.method_39457();
	}

	public static VanillaTerrainParameters createUndergroundParameters() {
		return new VanillaTerrainParameters(Spline.method_39427(0.0F), Spline.method_39427(0.0F), Spline.method_39427(0.0F));
	}

	public static VanillaTerrainParameters createIslandParameters() {
		return new VanillaTerrainParameters(Spline.method_39427(0.0F), Spline.method_39427(1.0F), Spline.method_39427(0.0F));
	}
}
