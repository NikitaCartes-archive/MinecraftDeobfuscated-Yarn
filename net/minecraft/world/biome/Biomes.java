/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltInBiomes;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class Biomes {
    private static final Int2ObjectMap<RegistryKey<Biome>> BIOMES = new Int2ObjectArrayMap<RegistryKey<Biome>>();
    public static final Biome PLAINS;
    public static final Biome THE_VOID;

    private static Biome register(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
        BIOMES.put(rawId, registryKey);
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, registryKey, biome);
    }

    public static RegistryKey<Biome> fromRawId(int rawId) {
        return (RegistryKey)BIOMES.get(rawId);
    }

    static {
        Biomes.register(0, BuiltInBiomes.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        PLAINS = Biomes.register(1, BuiltInBiomes.PLAINS, DefaultBiomeCreator.createPlains(false));
        Biomes.register(2, BuiltInBiomes.DESERT, DefaultBiomeCreator.createDesert(0.125f, 0.05f, true, true, true));
        Biomes.register(3, BuiltInBiomes.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.MOUNTAIN, false));
        Biomes.register(4, BuiltInBiomes.FOREST, DefaultBiomeCreator.createNormalForest(0.1f, 0.2f));
        Biomes.register(5, BuiltInBiomes.TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, false, false, true, false));
        Biomes.register(6, BuiltInBiomes.SWAMP, DefaultBiomeCreator.createSwamp(-0.2f, 0.1f, false));
        Biomes.register(7, BuiltInBiomes.RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.5f, 4159204, false));
        Biomes.register(8, BuiltInBiomes.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
        Biomes.register(9, BuiltInBiomes.THE_END, DefaultBiomeCreator.createTheEnd());
        Biomes.register(10, BuiltInBiomes.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        Biomes.register(11, BuiltInBiomes.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.0f, 3750089, true));
        Biomes.register(12, BuiltInBiomes.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125f, 0.05f, false, false));
        Biomes.register(13, BuiltInBiomes.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45f, 0.3f, false, true));
        Biomes.register(14, BuiltInBiomes.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2f, 0.3f));
        Biomes.register(15, BuiltInBiomes.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0f, 0.025f));
        Biomes.register(16, BuiltInBiomes.BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.8f, 0.4f, 4159204, false, false));
        Biomes.register(17, BuiltInBiomes.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45f, 0.3f, false, true, false));
        Biomes.register(18, BuiltInBiomes.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45f, 0.3f));
        Biomes.register(19, BuiltInBiomes.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, false, false, false, false));
        Biomes.register(20, BuiltInBiomes.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8f, 0.3f, ConfiguredSurfaceBuilders.GRASS, true));
        Biomes.register(21, BuiltInBiomes.JUNGLE, DefaultBiomeCreator.createJungle());
        Biomes.register(22, BuiltInBiomes.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
        Biomes.register(23, BuiltInBiomes.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
        Biomes.register(24, BuiltInBiomes.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        Biomes.register(25, BuiltInBiomes.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1f, 0.8f, 0.2f, 0.3f, 4159204, false, true));
        Biomes.register(26, BuiltInBiomes.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.05f, 0.3f, 4020182, true, false));
        Biomes.register(27, BuiltInBiomes.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1f, 0.2f, false));
        Biomes.register(28, BuiltInBiomes.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45f, 0.3f, false));
        Biomes.register(29, BuiltInBiomes.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1f, 0.2f, false));
        Biomes.register(30, BuiltInBiomes.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, true, false, false, true));
        Biomes.register(31, BuiltInBiomes.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, true, false, false, false));
        Biomes.register(32, BuiltInBiomes.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.3f, false));
        Biomes.register(33, BuiltInBiomes.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45f, 0.3f, 0.3f, false));
        Biomes.register(34, BuiltInBiomes.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRASS, true));
        Biomes.register(35, BuiltInBiomes.SAVANNA, DefaultBiomeCreator.createSavanna(0.125f, 0.05f, 1.2f, false, false));
        Biomes.register(36, BuiltInBiomes.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
        Biomes.register(37, BuiltInBiomes.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1f, 0.2f, false));
        Biomes.register(38, BuiltInBiomes.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5f, 0.025f));
        Biomes.register(39, BuiltInBiomes.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5f, 0.025f, true));
        Biomes.register(40, BuiltInBiomes.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
        Biomes.register(41, BuiltInBiomes.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
        Biomes.register(42, BuiltInBiomes.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
        Biomes.register(43, BuiltInBiomes.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
        Biomes.register(44, BuiltInBiomes.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        Biomes.register(45, BuiltInBiomes.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        Biomes.register(46, BuiltInBiomes.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        Biomes.register(47, BuiltInBiomes.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        Biomes.register(48, BuiltInBiomes.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        Biomes.register(49, BuiltInBiomes.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        Biomes.register(50, BuiltInBiomes.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        THE_VOID = Biomes.register(127, BuiltInBiomes.THE_VOID, DefaultBiomeCreator.createTheVoid());
        Biomes.register(129, BuiltInBiomes.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
        Biomes.register(130, BuiltInBiomes.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225f, 0.25f, false, false, false));
        Biomes.register(131, BuiltInBiomes.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        Biomes.register(132, BuiltInBiomes.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
        Biomes.register(133, BuiltInBiomes.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, false, true, false, false));
        Biomes.register(134, BuiltInBiomes.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1f, 0.3f, true));
        Biomes.register(140, BuiltInBiomes.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425f, 0.45000002f, true, false));
        Biomes.register(149, BuiltInBiomes.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
        Biomes.register(151, BuiltInBiomes.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
        Biomes.register(155, BuiltInBiomes.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2f, 0.4f, true));
        Biomes.register(156, BuiltInBiomes.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55f, 0.5f, true));
        Biomes.register(157, BuiltInBiomes.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2f, 0.4f, true));
        Biomes.register(158, BuiltInBiomes.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, true, true, false, false));
        Biomes.register(160, BuiltInBiomes.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        Biomes.register(161, BuiltInBiomes.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        Biomes.register(162, BuiltInBiomes.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        Biomes.register(163, BuiltInBiomes.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625f, 1.225f, 1.1f, true, true));
        Biomes.register(164, BuiltInBiomes.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05f, 1.2125001f, 1.0f, true, true));
        Biomes.register(165, BuiltInBiomes.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
        Biomes.register(166, BuiltInBiomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45f, 0.3f));
        Biomes.register(167, BuiltInBiomes.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45f, 0.3f, true));
        Biomes.register(168, BuiltInBiomes.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        Biomes.register(169, BuiltInBiomes.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
        Biomes.register(170, BuiltInBiomes.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
        Biomes.register(171, BuiltInBiomes.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
        Biomes.register(172, BuiltInBiomes.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
        Biomes.register(173, BuiltInBiomes.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
    }
}

