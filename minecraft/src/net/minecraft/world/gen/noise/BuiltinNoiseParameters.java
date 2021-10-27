package net.minecraft.world.gen.noise;

import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;

public class BuiltinNoiseParameters {
	public static DoublePerlinNoiseSampler.NoiseParameters init() {
		register(NoiseParametersKeys.TEMPERATURE, -10, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
		register(NoiseParametersKeys.VEGETATION, -8, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
		register(NoiseParametersKeys.CONTINENTALNESS, -9, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.EROSION, -9, 1.0, 1.0, 0.0, 1.0, 1.0);
		register(NoiseParametersKeys.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
		register(NoiseParametersKeys.OFFSET, -3, 1.0, 1.0, 1.0, 0.0);
		register(NoiseParametersKeys.AQUIFER_BARRIER, -3, 1.0);
		register(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
		register(NoiseParametersKeys.AQUIFER_LAVA, -1, 1.0);
		register(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);
		register(NoiseParametersKeys.PILLAR, -7, 1.0, 1.0);
		register(NoiseParametersKeys.PILLAR_RARENESS, -8, 1.0);
		register(NoiseParametersKeys.PILLAR_THICKNESS, -8, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_2D, -7, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION, -8, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR, -11, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS, -11, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_3D_1, -7, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_3D_2, -7, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_3D_RARITY, -11, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS, -8, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_ROUGHNESS, -5, 1.0);
		register(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0);
		register(NoiseParametersKeys.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
		register(NoiseParametersKeys.CAVE_LAYER, -8, 1.0);
		register(NoiseParametersKeys.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
		register(NoiseParametersKeys.ORE_VEININESS, -8, 1.0);
		register(NoiseParametersKeys.ORE_VEIN_A, -7, 1.0);
		register(NoiseParametersKeys.ORE_VEIN_B, -7, 1.0);
		register(NoiseParametersKeys.ORE_GAP, -5, 1.0);
		register(NoiseParametersKeys.NOODLE, -8, 1.0);
		register(NoiseParametersKeys.NOODLE_THICKNESS, -8, 1.0);
		register(NoiseParametersKeys.NOODLE_RIDGE_A, -7, 1.0);
		register(NoiseParametersKeys.NOODLE_RIDGE_B, -7, 1.0);
		register(NoiseParametersKeys.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.SURFACE, -6, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.CLAY_BANDS_OFFSET, -8, 1.0);
		register(NoiseParametersKeys.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.BADLANDS_PILLAR_ROOF, -8, 1.0);
		register(NoiseParametersKeys.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.ICEBERG_PILLAR_ROOF, -3, 1.0);
		register(NoiseParametersKeys.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.SURFACE_SWAMP, -2, 1.0);
		register(NoiseParametersKeys.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.POWDER_SNOW_UNDER, -6, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.POWDER_SNOW_SURFACE, -6, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.ICE, -4, 1.0, 1.0, 1.0, 1.0);
		register(NoiseParametersKeys.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(NoiseParametersKeys.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(NoiseParametersKeys.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(NoiseParametersKeys.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
		register(NoiseParametersKeys.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
		register(NoiseParametersKeys.NETHER_STATE_SELECTOR, -4, 1.0);
		return (DoublePerlinNoiseSampler.NoiseParameters)BuiltinRegistries.NOISE_PARAMETERS.iterator().next();
	}

	private static void register(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, int i, double d, double... ds) {
		BuiltinRegistries.add(BuiltinRegistries.NOISE_PARAMETERS, registryKey, new DoublePerlinNoiseSampler.NoiseParameters(i, d, ds));
	}
}
