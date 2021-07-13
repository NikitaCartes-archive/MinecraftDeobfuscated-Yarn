package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
	private static final Int2ObjectMap<RegistryKey<Biome>> BY_RAW_ID = new Int2ObjectArrayMap<>();
	public static final Biome PLAINS = register(1, BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome THE_VOID = register(127, BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome register(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
		BY_RAW_ID.put(rawId, registryKey);
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, registryKey, biome);
	}

	public static RegistryKey<Biome> fromRawId(int rawId) {
		return BY_RAW_ID.get(rawId);
	}

	static {
		register(0, BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(2, BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(true, true, true));
		register(3, BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(4, BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest());
		register(5, BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false, false, true, false));
		register(6, BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(false));
		register(7, BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(0.5F, 4159204, false));
		register(8, BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(9, BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
		register(10, BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(11, BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(0.0F, 3750089, true));
		register(12, BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(false, false));
		register(14, BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
		register(16, BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.8F, 0.4F, 4159204, false, false));
		register(21, BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(23, BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		register(24, BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(25, BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.2F, 0.3F, 4159204, false, true));
		register(26, BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.05F, 0.3F, 4020182, true, false));
		register(27, BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(false));
		register(29, BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(false));
		register(30, BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true, false, false, true));
		register(32, BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.3F, false));
		register(34, BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
		register(35, BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(1.2F, false, false));
		register(36, BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(38, BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau());
		register(39, BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(true));
		register(40, BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		register(41, BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		register(42, BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		register(43, BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		register(44, BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(45, BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(46, BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(47, BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(48, BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(49, BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(50, BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(129, BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		register(131, BiomeKeys.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(132, BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(140, BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(true, false));
		register(155, BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(true));
		register(160, BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.25F, true));
		register(163, BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(1.1F, true, true));
		register(165, BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(168, BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(170, BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(171, BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(172, BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(173, BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
		register(174, BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
		register(175, BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
		register(177, BiomeKeys.MEADOW, DefaultBiomeCreator.createMeadow());
		register(178, BiomeKeys.GROVE, DefaultBiomeCreator.createGrove());
		register(179, BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.createSnowySlopes());
		register(180, BiomeKeys.SNOWCAPPED_PEAKS, DefaultBiomeCreator.createSnowcappedPeaks());
		register(181, BiomeKeys.LOFTY_PEAKS, DefaultBiomeCreator.createLoftyPeaks());
		register(13, BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(false, true));
		register(15, BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields());
		register(17, BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(false, true, false));
		register(18, BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest());
		register(19, BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(false, false, false, false));
		register(20, BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
		register(22, BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		register(28, BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(false));
		register(31, BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(true, false, false, false));
		register(33, BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.3F, false));
		register(37, BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(false));
		register(130, BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(false, false, false));
		register(133, BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(false, true, false, false));
		register(134, BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(true));
		register(149, BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		register(151, BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		register(156, BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(true));
		register(157, BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(true));
		register(158, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(true, true, false, false));
		register(161, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.25F, true));
		register(162, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(164, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.0F, true, true));
		register(166, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau());
		register(167, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(true));
		register(169, BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
	}
}
