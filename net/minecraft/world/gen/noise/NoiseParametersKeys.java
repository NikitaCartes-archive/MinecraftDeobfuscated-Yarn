/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class NoiseParametersKeys {
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE = NoiseParametersKeys.register("temperature");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION = NoiseParametersKeys.register("vegetation");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS = NoiseParametersKeys.register("continentalness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION = NoiseParametersKeys.register("erosion");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE_LARGE = NoiseParametersKeys.register("temperature_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION_LARGE = NoiseParametersKeys.register("vegetation_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS_LARGE = NoiseParametersKeys.register("continentalness_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION_LARGE = NoiseParametersKeys.register("erosion_large");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> RIDGE = NoiseParametersKeys.register("ridge");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> OFFSET = NoiseParametersKeys.register("offset");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_BARRIER = NoiseParametersKeys.register("aquifer_barrier");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = NoiseParametersKeys.register("aquifer_fluid_level_floodedness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_LAVA = NoiseParametersKeys.register("aquifer_lava");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = NoiseParametersKeys.register("aquifer_fluid_level_spread");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR = NoiseParametersKeys.register("pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_RARENESS = NoiseParametersKeys.register("pillar_rareness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_THICKNESS = NoiseParametersKeys.register("pillar_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D = NoiseParametersKeys.register("spaghetti_2d");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_ELEVATION = NoiseParametersKeys.register("spaghetti_2d_elevation");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_MODULATOR = NoiseParametersKeys.register("spaghetti_2d_modulator");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_THICKNESS = NoiseParametersKeys.register("spaghetti_2d_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_1 = NoiseParametersKeys.register("spaghetti_3d_1");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_2 = NoiseParametersKeys.register("spaghetti_3d_2");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_RARITY = NoiseParametersKeys.register("spaghetti_3d_rarity");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_THICKNESS = NoiseParametersKeys.register("spaghetti_3d_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS = NoiseParametersKeys.register("spaghetti_roughness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = NoiseParametersKeys.register("spaghetti_roughness_modulator");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_ENTRANCE = NoiseParametersKeys.register("cave_entrance");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_LAYER = NoiseParametersKeys.register("cave_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_CHEESE = NoiseParametersKeys.register("cave_cheese");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEININESS = NoiseParametersKeys.register("ore_veininess");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_A = NoiseParametersKeys.register("ore_vein_a");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_B = NoiseParametersKeys.register("ore_vein_b");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_GAP = NoiseParametersKeys.register("ore_gap");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE = NoiseParametersKeys.register("noodle");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_THICKNESS = NoiseParametersKeys.register("noodle_thickness");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_A = NoiseParametersKeys.register("noodle_ridge_a");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_B = NoiseParametersKeys.register("noodle_ridge_b");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> JAGGED = NoiseParametersKeys.register("jagged");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE = NoiseParametersKeys.register("surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SECONDARY = NoiseParametersKeys.register("surface_secondary");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CLAY_BANDS_OFFSET = NoiseParametersKeys.register("clay_bands_offset");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR = NoiseParametersKeys.register("badlands_pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR_ROOF = NoiseParametersKeys.register("badlands_pillar_roof");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_SURFACE = NoiseParametersKeys.register("badlands_surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR = NoiseParametersKeys.register("iceberg_pillar");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR_ROOF = NoiseParametersKeys.register("iceberg_pillar_roof");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_SURFACE = NoiseParametersKeys.register("iceberg_surface");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SWAMP = NoiseParametersKeys.register("surface_swamp");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CALCITE = NoiseParametersKeys.register("calcite");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL = NoiseParametersKeys.register("gravel");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> POWDER_SNOW = NoiseParametersKeys.register("powder_snow");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PACKED_ICE = NoiseParametersKeys.register("packed_ice");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICE = NoiseParametersKeys.register("ice");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SOUL_SAND_LAYER = NoiseParametersKeys.register("soul_sand_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL_LAYER = NoiseParametersKeys.register("gravel_layer");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PATCH = NoiseParametersKeys.register("patch");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHERRACK = NoiseParametersKeys.register("netherrack");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_WART = NoiseParametersKeys.register("nether_wart");
    public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_STATE_SELECTOR = NoiseParametersKeys.register("nether_state_selector");

    private static RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> register(String id) {
        return RegistryKey.of(Registry.NOISE_KEY, new Identifier(id));
    }

    public static DoublePerlinNoiseSampler createNoiseSampler(Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, RandomSplitter randomSplitter, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey) {
        RegistryEntry.Reference<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = registry.entryOf(registryKey);
        return DoublePerlinNoiseSampler.create(randomSplitter.split(registryEntry.getKey().orElseThrow().getValue()), (DoublePerlinNoiseSampler.NoiseParameters)registryEntry.value());
    }
}

