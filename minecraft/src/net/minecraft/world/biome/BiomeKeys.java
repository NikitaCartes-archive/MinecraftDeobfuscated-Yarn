package net.minecraft.world.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public abstract class BiomeKeys {
	public static final RegistryKey<Biome> THE_VOID = register("the_void");
	public static final RegistryKey<Biome> PLAINS = register("plains");
	public static final RegistryKey<Biome> SUNFLOWER_PLAINS = register("sunflower_plains");
	public static final RegistryKey<Biome> SNOWY_PLAINS = register("snowy_plains");
	public static final RegistryKey<Biome> ICE_SPIKES = register("ice_spikes");
	public static final RegistryKey<Biome> DESERT = register("desert");
	public static final RegistryKey<Biome> SWAMP = register("swamp");
	public static final RegistryKey<Biome> MANGROVE_SWAMP = register("mangrove_swamp");
	public static final RegistryKey<Biome> FOREST = register("forest");
	public static final RegistryKey<Biome> FLOWER_FOREST = register("flower_forest");
	public static final RegistryKey<Biome> BIRCH_FOREST = register("birch_forest");
	public static final RegistryKey<Biome> DARK_FOREST = register("dark_forest");
	public static final RegistryKey<Biome> OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
	public static final RegistryKey<Biome> OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
	public static final RegistryKey<Biome> OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
	public static final RegistryKey<Biome> TAIGA = register("taiga");
	public static final RegistryKey<Biome> SNOWY_TAIGA = register("snowy_taiga");
	public static final RegistryKey<Biome> SAVANNA = register("savanna");
	public static final RegistryKey<Biome> SAVANNA_PLATEAU = register("savanna_plateau");
	public static final RegistryKey<Biome> WINDSWEPT_HILLS = register("windswept_hills");
	public static final RegistryKey<Biome> WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
	public static final RegistryKey<Biome> WINDSWEPT_FOREST = register("windswept_forest");
	public static final RegistryKey<Biome> WINDSWEPT_SAVANNA = register("windswept_savanna");
	public static final RegistryKey<Biome> JUNGLE = register("jungle");
	public static final RegistryKey<Biome> SPARSE_JUNGLE = register("sparse_jungle");
	public static final RegistryKey<Biome> BAMBOO_JUNGLE = register("bamboo_jungle");
	public static final RegistryKey<Biome> BADLANDS = register("badlands");
	public static final RegistryKey<Biome> ERODED_BADLANDS = register("eroded_badlands");
	public static final RegistryKey<Biome> WOODED_BADLANDS = register("wooded_badlands");
	public static final RegistryKey<Biome> MEADOW = register("meadow");
	public static final RegistryKey<Biome> CHERRY_GROVE = register("cherry_grove");
	public static final RegistryKey<Biome> GROVE = register("grove");
	public static final RegistryKey<Biome> SNOWY_SLOPES = register("snowy_slopes");
	public static final RegistryKey<Biome> FROZEN_PEAKS = register("frozen_peaks");
	public static final RegistryKey<Biome> JAGGED_PEAKS = register("jagged_peaks");
	public static final RegistryKey<Biome> STONY_PEAKS = register("stony_peaks");
	public static final RegistryKey<Biome> RIVER = register("river");
	public static final RegistryKey<Biome> FROZEN_RIVER = register("frozen_river");
	public static final RegistryKey<Biome> BEACH = register("beach");
	public static final RegistryKey<Biome> SNOWY_BEACH = register("snowy_beach");
	public static final RegistryKey<Biome> STONY_SHORE = register("stony_shore");
	public static final RegistryKey<Biome> WARM_OCEAN = register("warm_ocean");
	public static final RegistryKey<Biome> LUKEWARM_OCEAN = register("lukewarm_ocean");
	public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
	public static final RegistryKey<Biome> OCEAN = register("ocean");
	public static final RegistryKey<Biome> DEEP_OCEAN = register("deep_ocean");
	public static final RegistryKey<Biome> COLD_OCEAN = register("cold_ocean");
	public static final RegistryKey<Biome> DEEP_COLD_OCEAN = register("deep_cold_ocean");
	public static final RegistryKey<Biome> FROZEN_OCEAN = register("frozen_ocean");
	public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
	public static final RegistryKey<Biome> MUSHROOM_FIELDS = register("mushroom_fields");
	public static final RegistryKey<Biome> DRIPSTONE_CAVES = register("dripstone_caves");
	public static final RegistryKey<Biome> LUSH_CAVES = register("lush_caves");
	public static final RegistryKey<Biome> DEEP_DARK = register("deep_dark");
	public static final RegistryKey<Biome> NETHER_WASTES = register("nether_wastes");
	public static final RegistryKey<Biome> WARPED_FOREST = register("warped_forest");
	public static final RegistryKey<Biome> CRIMSON_FOREST = register("crimson_forest");
	public static final RegistryKey<Biome> SOUL_SAND_VALLEY = register("soul_sand_valley");
	public static final RegistryKey<Biome> BASALT_DELTAS = register("basalt_deltas");
	public static final RegistryKey<Biome> THE_END = register("the_end");
	public static final RegistryKey<Biome> END_HIGHLANDS = register("end_highlands");
	public static final RegistryKey<Biome> END_MIDLANDS = register("end_midlands");
	public static final RegistryKey<Biome> SMALL_END_ISLANDS = register("small_end_islands");
	public static final RegistryKey<Biome> END_BARRENS = register("end_barrens");

	private static RegistryKey<Biome> register(String name) {
		return RegistryKey.of(RegistryKeys.BIOME, new Identifier(name));
	}
}
