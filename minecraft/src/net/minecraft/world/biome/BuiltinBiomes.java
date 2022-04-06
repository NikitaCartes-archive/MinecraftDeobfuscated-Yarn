package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public abstract class BuiltinBiomes {
	private static void register(RegistryKey<Biome> key, Biome biome) {
		BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
	}

	public static RegistryEntry<Biome> getDefaultBiome() {
		return BuiltinRegistries.BIOME.entryOf(BiomeKeys.PLAINS);
	}

	static {
		register(BiomeKeys.THE_VOID, OverworldBiomeCreator.createTheVoid());
		register(BiomeKeys.PLAINS, OverworldBiomeCreator.createPlains(false, false, false));
		register(BiomeKeys.SUNFLOWER_PLAINS, OverworldBiomeCreator.createPlains(true, false, false));
		register(BiomeKeys.SNOWY_PLAINS, OverworldBiomeCreator.createPlains(false, true, false));
		register(BiomeKeys.ICE_SPIKES, OverworldBiomeCreator.createPlains(false, true, true));
		register(BiomeKeys.DESERT, OverworldBiomeCreator.createDesert());
		register(BiomeKeys.SWAMP, OverworldBiomeCreator.createSwamp());
		register(BiomeKeys.MANGROVE_SWAMP, OverworldBiomeCreator.createMangroveSwamp());
		register(BiomeKeys.FOREST, OverworldBiomeCreator.createNormalForest(false, false, false));
		register(BiomeKeys.FLOWER_FOREST, OverworldBiomeCreator.createNormalForest(false, false, true));
		register(BiomeKeys.BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, false, false));
		register(BiomeKeys.DARK_FOREST, OverworldBiomeCreator.createDarkForest());
		register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, true, false));
		register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(false));
		register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(true));
		register(BiomeKeys.TAIGA, OverworldBiomeCreator.createTaiga(false));
		register(BiomeKeys.SNOWY_TAIGA, OverworldBiomeCreator.createTaiga(true));
		register(BiomeKeys.SAVANNA, OverworldBiomeCreator.createSavanna(false, false));
		register(BiomeKeys.SAVANNA_PLATEAU, OverworldBiomeCreator.createSavanna(false, true));
		register(BiomeKeys.WINDSWEPT_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
		register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
		register(BiomeKeys.WINDSWEPT_FOREST, OverworldBiomeCreator.createWindsweptHills(true));
		register(BiomeKeys.WINDSWEPT_SAVANNA, OverworldBiomeCreator.createSavanna(true, false));
		register(BiomeKeys.JUNGLE, OverworldBiomeCreator.createJungle());
		register(BiomeKeys.SPARSE_JUNGLE, OverworldBiomeCreator.createSparseJungle());
		register(BiomeKeys.BAMBOO_JUNGLE, OverworldBiomeCreator.createNormalBambooJungle());
		register(BiomeKeys.BADLANDS, OverworldBiomeCreator.createBadlands(false));
		register(BiomeKeys.ERODED_BADLANDS, OverworldBiomeCreator.createBadlands(false));
		register(BiomeKeys.WOODED_BADLANDS, OverworldBiomeCreator.createBadlands(true));
		register(BiomeKeys.MEADOW, OverworldBiomeCreator.createMeadow());
		register(BiomeKeys.GROVE, OverworldBiomeCreator.createGrove());
		register(BiomeKeys.SNOWY_SLOPES, OverworldBiomeCreator.createSnowySlopes());
		register(BiomeKeys.FROZEN_PEAKS, OverworldBiomeCreator.createFrozenPeaks());
		register(BiomeKeys.JAGGED_PEAKS, OverworldBiomeCreator.createJaggedPeaks());
		register(BiomeKeys.STONY_PEAKS, OverworldBiomeCreator.createStonyPeaks());
		register(BiomeKeys.RIVER, OverworldBiomeCreator.createRiver(false));
		register(BiomeKeys.FROZEN_RIVER, OverworldBiomeCreator.createRiver(true));
		register(BiomeKeys.BEACH, OverworldBiomeCreator.createBeach(false, false));
		register(BiomeKeys.SNOWY_BEACH, OverworldBiomeCreator.createBeach(true, false));
		register(BiomeKeys.STONY_SHORE, OverworldBiomeCreator.createBeach(false, true));
		register(BiomeKeys.WARM_OCEAN, OverworldBiomeCreator.createWarmOcean());
		register(BiomeKeys.LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(false));
		register(BiomeKeys.DEEP_LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(true));
		register(BiomeKeys.OCEAN, OverworldBiomeCreator.createNormalOcean(false));
		register(BiomeKeys.DEEP_OCEAN, OverworldBiomeCreator.createNormalOcean(true));
		register(BiomeKeys.COLD_OCEAN, OverworldBiomeCreator.createColdOcean(false));
		register(BiomeKeys.DEEP_COLD_OCEAN, OverworldBiomeCreator.createColdOcean(true));
		register(BiomeKeys.FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(false));
		register(BiomeKeys.DEEP_FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(true));
		register(BiomeKeys.MUSHROOM_FIELDS, OverworldBiomeCreator.createMushroomFields());
		register(BiomeKeys.DRIPSTONE_CAVES, OverworldBiomeCreator.createDripstoneCaves());
		register(BiomeKeys.LUSH_CAVES, OverworldBiomeCreator.createLushCaves());
		register(BiomeKeys.DEEP_DARK, OverworldBiomeCreator.createDeepDark());
		register(BiomeKeys.NETHER_WASTES, TheNetherBiomeCreator.createNetherWastes());
		register(BiomeKeys.WARPED_FOREST, TheNetherBiomeCreator.createWarpedForest());
		register(BiomeKeys.CRIMSON_FOREST, TheNetherBiomeCreator.createCrimsonForest());
		register(BiomeKeys.SOUL_SAND_VALLEY, TheNetherBiomeCreator.createSoulSandValley());
		register(BiomeKeys.BASALT_DELTAS, TheNetherBiomeCreator.createBasaltDeltas());
		register(BiomeKeys.THE_END, TheEndBiomeCreator.createTheEnd());
		register(BiomeKeys.END_HIGHLANDS, TheEndBiomeCreator.createEndHighlands());
		register(BiomeKeys.END_MIDLANDS, TheEndBiomeCreator.createEndMidlands());
		register(BiomeKeys.SMALL_END_ISLANDS, TheEndBiomeCreator.createSmallEndIslands());
		register(BiomeKeys.END_BARRENS, TheEndBiomeCreator.createEndBarrens());
	}
}
