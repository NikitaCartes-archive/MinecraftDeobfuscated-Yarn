/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryKey;

public class NoiseParametersKeys {
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE = NoiseParametersKeys.of("temperature");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION = NoiseParametersKeys.of("vegetation");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS = NoiseParametersKeys.of("continentalness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION = NoiseParametersKeys.of("erosion");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE_LARGE = NoiseParametersKeys.of("temperature_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION_LARGE = NoiseParametersKeys.of("vegetation_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS_LARGE = NoiseParametersKeys.of("continentalness_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION_LARGE = NoiseParametersKeys.of("erosion_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> RIDGE = NoiseParametersKeys.of("ridge");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> OFFSET = NoiseParametersKeys.of("offset");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_BARRIER = NoiseParametersKeys.of("aquifer_barrier");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = NoiseParametersKeys.of("aquifer_fluid_level_floodedness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_LAVA = NoiseParametersKeys.of("aquifer_lava");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = NoiseParametersKeys.of("aquifer_fluid_level_spread");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR = NoiseParametersKeys.of("pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_RARENESS = NoiseParametersKeys.of("pillar_rareness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_THICKNESS = NoiseParametersKeys.of("pillar_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D = NoiseParametersKeys.of("spaghetti_2d");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_ELEVATION = NoiseParametersKeys.of("spaghetti_2d_elevation");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_MODULATOR = NoiseParametersKeys.of("spaghetti_2d_modulator");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_THICKNESS = NoiseParametersKeys.of("spaghetti_2d_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_1 = NoiseParametersKeys.of("spaghetti_3d_1");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_2 = NoiseParametersKeys.of("spaghetti_3d_2");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_RARITY = NoiseParametersKeys.of("spaghetti_3d_rarity");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_THICKNESS = NoiseParametersKeys.of("spaghetti_3d_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS = NoiseParametersKeys.of("spaghetti_roughness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = NoiseParametersKeys.of("spaghetti_roughness_modulator");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_ENTRANCE = NoiseParametersKeys.of("cave_entrance");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_LAYER = NoiseParametersKeys.of("cave_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_CHEESE = NoiseParametersKeys.of("cave_cheese");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEININESS = NoiseParametersKeys.of("ore_veininess");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_A = NoiseParametersKeys.of("ore_vein_a");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_B = NoiseParametersKeys.of("ore_vein_b");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_GAP = NoiseParametersKeys.of("ore_gap");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE = NoiseParametersKeys.of("noodle");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_THICKNESS = NoiseParametersKeys.of("noodle_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_A = NoiseParametersKeys.of("noodle_ridge_a");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_B = NoiseParametersKeys.of("noodle_ridge_b");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> JAGGED = NoiseParametersKeys.of("jagged");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE = NoiseParametersKeys.of("surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SECONDARY = NoiseParametersKeys.of("surface_secondary");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CLAY_BANDS_OFFSET = NoiseParametersKeys.of("clay_bands_offset");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR = NoiseParametersKeys.of("badlands_pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR_ROOF = NoiseParametersKeys.of("badlands_pillar_roof");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_SURFACE = NoiseParametersKeys.of("badlands_surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR = NoiseParametersKeys.of("iceberg_pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR_ROOF = NoiseParametersKeys.of("iceberg_pillar_roof");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_SURFACE = NoiseParametersKeys.of("iceberg_surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SWAMP = NoiseParametersKeys.of("surface_swamp");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CALCITE = NoiseParametersKeys.of("calcite");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL = NoiseParametersKeys.of("gravel");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> POWDER_SNOW = NoiseParametersKeys.of("powder_snow");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PACKED_ICE = NoiseParametersKeys.of("packed_ice");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICE = NoiseParametersKeys.of("ice");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SOUL_SAND_LAYER = NoiseParametersKeys.of("soul_sand_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL_LAYER = NoiseParametersKeys.of("gravel_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PATCH = NoiseParametersKeys.of("patch");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHERRACK = NoiseParametersKeys.of("netherrack");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_WART = NoiseParametersKeys.of("nether_wart");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_STATE_SELECTOR = NoiseParametersKeys.of("nether_state_selector");

    private static RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> of(String id) {
        return RegistryKey.of(Registry.NOISE_KEY, new Identifier(id));
    }

    public static DoublePerlinNoiseSampler createNoiseSampler(RegistryEntryLookup<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersLookup, RandomSplitter splitter, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> key) {
        RegistryEntry.Reference<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noiseParametersLookup.getOrThrow(key);
        return DoublePerlinNoiseSampler.create(splitter.split(registryEntry.getKey().orElseThrow().getValue()), (DoublePerlinNoiseSampler.NoiseParameters)registryEntry.value());
    }
}

