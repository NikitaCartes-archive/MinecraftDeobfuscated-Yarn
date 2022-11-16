package net.minecraft.world.gen.noise;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomSplitter;

public class NoiseParametersKeys {
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE = of("temperature");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION = of("vegetation");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS = of("continentalness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION = of("erosion");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE_LARGE = of("temperature_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION_LARGE = of("vegetation_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS_LARGE = of("continentalness_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION_LARGE = of("erosion_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> RIDGE = of("ridge");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> OFFSET = of("offset");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_BARRIER = of("aquifer_barrier");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = of("aquifer_fluid_level_floodedness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_LAVA = of("aquifer_lava");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = of("aquifer_fluid_level_spread");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR = of("pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_RARENESS = of("pillar_rareness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_THICKNESS = of("pillar_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D = of("spaghetti_2d");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_ELEVATION = of("spaghetti_2d_elevation");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_MODULATOR = of("spaghetti_2d_modulator");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_THICKNESS = of("spaghetti_2d_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_1 = of("spaghetti_3d_1");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_2 = of("spaghetti_3d_2");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_RARITY = of("spaghetti_3d_rarity");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_THICKNESS = of("spaghetti_3d_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS = of("spaghetti_roughness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = of("spaghetti_roughness_modulator");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_ENTRANCE = of("cave_entrance");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_LAYER = of("cave_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_CHEESE = of("cave_cheese");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEININESS = of("ore_veininess");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_A = of("ore_vein_a");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_B = of("ore_vein_b");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_GAP = of("ore_gap");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE = of("noodle");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_THICKNESS = of("noodle_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_A = of("noodle_ridge_a");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_B = of("noodle_ridge_b");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> JAGGED = of("jagged");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE = of("surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SECONDARY = of("surface_secondary");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CLAY_BANDS_OFFSET = of("clay_bands_offset");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR = of("badlands_pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR_ROOF = of("badlands_pillar_roof");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_SURFACE = of("badlands_surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR = of("iceberg_pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR_ROOF = of("iceberg_pillar_roof");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_SURFACE = of("iceberg_surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SWAMP = of("surface_swamp");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CALCITE = of("calcite");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL = of("gravel");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> POWDER_SNOW = of("powder_snow");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PACKED_ICE = of("packed_ice");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICE = of("ice");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SOUL_SAND_LAYER = of("soul_sand_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL_LAYER = of("gravel_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PATCH = of("patch");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHERRACK = of("netherrack");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_WART = of("nether_wart");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_STATE_SELECTOR = of("nether_state_selector");

	private static RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> of(String id) {
		return RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, new Identifier(id));
	}

	public static DoublePerlinNoiseSampler createNoiseSampler(
		RegistryEntryLookup<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersLookup,
		RandomSplitter splitter,
		RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> key
	) {
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noiseParametersLookup.getOrThrow(key);
		return DoublePerlinNoiseSampler.create(splitter.split(((RegistryKey)registryEntry.getKey().orElseThrow()).getValue()), registryEntry.value());
	}
}
