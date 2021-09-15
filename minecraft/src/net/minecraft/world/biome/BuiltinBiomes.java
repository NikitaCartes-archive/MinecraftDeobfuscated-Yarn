package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
	public static final Biome PLAINS = register(BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome THE_VOID = register(BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome register(RegistryKey<Biome> registryKey, Biome biome) {
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, registryKey, biome);
	}

	static {
		register(BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(true));
		register(BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest());
		register(BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false, false));
		register(BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(false));
		register(BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(0.5F, 4159204, false));
		register(BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
		register(BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(0.0F, 3750089, true));
		register(BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(false));
		register(BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
		register(BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.8F, 0.4F, 4159204, false, false));
		register(BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		register(BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.2F, 0.3F, 4159204, false, true));
		register(BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.05F, 0.3F, 4020182, true, false));
		register(BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(false));
		register(BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(false));
		register(BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true, false));
		register(BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.3F, false));
		register(BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
		register(BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(1.2F, false));
		register(BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau());
		register(BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands());
		register(BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		register(BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		register(BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		register(BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		register(BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		register(BiomeKeys.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(true));
		register(BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(true));
		register(BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.25F, true));
		register(BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(1.1F, true));
		register(BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
		register(BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
		register(BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
		register(BiomeKeys.MEADOW, DefaultBiomeCreator.composeMeadowSettings());
		register(BiomeKeys.GROVE, DefaultBiomeCreator.composeGroveSettings());
		register(BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.composeSnowySlopesSettings());
		register(BiomeKeys.SNOWCAPPED_PEAKS, DefaultBiomeCreator.composeSnowcappedPeaksSettings());
		register(BiomeKeys.LOFTY_PEAKS, DefaultBiomeCreator.composeLoftyPeaksSettings());
		register(BiomeKeys.STONY_PEAKS, DefaultBiomeCreator.composeStonyPeaksSettings());
		register(BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(false));
		register(BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields());
		register(BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(false));
		register(BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest());
		register(BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(false, false));
		register(BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
		register(BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		register(BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(false));
		register(BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(true, false));
		register(BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.3F, false));
		register(BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands());
		register(BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(false));
		register(BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(false, true));
		register(BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(true));
		register(BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		register(BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		register(BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(true));
		register(BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(true));
		register(BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(true, true));
		register(BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.25F, true));
		register(BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.0F, true));
		register(BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau());
		register(BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands());
		register(BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
	}
}
