/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    private static final Int2ObjectMap<RegistryKey<Biome>> field_26736 = new Int2ObjectArrayMap<RegistryKey<Biome>>();
    public static final Biome field_26734;
    public static final Biome field_26735;

    private static Biome method_31145(int i, RegistryKey<Biome> registryKey, Biome biome) {
        field_26736.put(i, registryKey);
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, i, registryKey, biome);
    }

    public static RegistryKey<Biome> method_31144(int i) {
        return (RegistryKey)field_26736.get(i);
    }

    static {
        class_5504.method_31145(0, Biomes.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        field_26734 = class_5504.method_31145(1, Biomes.PLAINS, DefaultBiomeCreator.createPlains(false));
        class_5504.method_31145(2, Biomes.DESERT, DefaultBiomeCreator.createDesert(0.125f, 0.05f, true, true, true));
        class_5504.method_31145(3, Biomes.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.MOUNTAIN, false));
        class_5504.method_31145(4, Biomes.FOREST, DefaultBiomeCreator.createNormalForest(0.1f, 0.2f));
        class_5504.method_31145(5, Biomes.TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, false, false, true, false));
        class_5504.method_31145(6, Biomes.SWAMP, DefaultBiomeCreator.createSwamp(-0.2f, 0.1f, false));
        class_5504.method_31145(7, Biomes.RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.5f, 4159204, false));
        class_5504.method_31145(8, Biomes.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
        class_5504.method_31145(9, Biomes.THE_END, DefaultBiomeCreator.createTheEnd());
        class_5504.method_31145(10, Biomes.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        class_5504.method_31145(11, Biomes.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.0f, 3750089, true));
        class_5504.method_31145(12, Biomes.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125f, 0.05f, false, false));
        class_5504.method_31145(13, Biomes.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45f, 0.3f, false, true));
        class_5504.method_31145(14, Biomes.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2f, 0.3f));
        class_5504.method_31145(15, Biomes.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0f, 0.025f));
        class_5504.method_31145(16, Biomes.BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.8f, 0.4f, 4159204, false, false));
        class_5504.method_31145(17, Biomes.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45f, 0.3f, false, true, false));
        class_5504.method_31145(18, Biomes.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45f, 0.3f));
        class_5504.method_31145(19, Biomes.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, false, false, false, false));
        class_5504.method_31145(20, Biomes.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8f, 0.3f, ConfiguredSurfaceBuilders.GRASS, true));
        class_5504.method_31145(21, Biomes.JUNGLE, DefaultBiomeCreator.createJungle());
        class_5504.method_31145(22, Biomes.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
        class_5504.method_31145(23, Biomes.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
        class_5504.method_31145(24, Biomes.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        class_5504.method_31145(25, Biomes.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1f, 0.8f, 0.2f, 0.3f, 4159204, false, true));
        class_5504.method_31145(26, Biomes.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.05f, 0.3f, 4020182, true, false));
        class_5504.method_31145(27, Biomes.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1f, 0.2f, false));
        class_5504.method_31145(28, Biomes.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45f, 0.3f, false));
        class_5504.method_31145(29, Biomes.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1f, 0.2f, false));
        class_5504.method_31145(30, Biomes.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, true, false, false, true));
        class_5504.method_31145(31, Biomes.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, true, false, false, false));
        class_5504.method_31145(32, Biomes.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.3f, false));
        class_5504.method_31145(33, Biomes.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45f, 0.3f, 0.3f, false));
        class_5504.method_31145(34, Biomes.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRASS, true));
        class_5504.method_31145(35, Biomes.SAVANNA, DefaultBiomeCreator.createSavanna(0.125f, 0.05f, 1.2f, false, false));
        class_5504.method_31145(36, Biomes.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
        class_5504.method_31145(37, Biomes.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1f, 0.2f, false));
        class_5504.method_31145(38, Biomes.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5f, 0.025f));
        class_5504.method_31145(39, Biomes.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5f, 0.025f, true));
        class_5504.method_31145(40, Biomes.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
        class_5504.method_31145(41, Biomes.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
        class_5504.method_31145(42, Biomes.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
        class_5504.method_31145(43, Biomes.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
        class_5504.method_31145(44, Biomes.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        class_5504.method_31145(45, Biomes.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        class_5504.method_31145(46, Biomes.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        class_5504.method_31145(47, Biomes.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        class_5504.method_31145(48, Biomes.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        class_5504.method_31145(49, Biomes.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        class_5504.method_31145(50, Biomes.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        field_26735 = class_5504.method_31145(127, Biomes.THE_VOID, DefaultBiomeCreator.createTheVoid());
        class_5504.method_31145(129, Biomes.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
        class_5504.method_31145(130, Biomes.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225f, 0.25f, false, false, false));
        class_5504.method_31145(131, Biomes.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        class_5504.method_31145(132, Biomes.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
        class_5504.method_31145(133, Biomes.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, false, true, false, false));
        class_5504.method_31145(134, Biomes.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1f, 0.3f, true));
        class_5504.method_31145(140, Biomes.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425f, 0.45000002f, true, false));
        class_5504.method_31145(149, Biomes.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
        class_5504.method_31145(151, Biomes.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
        class_5504.method_31145(155, Biomes.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2f, 0.4f, true));
        class_5504.method_31145(156, Biomes.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55f, 0.5f, true));
        class_5504.method_31145(157, Biomes.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2f, 0.4f, true));
        class_5504.method_31145(158, Biomes.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, true, true, false, false));
        class_5504.method_31145(160, Biomes.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        class_5504.method_31145(161, Biomes.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        class_5504.method_31145(162, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        class_5504.method_31145(163, Biomes.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625f, 1.225f, 1.1f, true, true));
        class_5504.method_31145(164, Biomes.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05f, 1.2125001f, 1.0f, true, true));
        class_5504.method_31145(165, Biomes.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
        class_5504.method_31145(166, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45f, 0.3f));
        class_5504.method_31145(167, Biomes.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45f, 0.3f, true));
        class_5504.method_31145(168, Biomes.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        class_5504.method_31145(169, Biomes.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
        class_5504.method_31145(170, Biomes.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
        class_5504.method_31145(171, Biomes.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
        class_5504.method_31145(172, Biomes.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
        class_5504.method_31145(173, Biomes.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
    }
}

