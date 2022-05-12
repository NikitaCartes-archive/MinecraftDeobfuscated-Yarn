package net.minecraft.world.gen.noise;

import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class BuiltinNoiseParameters {
	public static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> init(Registry<DoublePerlinNoiseSampler.NoiseParameters> registry) {
		register(registry, 0, NoiseParametersKeys.TEMPERATURE, NoiseParametersKeys.VEGETATION, NoiseParametersKeys.CONTINENTALNESS, NoiseParametersKeys.EROSION);
		register(
			registry,
			-2,
			NoiseParametersKeys.TEMPERATURE_LARGE,
			NoiseParametersKeys.VEGETATION_LARGE,
			NoiseParametersKeys.CONTINENTALNESS_LARGE,
			NoiseParametersKeys.EROSION_LARGE
		);
		register(registry, NoiseParametersKeys.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
		register(registry, NoiseParametersKeys.OFFSET, -3, 1.0, 1.0, 1.0, 0.0);
		register(registry, NoiseParametersKeys.AQUIFER_BARRIER, -3, 1.0);
		register(registry, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
		register(registry, NoiseParametersKeys.AQUIFER_LAVA, -1, 1.0);
		register(registry, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);
		register(registry, NoiseParametersKeys.PILLAR, -7, 1.0, 1.0);
		register(registry, NoiseParametersKeys.PILLAR_RARENESS, -8, 1.0);
		register(registry, NoiseParametersKeys.PILLAR_THICKNESS, -8, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_2D, -7, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_2D_ELEVATION, -8, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_2D_MODULATOR, -11, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_2D_THICKNESS, -11, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_3D_1, -7, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_3D_2, -7, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_3D_RARITY, -11, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_3D_THICKNESS, -8, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_ROUGHNESS, -5, 1.0);
		register(registry, NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0);
		register(registry, NoiseParametersKeys.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
		register(registry, NoiseParametersKeys.CAVE_LAYER, -8, 1.0);
		register(registry, NoiseParametersKeys.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
		register(registry, NoiseParametersKeys.ORE_VEININESS, -8, 1.0);
		register(registry, NoiseParametersKeys.ORE_VEIN_A, -7, 1.0);
		register(registry, NoiseParametersKeys.ORE_VEIN_B, -7, 1.0);
		register(registry, NoiseParametersKeys.ORE_GAP, -5, 1.0);
		register(registry, NoiseParametersKeys.NOODLE, -8, 1.0);
		register(registry, NoiseParametersKeys.NOODLE_THICKNESS, -8, 1.0);
		register(registry, NoiseParametersKeys.NOODLE_RIDGE_A, -7, 1.0);
		register(registry, NoiseParametersKeys.NOODLE_RIDGE_B, -7, 1.0);
		register(registry, NoiseParametersKeys.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.SURFACE, -6, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.SURFACE_SECONDARY, -6, 1.0, 1.0, 0.0, 1.0);
		register(registry, NoiseParametersKeys.CLAY_BANDS_OFFSET, -8, 1.0);
		register(registry, NoiseParametersKeys.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.BADLANDS_PILLAR_ROOF, -8, 1.0);
		register(registry, NoiseParametersKeys.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.ICEBERG_PILLAR_ROOF, -3, 1.0);
		register(registry, NoiseParametersKeys.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.SURFACE_SWAMP, -2, 1.0);
		register(registry, NoiseParametersKeys.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.POWDER_SNOW, -6, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.ICE, -4, 1.0, 1.0, 1.0, 1.0);
		register(registry, NoiseParametersKeys.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(registry, NoiseParametersKeys.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(registry, NoiseParametersKeys.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(registry, NoiseParametersKeys.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
		register(registry, NoiseParametersKeys.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
		return register(registry, NoiseParametersKeys.NETHER_STATE_SELECTOR, -4, 1.0);
	}

	private static void register(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		int i,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey2,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey3,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey4
	) {
		register(registry, registryKey, -10 + i, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
		register(registry, registryKey2, -8 + i, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
		register(registry, registryKey3, -9 + i, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		register(registry, registryKey4, -9 + i, 1.0, 1.0, 0.0, 1.0, 1.0);
	}

	private static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> register(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey, int i, double d, double... ds
	) {
		return BuiltinRegistries.add(registry, registryKey, new DoublePerlinNoiseSampler.NoiseParameters(i, d, ds));
	}
}
