/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.noise.NoiseParametersKeys;

public class BuiltinNoiseParameters {
    public static DoublePerlinNoiseSampler.NoiseParameters init() {
        BuiltinNoiseParameters.register(0, NoiseParametersKeys.TEMPERATURE, NoiseParametersKeys.VEGETATION, NoiseParametersKeys.CONTINENTALNESS, NoiseParametersKeys.EROSION);
        BuiltinNoiseParameters.register(-2, NoiseParametersKeys.TEMPERATURE_LARGE, NoiseParametersKeys.VEGETATION_LARGE, NoiseParametersKeys.CONTINENTALNESS_LARGE, NoiseParametersKeys.EROSION_LARGE);
        BuiltinNoiseParameters.register(NoiseParametersKeys.RIDGE, -7, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.OFFSET, -3, 1.0, 1.0, 1.0, 0.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.AQUIFER_BARRIER, -3, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.AQUIFER_LAVA, -1, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.PILLAR, -7, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.PILLAR_RARENESS, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.PILLAR_THICKNESS, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_2D, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR, -11, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS, -11, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_3D_1, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_3D_2, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_3D_RARITY, -11, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_ROUGHNESS, -5, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.CAVE_ENTRANCE, -7, 0.4, 0.5, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.CAVE_LAYER, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.CAVE_CHEESE, -8, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ORE_VEININESS, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ORE_VEIN_A, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ORE_VEIN_B, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ORE_GAP, -5, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NOODLE, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NOODLE_THICKNESS, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NOODLE_RIDGE_A, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NOODLE_RIDGE_B, -7, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.JAGGED, -16, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SURFACE, -6, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SURFACE_SECONDARY, -6, 1.0, 1.0, 0.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.CLAY_BANDS_OFFSET, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.BADLANDS_PILLAR, -2, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.BADLANDS_PILLAR_ROOF, -8, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.BADLANDS_SURFACE, -6, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ICEBERG_PILLAR, -6, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ICEBERG_PILLAR_ROOF, -3, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ICEBERG_SURFACE, -6, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SURFACE_SWAMP, -2, 1.0, new double[0]);
        BuiltinNoiseParameters.register(NoiseParametersKeys.CALCITE, -9, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.GRAVEL, -8, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.POWDER_SNOW, -6, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.PACKED_ICE, -7, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.ICE, -4, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(NoiseParametersKeys.SOUL_SAND_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        BuiltinNoiseParameters.register(NoiseParametersKeys.GRAVEL_LAYER, -8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        BuiltinNoiseParameters.register(NoiseParametersKeys.PATCH, -5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NETHERRACK, -3, 1.0, 0.0, 0.0, 0.35);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NETHER_WART, -3, 1.0, 0.0, 0.0, 0.9);
        BuiltinNoiseParameters.register(NoiseParametersKeys.NETHER_STATE_SELECTOR, -4, 1.0, new double[0]);
        return (DoublePerlinNoiseSampler.NoiseParameters)BuiltinRegistries.NOISE_PARAMETERS.iterator().next();
    }

    private static void register(int firstOctaveOffset, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> temperature, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> vegetation, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> continentalness, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> erosion) {
        BuiltinNoiseParameters.register(temperature, -10 + firstOctaveOffset, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
        BuiltinNoiseParameters.register(vegetation, -8 + firstOctaveOffset, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
        BuiltinNoiseParameters.register(continentalness, -9 + firstOctaveOffset, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
        BuiltinNoiseParameters.register(erosion, -9 + firstOctaveOffset, 1.0, 1.0, 0.0, 1.0, 1.0);
    }

    private static void register(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, int firstOctave, double firstAmplitude, double ... amplitudes) {
        BuiltinRegistries.add(BuiltinRegistries.NOISE_PARAMETERS, noise, new DoublePerlinNoiseSampler.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }
}

