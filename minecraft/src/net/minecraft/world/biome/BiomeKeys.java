package net.minecraft.world.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public abstract class BiomeKeys {
	public static final RegistryKey<Biome> THE_VOID = keyOf("the_void");
	public static final RegistryKey<Biome> PLAINS = keyOf("plains");
	public static final RegistryKey<Biome> SUNFLOWER_PLAINS = keyOf("sunflower_plains");
	public static final RegistryKey<Biome> SNOWY_PLAINS = keyOf("snowy_plains");
	public static final RegistryKey<Biome> ICE_SPIKES = keyOf("ice_spikes");
	public static final RegistryKey<Biome> DESERT = keyOf("desert");
	public static final RegistryKey<Biome> SWAMP = keyOf("swamp");
	public static final RegistryKey<Biome> MANGROVE_SWAMP = keyOf("mangrove_swamp");
	public static final RegistryKey<Biome> FOREST = keyOf("forest");
	public static final RegistryKey<Biome> FLOWER_FOREST = keyOf("flower_forest");
	public static final RegistryKey<Biome> BIRCH_FOREST = keyOf("birch_forest");
	public static final RegistryKey<Biome> DARK_FOREST = keyOf("dark_forest");
	public static final RegistryKey<Biome> OLD_GROWTH_BIRCH_FOREST = keyOf("old_growth_birch_forest");
	public static final RegistryKey<Biome> OLD_GROWTH_PINE_TAIGA = keyOf("old_growth_pine_taiga");
	public static final RegistryKey<Biome> OLD_GROWTH_SPRUCE_TAIGA = keyOf("old_growth_spruce_taiga");
	public static final RegistryKey<Biome> TAIGA = keyOf("taiga");
	public static final RegistryKey<Biome> SNOWY_TAIGA = keyOf("snowy_taiga");
	public static final RegistryKey<Biome> SAVANNA = keyOf("savanna");
	public static final RegistryKey<Biome> SAVANNA_PLATEAU = keyOf("savanna_plateau");
	public static final RegistryKey<Biome> WINDSWEPT_HILLS = keyOf("windswept_hills");
	public static final RegistryKey<Biome> WINDSWEPT_GRAVELLY_HILLS = keyOf("windswept_gravelly_hills");
	public static final RegistryKey<Biome> WINDSWEPT_FOREST = keyOf("windswept_forest");
	public static final RegistryKey<Biome> WINDSWEPT_SAVANNA = keyOf("windswept_savanna");
	public static final RegistryKey<Biome> JUNGLE = keyOf("jungle");
	public static final RegistryKey<Biome> SPARSE_JUNGLE = keyOf("sparse_jungle");
	public static final RegistryKey<Biome> BAMBOO_JUNGLE = keyOf("bamboo_jungle");
	public static final RegistryKey<Biome> BADLANDS = keyOf("badlands");
	public static final RegistryKey<Biome> ERODED_BADLANDS = keyOf("eroded_badlands");
	public static final RegistryKey<Biome> WOODED_BADLANDS = keyOf("wooded_badlands");
	public static final RegistryKey<Biome> MEADOW = keyOf("meadow");
	public static final RegistryKey<Biome> CHERRY_GROVE = keyOf("cherry_grove");
	public static final RegistryKey<Biome> GROVE = keyOf("grove");
	public static final RegistryKey<Biome> SNOWY_SLOPES = keyOf("snowy_slopes");
	public static final RegistryKey<Biome> FROZEN_PEAKS = keyOf("frozen_peaks");
	public static final RegistryKey<Biome> JAGGED_PEAKS = keyOf("jagged_peaks");
	public static final RegistryKey<Biome> STONY_PEAKS = keyOf("stony_peaks");
	public static final RegistryKey<Biome> RIVER = keyOf("river");
	public static final RegistryKey<Biome> FROZEN_RIVER = keyOf("frozen_river");
	public static final RegistryKey<Biome> BEACH = keyOf("beach");
	public static final RegistryKey<Biome> SNOWY_BEACH = keyOf("snowy_beach");
	public static final RegistryKey<Biome> STONY_SHORE = keyOf("stony_shore");
	public static final RegistryKey<Biome> WARM_OCEAN = keyOf("warm_ocean");
	public static final RegistryKey<Biome> LUKEWARM_OCEAN = keyOf("lukewarm_ocean");
	public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = keyOf("deep_lukewarm_ocean");
	public static final RegistryKey<Biome> OCEAN = keyOf("ocean");
	public static final RegistryKey<Biome> DEEP_OCEAN = keyOf("deep_ocean");
	public static final RegistryKey<Biome> COLD_OCEAN = keyOf("cold_ocean");
	public static final RegistryKey<Biome> DEEP_COLD_OCEAN = keyOf("deep_cold_ocean");
	public static final RegistryKey<Biome> FROZEN_OCEAN = keyOf("frozen_ocean");
	public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = keyOf("deep_frozen_ocean");
	public static final RegistryKey<Biome> MUSHROOM_FIELDS = keyOf("mushroom_fields");
	public static final RegistryKey<Biome> DRIPSTONE_CAVES = keyOf("dripstone_caves");
	public static final RegistryKey<Biome> LUSH_CAVES = keyOf("lush_caves");
	public static final RegistryKey<Biome> DEEP_DARK = keyOf("deep_dark");
	public static final RegistryKey<Biome> NETHER_WASTES = keyOf("nether_wastes");
	public static final RegistryKey<Biome> WARPED_FOREST = keyOf("warped_forest");
	public static final RegistryKey<Biome> CRIMSON_FOREST = keyOf("crimson_forest");
	public static final RegistryKey<Biome> SOUL_SAND_VALLEY = keyOf("soul_sand_valley");
	public static final RegistryKey<Biome> BASALT_DELTAS = keyOf("basalt_deltas");
	public static final RegistryKey<Biome> THE_END = keyOf("the_end");
	public static final RegistryKey<Biome> END_HIGHLANDS = keyOf("end_highlands");
	public static final RegistryKey<Biome> END_MIDLANDS = keyOf("end_midlands");
	public static final RegistryKey<Biome> SMALL_END_ISLANDS = keyOf("small_end_islands");
	public static final RegistryKey<Biome> END_BARRENS = keyOf("end_barrens");

	private static RegistryKey<Biome> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.BIOME, Identifier.ofVanilla(id));
	}
}
