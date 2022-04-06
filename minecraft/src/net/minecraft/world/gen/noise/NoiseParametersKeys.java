package net.minecraft.world.gen.noise;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomDeriver;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class NoiseParametersKeys {
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE = register("temperature");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION = register("vegetation");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS = register("continentalness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION = register("erosion");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE_LARGE = register("temperature_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION_LARGE = register("vegetation_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS_LARGE = register("continentalness_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION_LARGE = register("erosion_large");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> RIDGE = register("ridge");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> OFFSET = register("offset");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_BARRIER = register("aquifer_barrier");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = register("aquifer_fluid_level_floodedness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_LAVA = register("aquifer_lava");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = register("aquifer_fluid_level_spread");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR = register("pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_RARENESS = register("pillar_rareness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_THICKNESS = register("pillar_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D = register("spaghetti_2d");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_ELEVATION = register("spaghetti_2d_elevation");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_MODULATOR = register("spaghetti_2d_modulator");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_THICKNESS = register("spaghetti_2d_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_1 = register("spaghetti_3d_1");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_2 = register("spaghetti_3d_2");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_RARITY = register("spaghetti_3d_rarity");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_THICKNESS = register("spaghetti_3d_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS = register("spaghetti_roughness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = register("spaghetti_roughness_modulator");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_ENTRANCE = register("cave_entrance");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_LAYER = register("cave_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_CHEESE = register("cave_cheese");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEININESS = register("ore_veininess");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_A = register("ore_vein_a");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_B = register("ore_vein_b");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_GAP = register("ore_gap");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE = register("noodle");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_THICKNESS = register("noodle_thickness");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_A = register("noodle_ridge_a");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_B = register("noodle_ridge_b");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> JAGGED = register("jagged");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE = register("surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SECONDARY = register("surface_secondary");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CLAY_BANDS_OFFSET = register("clay_bands_offset");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR = register("badlands_pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR_ROOF = register("badlands_pillar_roof");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_SURFACE = register("badlands_surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR = register("iceberg_pillar");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR_ROOF = register("iceberg_pillar_roof");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_SURFACE = register("iceberg_surface");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SWAMP = register("surface_swamp");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CALCITE = register("calcite");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL = register("gravel");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> POWDER_SNOW = register("powder_snow");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PACKED_ICE = register("packed_ice");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICE = register("ice");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SOUL_SAND_LAYER = register("soul_sand_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL_LAYER = register("gravel_layer");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PATCH = register("patch");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHERRACK = register("netherrack");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_WART = register("nether_wart");
	public static final RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_STATE_SELECTOR = register("nether_state_selector");

	private static RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> register(String id) {
		return RegistryKey.of(Registry.NOISE_WORLDGEN, new Identifier(id));
	}

	public static DoublePerlinNoiseSampler createNoiseSampler(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, RandomDeriver randomDeriver, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey
	) {
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = registry.entryOf(registryKey);
		return DoublePerlinNoiseSampler.create(randomDeriver.createRandom(((RegistryKey)registryEntry.getKey().orElseThrow()).getValue()), registryEntry.value());
	}
}
