package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
	private static final Int2ObjectMap<RegistryKey<Biome>> BY_RAW_ID = new Int2ObjectArrayMap<>();
	public static final Biome PLAINS = register(BiomeIds.PLAINS, BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome THE_VOID = register(127, BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome register(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
		BY_RAW_ID.put(rawId, registryKey);
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, registryKey, biome);
	}

	public static RegistryKey<Biome> fromRawId(int rawId) {
		return BY_RAW_ID.get(rawId);
	}

	static {
		register(BiomeIds.OCEAN, BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(BiomeIds.DESERT, BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(0.125F, 0.05F, true, true, true));
		register(BiomeIds.MOUNTAINS, BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(BiomeIds.FOREST, BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
		register(BiomeIds.TAIGA, BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, false, false, true, false));
		register(BiomeIds.SWAMP, BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(-0.2F, 0.1F, false));
		register(BiomeIds.RIVER, BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
		register(8, BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(9, BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
		register(BiomeIds.FROZEN_OCEAN, BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(BiomeIds.FROZEN_RIVER, BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
		register(BiomeIds.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125F, 0.05F, false, false));
		register(BiomeIds.SNOWY_MOUNTAINS, BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45F, 0.3F, false, true));
		register(BiomeIds.MUSHROOM_FIELDS, BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
		register(BiomeIds.MUSHROOM_FIELD_SHORE, BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
		register(BiomeIds.BEACH, BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
		register(BiomeIds.DESERT_HILLS, BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45F, 0.3F, false, true, false));
		register(BiomeIds.WOODED_HILLS, BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
		register(BiomeIds.TAIGA_HILLS, BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, false, false, false, false));
		register(BiomeIds.MOUNTAIN_EDGE, BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true));
		register(BiomeIds.JUNGLE, BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(BiomeIds.JUNGLE_HILLS, BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		register(BiomeIds.JUNGLE_EDGE, BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		register(BiomeIds.DEEP_OCEAN, BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(BiomeIds.STONE_SHORE, BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
		register(BiomeIds.SNOWY_BEACH, BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
		register(BiomeIds.BIRCH_FOREST, BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, false));
		register(BiomeIds.BIRCH_FOREST_HILLS, BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, false));
		register(BiomeIds.DARK_FOREST, BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1F, 0.2F, false));
		register(BiomeIds.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, true, false, false, true));
		register(BiomeIds.SNOWY_TAIGA_HILLS, BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, true, false, false, false));
		register(BiomeIds.GIANT_TREE_TAIGA, BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false));
		register(BiomeIds.GIANT_TREE_TAIGA_HILLS, BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false));
		register(BiomeIds.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true));
		register(BiomeIds.SAVANNA, BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(0.125F, 0.05F, 1.2F, false, false));
		register(BiomeIds.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(BiomeIds.BADLANDS, BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1F, 0.2F, false));
		register(BiomeIds.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5F, 0.025F));
		register(BiomeIds.BADLANDS_PLATEAU, BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5F, 0.025F, true));
		register(40, BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		register(41, BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		register(42, BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		register(43, BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		register(BiomeIds.WARM_OCEAN, BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(BiomeIds.LUKEWARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(BiomeIds.COLD_OCEAN, BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(BiomeIds.DEEP_WARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(BiomeIds.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(BiomeIds.DEEP_COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(BiomeIds.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(BiomeIds.SUNFLOWER_PLAINS, BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		register(BiomeIds.DESERT_LAKES, BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225F, 0.25F, false, false, false));
		register(
			BiomeIds.GRAVELLY_MOUNTAINS,
			BiomeKeys.GRAVELLY_MOUNTAINS,
			DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false)
		);
		register(BiomeIds.FLOWER_FOREST, BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(BiomeIds.TAIGA_MOUNTAINS, BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, false, true, false, false));
		register(BiomeIds.SWAMP_HILLS, BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1F, 0.3F, true));
		register(BiomeIds.ICE_SPIKES, BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425F, 0.45000002F, true, false));
		register(BiomeIds.MODIFIED_JUNGLE, BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		register(BiomeIds.MODIFIED_JUNGLE_EDGE, BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		register(BiomeIds.TALL_BIRCH_FOREST, BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, true));
		register(BiomeIds.TALL_BIRCH_HILLS, BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, true));
		register(BiomeIds.DARK_FOREST_HILLS, BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2F, 0.4F, true));
		register(BiomeIds.SNOWY_TAIGA_MOUNTAINS, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, true, true, false, false));
		register(BiomeIds.GIANT_SPRUCE_TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(BiomeIds.GIANT_SPRUCE_TAIGA_HILLS, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(
			BiomeIds.MODIFIED_GRAVELLY_MOUNTAINS,
			BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS,
			DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false)
		);
		register(BiomeIds.SHATTERED_SAVANNA, BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625F, 1.225F, 1.1F, true, true));
		register(BiomeIds.SHATTERED_SAVANNA_PLATEAU, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05F, 1.2125001F, 1.0F, true, true));
		register(BiomeIds.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(BiomeIds.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45F, 0.3F));
		register(BiomeIds.MODIFIED_BADLANDS_PLATEAU, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45F, 0.3F, true));
		register(BiomeIds.BAMBOO_JUNGLE, BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(BiomeIds.BAMBOO_JUNGLE_HILLS, BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
		register(170, BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(171, BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(172, BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(173, BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
		register(174, BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
		register(175, BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
	}
}
