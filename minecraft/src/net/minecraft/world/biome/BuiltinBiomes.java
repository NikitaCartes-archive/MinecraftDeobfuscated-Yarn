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
		register(2, BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(0.125F, 0.05F, true, true, true));
		register(3, BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(4, BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
		register(5, BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, false, false, true, false));
		register(6, BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(-0.2F, 0.1F, false));
		register(7, BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
		register(8, BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(9, BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
		register(10, BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(11, BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
		register(12, BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125F, 0.05F, false, false));
		register(13, BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45F, 0.3F, false, true));
		register(14, BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
		register(15, BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
		register(16, BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
		register(17, BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45F, 0.3F, false, true, false));
		register(18, BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
		register(19, BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, false, false, false, false));
		register(20, BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true));
		register(21, BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(22, BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		register(23, BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		register(24, BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(25, BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
		register(26, BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
		register(27, BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, false));
		register(28, BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, false));
		register(29, BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1F, 0.2F, false));
		register(30, BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, true, false, false, true));
		register(31, BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, true, false, false, false));
		register(32, BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false));
		register(33, BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false));
		register(34, BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true));
		register(35, BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(0.125F, 0.05F, 1.2F, false, false));
		register(36, BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(37, BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1F, 0.2F, false));
		register(38, BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5F, 0.025F));
		register(39, BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5F, 0.025F, true));
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
		register(130, BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225F, 0.25F, false, false, false));
		register(131, BiomeKeys.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(132, BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(133, BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, false, true, false, false));
		register(134, BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1F, 0.3F, true));
		register(140, BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425F, 0.45000002F, true, false));
		register(149, BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		register(151, BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		register(155, BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, true));
		register(156, BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, true));
		register(157, BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2F, 0.4F, true));
		register(158, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, true, true, false, false));
		register(160, BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(161, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(162, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(163, BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625F, 1.225F, 1.1F, true, true));
		register(164, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05F, 1.2125001F, 1.0F, true, true));
		register(165, BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(166, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45F, 0.3F));
		register(167, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45F, 0.3F, true));
		register(168, BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(169, BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
		register(170, BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(171, BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(172, BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(173, BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
		register(174, BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
	}
}
