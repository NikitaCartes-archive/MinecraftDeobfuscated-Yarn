package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class class_5504 {
	private static final Int2ObjectMap<RegistryKey<Biome>> field_26736 = new Int2ObjectArrayMap<>();
	public static final Biome field_26734 = method_31145(1, Biomes.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome field_26735 = method_31145(127, Biomes.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome method_31145(int i, RegistryKey<Biome> registryKey, Biome biome) {
		field_26736.put(i, registryKey);
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, i, registryKey, biome);
	}

	public static RegistryKey<Biome> method_31144(int i) {
		return field_26736.get(i);
	}

	static {
		method_31145(0, Biomes.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		method_31145(2, Biomes.DESERT, DefaultBiomeCreator.createDesert(0.125F, 0.05F, true, true, true));
		method_31145(3, Biomes.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false));
		method_31145(4, Biomes.FOREST, DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
		method_31145(5, Biomes.TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, false, false, true, false));
		method_31145(6, Biomes.SWAMP, DefaultBiomeCreator.createSwamp(-0.2F, 0.1F, false));
		method_31145(7, Biomes.RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
		method_31145(8, Biomes.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		method_31145(9, Biomes.THE_END, DefaultBiomeCreator.createTheEnd());
		method_31145(10, Biomes.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		method_31145(11, Biomes.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
		method_31145(12, Biomes.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125F, 0.05F, false, false));
		method_31145(13, Biomes.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45F, 0.3F, false, true));
		method_31145(14, Biomes.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
		method_31145(15, Biomes.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
		method_31145(16, Biomes.BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
		method_31145(17, Biomes.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45F, 0.3F, false, true, false));
		method_31145(18, Biomes.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
		method_31145(19, Biomes.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, false, false, false, false));
		method_31145(20, Biomes.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true));
		method_31145(21, Biomes.JUNGLE, DefaultBiomeCreator.createJungle());
		method_31145(22, Biomes.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
		method_31145(23, Biomes.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
		method_31145(24, Biomes.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		method_31145(25, Biomes.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
		method_31145(26, Biomes.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
		method_31145(27, Biomes.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, false));
		method_31145(28, Biomes.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, false));
		method_31145(29, Biomes.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1F, 0.2F, false));
		method_31145(30, Biomes.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, true, false, false, true));
		method_31145(31, Biomes.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, true, false, false, false));
		method_31145(32, Biomes.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false));
		method_31145(33, Biomes.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false));
		method_31145(34, Biomes.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true));
		method_31145(35, Biomes.SAVANNA, DefaultBiomeCreator.createSavanna(0.125F, 0.05F, 1.2F, false, false));
		method_31145(36, Biomes.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		method_31145(37, Biomes.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1F, 0.2F, false));
		method_31145(38, Biomes.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5F, 0.025F));
		method_31145(39, Biomes.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5F, 0.025F, true));
		method_31145(40, Biomes.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		method_31145(41, Biomes.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		method_31145(42, Biomes.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		method_31145(43, Biomes.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		method_31145(44, Biomes.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		method_31145(45, Biomes.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		method_31145(46, Biomes.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		method_31145(47, Biomes.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		method_31145(48, Biomes.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		method_31145(49, Biomes.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		method_31145(50, Biomes.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		method_31145(129, Biomes.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		method_31145(130, Biomes.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225F, 0.25F, false, false, false));
		method_31145(131, Biomes.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		method_31145(132, Biomes.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		method_31145(133, Biomes.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, false, true, false, false));
		method_31145(134, Biomes.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1F, 0.3F, true));
		method_31145(140, Biomes.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425F, 0.45000002F, true, false));
		method_31145(149, Biomes.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
		method_31145(151, Biomes.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
		method_31145(155, Biomes.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, true));
		method_31145(156, Biomes.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, true));
		method_31145(157, Biomes.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2F, 0.4F, true));
		method_31145(158, Biomes.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, true, true, false, false));
		method_31145(160, Biomes.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		method_31145(161, Biomes.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		method_31145(162, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		method_31145(163, Biomes.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625F, 1.225F, 1.1F, true, true));
		method_31145(164, Biomes.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05F, 1.2125001F, 1.0F, true, true));
		method_31145(165, Biomes.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		method_31145(166, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45F, 0.3F));
		method_31145(167, Biomes.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45F, 0.3F, true));
		method_31145(168, Biomes.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		method_31145(169, Biomes.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
		method_31145(170, Biomes.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		method_31145(171, Biomes.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		method_31145(172, Biomes.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		method_31145(173, Biomes.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
	}
}
