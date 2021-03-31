/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeIds;
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
        BuiltinBiomes.register(BiomeIds.OCEAN, BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        PLAINS = BuiltinBiomes.register(BiomeIds.PLAINS, BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
        BuiltinBiomes.register(BiomeIds.DESERT, BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(0.125f, 0.05f, true, true, true));
        BuiltinBiomes.register(BiomeIds.MOUNTAINS, BiomeKeys.MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.MOUNTAIN, false));
        BuiltinBiomes.register(BiomeIds.FOREST, BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(0.1f, 0.2f));
        BuiltinBiomes.register(BiomeIds.TAIGA, BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, false, false, true, false));
        BuiltinBiomes.register(BiomeIds.SWAMP, BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(-0.2f, 0.1f, false));
        BuiltinBiomes.register(BiomeIds.RIVER, BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.5f, 4159204, false));
        BuiltinBiomes.register(8, BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
        BuiltinBiomes.register(9, BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
        BuiltinBiomes.register(BiomeIds.FROZEN_OCEAN, BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        BuiltinBiomes.register(BiomeIds.FROZEN_RIVER, BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(-0.5f, 0.0f, 0.0f, 3750089, true));
        BuiltinBiomes.register(BiomeIds.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, DefaultBiomeCreator.createSnowyTundra(0.125f, 0.05f, false, false));
        BuiltinBiomes.register(BiomeIds.SNOWY_MOUNTAINS, BiomeKeys.SNOWY_MOUNTAINS, DefaultBiomeCreator.createSnowyTundra(0.45f, 0.3f, false, true));
        BuiltinBiomes.register(BiomeIds.MUSHROOM_FIELDS, BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields(0.2f, 0.3f));
        BuiltinBiomes.register(BiomeIds.MUSHROOM_FIELD_SHORE, BiomeKeys.MUSHROOM_FIELD_SHORE, DefaultBiomeCreator.createMushroomFields(0.0f, 0.025f));
        BuiltinBiomes.register(BiomeIds.BEACH, BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.8f, 0.4f, 4159204, false, false));
        BuiltinBiomes.register(BiomeIds.DESERT_HILLS, BiomeKeys.DESERT_HILLS, DefaultBiomeCreator.createDesert(0.45f, 0.3f, false, true, false));
        BuiltinBiomes.register(BiomeIds.WOODED_HILLS, BiomeKeys.WOODED_HILLS, DefaultBiomeCreator.createNormalForest(0.45f, 0.3f));
        BuiltinBiomes.register(BiomeIds.TAIGA_HILLS, BiomeKeys.TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, false, false, false, false));
        BuiltinBiomes.register(BiomeIds.MOUNTAIN_EDGE, BiomeKeys.MOUNTAIN_EDGE, DefaultBiomeCreator.createMountains(0.8f, 0.3f, ConfiguredSurfaceBuilders.GRASS, true));
        BuiltinBiomes.register(BiomeIds.JUNGLE, BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
        BuiltinBiomes.register(BiomeIds.JUNGLE_HILLS, BiomeKeys.JUNGLE_HILLS, DefaultBiomeCreator.createJungleHills());
        BuiltinBiomes.register(BiomeIds.JUNGLE_EDGE, BiomeKeys.JUNGLE_EDGE, DefaultBiomeCreator.createJungleEdge());
        BuiltinBiomes.register(BiomeIds.DEEP_OCEAN, BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        BuiltinBiomes.register(BiomeIds.STONE_SHORE, BiomeKeys.STONE_SHORE, DefaultBiomeCreator.createBeach(0.1f, 0.8f, 0.2f, 0.3f, 4159204, false, true));
        BuiltinBiomes.register(BiomeIds.SNOWY_BEACH, BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.0f, 0.025f, 0.05f, 0.3f, 4020182, true, false));
        BuiltinBiomes.register(BiomeIds.BIRCH_FOREST, BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.1f, 0.2f, false));
        BuiltinBiomes.register(BiomeIds.BIRCH_FOREST_HILLS, BiomeKeys.BIRCH_FOREST_HILLS, DefaultBiomeCreator.createBirchForest(0.45f, 0.3f, false));
        BuiltinBiomes.register(BiomeIds.DARK_FOREST, BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(0.1f, 0.2f, false));
        BuiltinBiomes.register(BiomeIds.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(0.2f, 0.2f, true, false, false, true));
        BuiltinBiomes.register(BiomeIds.SNOWY_TAIGA_HILLS, BiomeKeys.SNOWY_TAIGA_HILLS, DefaultBiomeCreator.createTaiga(0.45f, 0.3f, true, false, false, false));
        BuiltinBiomes.register(BiomeIds.GIANT_TREE_TAIGA, BiomeKeys.GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.3f, false));
        BuiltinBiomes.register(BiomeIds.GIANT_TREE_TAIGA_HILLS, BiomeKeys.GIANT_TREE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.45f, 0.3f, 0.3f, false));
        BuiltinBiomes.register(BiomeIds.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRASS, true));
        BuiltinBiomes.register(BiomeIds.SAVANNA, BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(0.125f, 0.05f, 1.2f, false, false));
        BuiltinBiomes.register(BiomeIds.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
        BuiltinBiomes.register(BiomeIds.BADLANDS, BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(0.1f, 0.2f, false));
        BuiltinBiomes.register(BiomeIds.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5f, 0.025f));
        BuiltinBiomes.register(BiomeIds.BADLANDS_PLATEAU, BiomeKeys.BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(1.5f, 0.025f, true));
        BuiltinBiomes.register(40, BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
        BuiltinBiomes.register(41, BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
        BuiltinBiomes.register(42, BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
        BuiltinBiomes.register(43, BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
        BuiltinBiomes.register(BiomeIds.WARM_OCEAN, BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        BuiltinBiomes.register(BiomeIds.LUKEWARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        BuiltinBiomes.register(BiomeIds.COLD_OCEAN, BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        BuiltinBiomes.register(BiomeIds.DEEP_WARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        BuiltinBiomes.register(BiomeIds.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        BuiltinBiomes.register(BiomeIds.DEEP_COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        BuiltinBiomes.register(BiomeIds.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        THE_VOID = BuiltinBiomes.register(127, BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());
        BuiltinBiomes.register(BiomeIds.SUNFLOWER_PLAINS, BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
        BuiltinBiomes.register(BiomeIds.DESERT_LAKES, BiomeKeys.DESERT_LAKES, DefaultBiomeCreator.createDesert(0.225f, 0.25f, false, false, false));
        BuiltinBiomes.register(BiomeIds.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        BuiltinBiomes.register(BiomeIds.FLOWER_FOREST, BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
        BuiltinBiomes.register(BiomeIds.TAIGA_MOUNTAINS, BiomeKeys.TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, false, true, false, false));
        BuiltinBiomes.register(BiomeIds.SWAMP_HILLS, BiomeKeys.SWAMP_HILLS, DefaultBiomeCreator.createSwamp(-0.1f, 0.3f, true));
        BuiltinBiomes.register(BiomeIds.ICE_SPIKES, BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(0.425f, 0.45000002f, true, false));
        BuiltinBiomes.register(BiomeIds.MODIFIED_JUNGLE, BiomeKeys.MODIFIED_JUNGLE, DefaultBiomeCreator.createModifiedJungle());
        BuiltinBiomes.register(BiomeIds.MODIFIED_JUNGLE_EDGE, BiomeKeys.MODIFIED_JUNGLE_EDGE, DefaultBiomeCreator.createModifiedJungleEdge());
        BuiltinBiomes.register(BiomeIds.TALL_BIRCH_FOREST, BiomeKeys.TALL_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(0.2f, 0.4f, true));
        BuiltinBiomes.register(BiomeIds.TALL_BIRCH_HILLS, BiomeKeys.TALL_BIRCH_HILLS, DefaultBiomeCreator.createBirchForest(0.55f, 0.5f, true));
        BuiltinBiomes.register(BiomeIds.DARK_FOREST_HILLS, BiomeKeys.DARK_FOREST_HILLS, DefaultBiomeCreator.createDarkForest(0.2f, 0.4f, true));
        BuiltinBiomes.register(BiomeIds.SNOWY_TAIGA_MOUNTAINS, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, DefaultBiomeCreator.createTaiga(0.3f, 0.4f, true, true, false, false));
        BuiltinBiomes.register(BiomeIds.GIANT_SPRUCE_TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        BuiltinBiomes.register(BiomeIds.GIANT_SPRUCE_TAIGA_HILLS, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, DefaultBiomeCreator.createGiantTreeTaiga(0.2f, 0.2f, 0.25f, true));
        BuiltinBiomes.register(BiomeIds.MODIFIED_GRAVELLY_MOUNTAINS, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, DefaultBiomeCreator.createMountains(1.0f, 0.5f, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        BuiltinBiomes.register(BiomeIds.SHATTERED_SAVANNA, BiomeKeys.SHATTERED_SAVANNA, DefaultBiomeCreator.createSavanna(0.3625f, 1.225f, 1.1f, true, true));
        BuiltinBiomes.register(BiomeIds.SHATTERED_SAVANNA_PLATEAU, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(1.05f, 1.2125001f, 1.0f, true, true));
        BuiltinBiomes.register(BiomeIds.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
        BuiltinBiomes.register(BiomeIds.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45f, 0.3f));
        BuiltinBiomes.register(BiomeIds.MODIFIED_BADLANDS_PLATEAU, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DefaultBiomeCreator.createNormalBadlands(0.45f, 0.3f, true));
        BuiltinBiomes.register(BiomeIds.BAMBOO_JUNGLE, BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        BuiltinBiomes.register(BiomeIds.BAMBOO_JUNGLE_HILLS, BiomeKeys.BAMBOO_JUNGLE_HILLS, DefaultBiomeCreator.createBambooJungleHills());
        BuiltinBiomes.register(170, BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
        BuiltinBiomes.register(171, BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
        BuiltinBiomes.register(172, BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
        BuiltinBiomes.register(173, BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
        BuiltinBiomes.register(174, BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
        BuiltinBiomes.register(175, BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
    }
}

