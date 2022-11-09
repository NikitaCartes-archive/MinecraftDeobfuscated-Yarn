package net.minecraft.world.gen.noise;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;

public class BuiltinNoiseParameters {
	@Deprecated
	public static final DoublePerlinNoiseSampler.NoiseParameters OFFSET = new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

	public static void bootstrap(Registerable<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegisterable) {
		register(
			noiseParametersRegisterable,
			0,
			NoiseParametersKeys.TEMPERATURE,
			NoiseParametersKeys.VEGETATION,
			NoiseParametersKeys.CONTINENTALNESS,
			NoiseParametersKeys.EROSION
		);
		register(
			noiseParametersRegisterable,
			-2,
			NoiseParametersKeys.TEMPERATURE_LARGE,
			NoiseParametersKeys.VEGETATION_LARGE,
			NoiseParametersKeys.CONTINENTALNESS_LARGE,
			NoiseParametersKeys.EROSION_LARGE
		);
		register(noiseParametersRegisterable, NoiseParametersKeys.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
		noiseParametersRegisterable.register(NoiseParametersKeys.OFFSET, OFFSET);
		register(noiseParametersRegisterable, NoiseParametersKeys.AQUIFER_BARRIER, -3, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.AQUIFER_LAVA, -1, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.PILLAR, -7, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.PILLAR_RARENESS, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.PILLAR_THICKNESS, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_2D, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_2D_ELEVATION, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_2D_MODULATOR, -11, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_2D_THICKNESS, -11, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_3D_1, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_3D_2, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_3D_RARITY, -11, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_3D_THICKNESS, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_ROUGHNESS, -5, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.CAVE_LAYER, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ORE_VEININESS, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ORE_VEIN_A, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ORE_VEIN_B, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ORE_GAP, -5, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.NOODLE, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.NOODLE_THICKNESS, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.NOODLE_RIDGE_A, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.NOODLE_RIDGE_B, -7, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SURFACE, -6, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SURFACE_SECONDARY, -6, 1.0, 1.0, 0.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.CLAY_BANDS_OFFSET, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.BADLANDS_PILLAR_ROOF, -8, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ICEBERG_PILLAR_ROOF, -3, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SURFACE_SWAMP, -2, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.POWDER_SNOW, -6, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.ICE, -4, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, NoiseParametersKeys.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(noiseParametersRegisterable, NoiseParametersKeys.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(noiseParametersRegisterable, NoiseParametersKeys.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
		register(noiseParametersRegisterable, NoiseParametersKeys.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
		register(noiseParametersRegisterable, NoiseParametersKeys.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
		register(noiseParametersRegisterable, NoiseParametersKeys.NETHER_STATE_SELECTOR, -4, 1.0);
	}

	private static void register(
		Registerable<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegisterable,
		int octaveOffset,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> temperatureKey,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> vegetationKey,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> continentalnessKey,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> erosionKey
	) {
		register(noiseParametersRegisterable, temperatureKey, -10 + octaveOffset, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
		register(noiseParametersRegisterable, vegetationKey, -8 + octaveOffset, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
		register(noiseParametersRegisterable, continentalnessKey, -9 + octaveOffset, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
		register(noiseParametersRegisterable, erosionKey, -9 + octaveOffset, 1.0, 1.0, 0.0, 1.0, 1.0);
	}

	private static void register(
		Registerable<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegisterable,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> key,
		int firstOctave,
		double firstAmplitude,
		double... amplitudes
	) {
		noiseParametersRegisterable.register(key, new DoublePerlinNoiseSampler.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
	}
}
