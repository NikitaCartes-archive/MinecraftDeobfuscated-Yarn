package net.minecraft.world.biome;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public abstract class BuiltinBiomes {
	public static void bootstrap(Registerable<Biome> biomeRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = biomeRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntryLookup<ConfiguredCarver<?>> registryEntryLookup2 = biomeRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);
		biomeRegisterable.register(BiomeKeys.THE_VOID, OverworldBiomeCreator.createTheVoid(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.PLAINS, OverworldBiomeCreator.createPlains(registryEntryLookup, registryEntryLookup2, false, false, false));
		biomeRegisterable.register(BiomeKeys.SUNFLOWER_PLAINS, OverworldBiomeCreator.createPlains(registryEntryLookup, registryEntryLookup2, true, false, false));
		biomeRegisterable.register(BiomeKeys.SNOWY_PLAINS, OverworldBiomeCreator.createPlains(registryEntryLookup, registryEntryLookup2, false, true, false));
		biomeRegisterable.register(BiomeKeys.ICE_SPIKES, OverworldBiomeCreator.createPlains(registryEntryLookup, registryEntryLookup2, false, true, true));
		biomeRegisterable.register(BiomeKeys.DESERT, OverworldBiomeCreator.createDesert(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.SWAMP, OverworldBiomeCreator.createSwamp(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.MANGROVE_SWAMP, OverworldBiomeCreator.createMangroveSwamp(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.FOREST, OverworldBiomeCreator.createNormalForest(registryEntryLookup, registryEntryLookup2, false, false, false));
		biomeRegisterable.register(BiomeKeys.FLOWER_FOREST, OverworldBiomeCreator.createNormalForest(registryEntryLookup, registryEntryLookup2, false, false, true));
		biomeRegisterable.register(BiomeKeys.BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(registryEntryLookup, registryEntryLookup2, true, false, false));
		biomeRegisterable.register(BiomeKeys.DARK_FOREST, OverworldBiomeCreator.createDarkForest(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(
			BiomeKeys.OLD_GROWTH_BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(registryEntryLookup, registryEntryLookup2, true, true, false)
		);
		biomeRegisterable.register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomeCreator.createOldGrowthTaiga(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.TAIGA, OverworldBiomeCreator.createTaiga(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.SNOWY_TAIGA, OverworldBiomeCreator.createTaiga(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.SAVANNA, OverworldBiomeCreator.createSavanna(registryEntryLookup, registryEntryLookup2, false, false));
		biomeRegisterable.register(BiomeKeys.SAVANNA_PLATEAU, OverworldBiomeCreator.createSavanna(registryEntryLookup, registryEntryLookup2, false, true));
		biomeRegisterable.register(BiomeKeys.WINDSWEPT_HILLS, OverworldBiomeCreator.createWindsweptHills(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomeCreator.createWindsweptHills(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.WINDSWEPT_FOREST, OverworldBiomeCreator.createWindsweptHills(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.WINDSWEPT_SAVANNA, OverworldBiomeCreator.createSavanna(registryEntryLookup, registryEntryLookup2, true, false));
		biomeRegisterable.register(BiomeKeys.JUNGLE, OverworldBiomeCreator.createJungle(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.SPARSE_JUNGLE, OverworldBiomeCreator.createSparseJungle(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.BAMBOO_JUNGLE, OverworldBiomeCreator.createNormalBambooJungle(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.BADLANDS, OverworldBiomeCreator.createBadlands(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.ERODED_BADLANDS, OverworldBiomeCreator.createBadlands(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.WOODED_BADLANDS, OverworldBiomeCreator.createBadlands(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.MEADOW, OverworldBiomeCreator.createMeadow(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.CHERRY_GROVE, OverworldBiomeCreator.createMeadow(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.GROVE, OverworldBiomeCreator.createGrove(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.SNOWY_SLOPES, OverworldBiomeCreator.createSnowySlopes(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.FROZEN_PEAKS, OverworldBiomeCreator.createFrozenPeaks(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.JAGGED_PEAKS, OverworldBiomeCreator.createJaggedPeaks(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.STONY_PEAKS, OverworldBiomeCreator.createStonyPeaks(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.RIVER, OverworldBiomeCreator.createRiver(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.FROZEN_RIVER, OverworldBiomeCreator.createRiver(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.BEACH, OverworldBiomeCreator.createBeach(registryEntryLookup, registryEntryLookup2, false, false));
		biomeRegisterable.register(BiomeKeys.SNOWY_BEACH, OverworldBiomeCreator.createBeach(registryEntryLookup, registryEntryLookup2, true, false));
		biomeRegisterable.register(BiomeKeys.STONY_SHORE, OverworldBiomeCreator.createBeach(registryEntryLookup, registryEntryLookup2, false, true));
		biomeRegisterable.register(BiomeKeys.WARM_OCEAN, OverworldBiomeCreator.createWarmOcean(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.DEEP_LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.OCEAN, OverworldBiomeCreator.createNormalOcean(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.DEEP_OCEAN, OverworldBiomeCreator.createNormalOcean(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.COLD_OCEAN, OverworldBiomeCreator.createColdOcean(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.DEEP_COLD_OCEAN, OverworldBiomeCreator.createColdOcean(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(registryEntryLookup, registryEntryLookup2, false));
		biomeRegisterable.register(BiomeKeys.DEEP_FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(registryEntryLookup, registryEntryLookup2, true));
		biomeRegisterable.register(BiomeKeys.MUSHROOM_FIELDS, OverworldBiomeCreator.createMushroomFields(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.DRIPSTONE_CAVES, OverworldBiomeCreator.createDripstoneCaves(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.LUSH_CAVES, OverworldBiomeCreator.createLushCaves(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.DEEP_DARK, OverworldBiomeCreator.createDeepDark(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.NETHER_WASTES, TheNetherBiomeCreator.createNetherWastes(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.WARPED_FOREST, TheNetherBiomeCreator.createWarpedForest(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.CRIMSON_FOREST, TheNetherBiomeCreator.createCrimsonForest(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.SOUL_SAND_VALLEY, TheNetherBiomeCreator.createSoulSandValley(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.BASALT_DELTAS, TheNetherBiomeCreator.createBasaltDeltas(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.THE_END, TheEndBiomeCreator.createTheEnd(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.END_HIGHLANDS, TheEndBiomeCreator.createEndHighlands(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.END_MIDLANDS, TheEndBiomeCreator.createEndMidlands(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.SMALL_END_ISLANDS, TheEndBiomeCreator.createSmallEndIslands(registryEntryLookup, registryEntryLookup2));
		biomeRegisterable.register(BiomeKeys.END_BARRENS, TheEndBiomeCreator.createEndBarrens(registryEntryLookup, registryEntryLookup2));
	}
}
