/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
    private static final Int2ObjectMap<RegistryKey<Biome>> BY_RAW_ID = new Int2ObjectArrayMap<RegistryKey<Biome>>();
    public static final Biome PLAINS;
    public static final Biome THE_VOID;

    private static Biome register(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
        BY_RAW_ID.put(rawId, registryKey);
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, registryKey, biome);
    }

    public static RegistryKey<Biome> fromRawId(int rawId) {
        return (RegistryKey)BY_RAW_ID.get(rawId);
    }

    static {
        BuiltinBiomes.register(0, BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        PLAINS = BuiltinBiomes.register(1, BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
        BuiltinBiomes.register(2, BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(0.125f, 0.05f, true, true, true));
        BuiltinBiomes.register(3, BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.MOUNTAIN, false));
        BuiltinBiomes.register(4, BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(0.1f, 0.2f));
        BuiltinBiomes.register(5, BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, false, false, true, false));
        BuiltinBiomes.register(6, BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(-0.2f, 0.1f, false));
        BuiltinBiomes.register(7, BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.5f, 4159204, false));
        BuiltinBiomes.register(8, BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
        BuiltinBiomes.register(9, BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
        BuiltinBiomes.register(10, BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        BuiltinBiomes.register(11, BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.0f, 3750089, true));
        BuiltinBiomes.register(12, BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125f, 0.05f, false, false));
        BuiltinBiomes.register(13, BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45f, 0.3f, false, true));
        BuiltinBiomes.register(14, BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2f, 0.3f));
        BuiltinBiomes.register(15, BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0f, 0.025f));
        BuiltinBiomes.register(16, BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.8f, 0.4f, 4159204, false, false));
        BuiltinBiomes.register(17, BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45f, 0.3f, false, true, false));
        BuiltinBiomes.register(18, BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45f, 0.3f));
        BuiltinBiomes.register(19, BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, false, false, false, false));
        BuiltinBiomes.register(20, BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8f, 0.3f, ConfiguredSurfaceBuilders.GRASS, true));
        BuiltinBiomes.register(21, BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
        BuiltinBiomes.register(22, BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
        BuiltinBiomes.register(23, BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
        BuiltinBiomes.register(24, BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        BuiltinBiomes.register(25, BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1f, 0.8f, 0.2f, 0.3f, 4159204, false, true));
        BuiltinBiomes.register(26, BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.05f, 0.3f, 4020182, true, false));
        BuiltinBiomes.register(27, BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1f, 0.2f, false));
        BuiltinBiomes.register(28, BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45f, 0.3f, false));
        BuiltinBiomes.register(29, BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1f, 0.2f, false));
        BuiltinBiomes.register(30, BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, true, false, false, true));
        BuiltinBiomes.register(31, BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, true, false, false, false));
        BuiltinBiomes.register(32, BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.3f, false));
        BuiltinBiomes.register(33, BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45f, 0.3f, 0.3f, false));
        BuiltinBiomes.register(34, BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRASS, true));
        BuiltinBiomes.register(35, BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(0.125f, 0.05f, 1.2f, false, false));
        BuiltinBiomes.register(36, BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
        BuiltinBiomes.register(37, BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1f, 0.2f, false));
        BuiltinBiomes.register(38, BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5f, 0.025f));
        BuiltinBiomes.register(39, BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5f, 0.025f, true));
        BuiltinBiomes.register(40, BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
        BuiltinBiomes.register(41, BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
        BuiltinBiomes.register(42, BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
        BuiltinBiomes.register(43, BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
        BuiltinBiomes.register(44, BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        BuiltinBiomes.register(45, BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        BuiltinBiomes.register(46, BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        BuiltinBiomes.register(47, BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        BuiltinBiomes.register(48, BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        BuiltinBiomes.register(49, BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        BuiltinBiomes.register(50, BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        THE_VOID = BuiltinBiomes.register(127, BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());
        BuiltinBiomes.register(129, BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
        BuiltinBiomes.register(130, BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225f, 0.25f, false, false, false));
        BuiltinBiomes.register(131, BiomeKeys.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        BuiltinBiomes.register(132, BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
        BuiltinBiomes.register(133, BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, false, true, false, false));
        BuiltinBiomes.register(134, BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1f, 0.3f, true));
        BuiltinBiomes.register(140, BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425f, 0.45000002f, true, false));
        BuiltinBiomes.register(149, BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
        BuiltinBiomes.register(151, BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
        BuiltinBiomes.register(155, BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2f, 0.4f, true));
        BuiltinBiomes.register(156, BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55f, 0.5f, true));
        BuiltinBiomes.register(157, BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2f, 0.4f, true));
        BuiltinBiomes.register(158, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, true, true, false, false));
        BuiltinBiomes.register(160, BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        BuiltinBiomes.register(161, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        BuiltinBiomes.register(162, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        BuiltinBiomes.register(163, BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625f, 1.225f, 1.1f, true, true));
        BuiltinBiomes.register(164, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05f, 1.2125001f, 1.0f, true, true));
        BuiltinBiomes.register(165, BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
        BuiltinBiomes.register(166, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45f, 0.3f));
        BuiltinBiomes.register(167, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45f, 0.3f, true));
        BuiltinBiomes.register(168, BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        BuiltinBiomes.register(169, BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
        BuiltinBiomes.register(170, BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
        BuiltinBiomes.register(171, BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
        BuiltinBiomes.register(172, BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
        BuiltinBiomes.register(173, BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
        BuiltinBiomes.register(174, BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.method_33132());
    }
}

