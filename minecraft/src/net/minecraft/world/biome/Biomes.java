package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class Biomes {
	private static final Int2ObjectMap<RegistryKey<Biome>> BIOMES = new Int2ObjectArrayMap<>();
	public static final Biome PLAINS = register(1, BuiltInBiomes.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome THE_VOID = register(127, BuiltInBiomes.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome register(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
		BIOMES.put(rawId, registryKey);
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, registryKey, biome);
	}

	public static RegistryKey<Biome> fromRawId(int rawId) {
		return BIOMES.get(rawId);
	}

	static {
		register(0, BuiltInBiomes.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(2, BuiltInBiomes.DESERT, DefaultBiomeCreator.createDesert(0.125F, 0.05F, true, true, true));
		register(3, BuiltInBiomes.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(4, BuiltInBiomes.FOREST, DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
		register(5, BuiltInBiomes.TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, false, false, true, false));
		register(6, BuiltInBiomes.SWAMP, DefaultBiomeCreator.createSwamp(-0.2F, 0.1F, false));
		register(7, BuiltInBiomes.RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
		register(8, BuiltInBiomes.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(9, BuiltInBiomes.THE_END, DefaultBiomeCreator.createTheEnd());
		register(10, BuiltInBiomes.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(11, BuiltInBiomes.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
		register(12, BuiltInBiomes.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125F, 0.05F, false, false));
		register(13, BuiltInBiomes.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45F, 0.3F, false, true));
		register(14, BuiltInBiomes.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
		register(15, BuiltInBiomes.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
		register(16, BuiltInBiomes.BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
		register(17, BuiltInBiomes.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45F, 0.3F, false, true, false));
		register(18, BuiltInBiomes.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
		register(19, BuiltInBiomes.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, false, false, false, false));
		register(20, BuiltInBiomes.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true));
		register(21, BuiltInBiomes.JUNGLE, DefaultBiomeCreator.createJungle());
		register(22, BuiltInBiomes.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		register(23, BuiltInBiomes.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		register(24, BuiltInBiomes.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(25, BuiltInBiomes.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
		register(26, BuiltInBiomes.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
		register(27, BuiltInBiomes.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, false));
		register(28, BuiltInBiomes.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, false));
		register(29, BuiltInBiomes.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1F, 0.2F, false));
		register(30, BuiltInBiomes.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, true, false, false, true));
		register(31, BuiltInBiomes.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, true, false, false, false));
		register(32, BuiltInBiomes.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false));
		register(33, BuiltInBiomes.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false));
		register(34, BuiltInBiomes.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true));
		register(35, BuiltInBiomes.SAVANNA, DefaultBiomeCreator.createSavanna(0.125F, 0.05F, 1.2F, false, false));
		register(36, BuiltInBiomes.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(37, BuiltInBiomes.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1F, 0.2F, false));
		register(38, BuiltInBiomes.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5F, 0.025F));
		register(39, BuiltInBiomes.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5F, 0.025F, true));
		register(40, BuiltInBiomes.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		register(41, BuiltInBiomes.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		register(42, BuiltInBiomes.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		register(43, BuiltInBiomes.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		register(44, BuiltInBiomes.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(45, BuiltInBiomes.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(46, BuiltInBiomes.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(47, BuiltInBiomes.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(48, BuiltInBiomes.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(49, BuiltInBiomes.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(50, BuiltInBiomes.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(129, BuiltInBiomes.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		register(130, BuiltInBiomes.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225F, 0.25F, false, false, false));
		register(131, BuiltInBiomes.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(132, BuiltInBiomes.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(133, BuiltInBiomes.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, false, true, false, false));
		register(134, BuiltInBiomes.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1F, 0.3F, true));
		register(140, BuiltInBiomes.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425F, 0.45000002F, true, false));
		register(149, BuiltInBiomes.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		register(151, BuiltInBiomes.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		register(155, BuiltInBiomes.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, true));
		register(156, BuiltInBiomes.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, true));
		register(157, BuiltInBiomes.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2F, 0.4F, true));
		register(158, BuiltInBiomes.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, true, true, false, false));
		register(160, BuiltInBiomes.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(161, BuiltInBiomes.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		register(162, BuiltInBiomes.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(163, BuiltInBiomes.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625F, 1.225F, 1.1F, true, true));
		register(164, BuiltInBiomes.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05F, 1.2125001F, 1.0F, true, true));
		register(165, BuiltInBiomes.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(166, BuiltInBiomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45F, 0.3F));
		register(167, BuiltInBiomes.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45F, 0.3F, true));
		register(168, BuiltInBiomes.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(169, BuiltInBiomes.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
		register(170, BuiltInBiomes.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(171, BuiltInBiomes.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(172, BuiltInBiomes.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(173, BuiltInBiomes.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
	}
}
