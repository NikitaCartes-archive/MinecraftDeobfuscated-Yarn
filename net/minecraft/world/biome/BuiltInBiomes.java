/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
    public static final Biome PLAINS;
    public static final Biome THE_VOID;

    private static Biome register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, key, biome);
    }

    static {
        BuiltinBiomes.register(BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        PLAINS = BuiltinBiomes.register(BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
        BuiltinBiomes.register(BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_HILLS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.MOUNTAIN, false));
        BuiltinBiomes.register(BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest());
        BuiltinBiomes.register(BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false, false));
        BuiltinBiomes.register(BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(false));
        BuiltinBiomes.register(BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(0.5f, 4159204, false));
        BuiltinBiomes.register(BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
        BuiltinBiomes.register(BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
        BuiltinBiomes.register(BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        BuiltinBiomes.register(BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(0.0f, 3750089, true));
        BuiltinBiomes.register(BiomeKeys.SNOWY_PLAINS, DefaultBiomeCreator.createSnowyTundra(false));
        BuiltinBiomes.register(BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
        BuiltinBiomes.register(BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.8f, 0.4f, 4159204, false, false));
        BuiltinBiomes.register(BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
        BuiltinBiomes.register(BiomeKeys.SPARSE_JUNGLE, DefaultBiomeCreator.createJungleEdge());
        BuiltinBiomes.register(BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        BuiltinBiomes.register(BiomeKeys.STONY_SHORE, DefaultBiomeCreator.createBeach(0.2f, 0.3f, 4159204, false, true));
        BuiltinBiomes.register(BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.05f, 0.3f, 4020182, true, false));
        BuiltinBiomes.register(BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(false));
        BuiltinBiomes.register(BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true, false));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.3f, false));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_FOREST, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
        BuiltinBiomes.register(BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(1.2f, false));
        BuiltinBiomes.register(BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
        BuiltinBiomes.register(BiomeKeys.WOODED_BADLANDS, DefaultBiomeCreator.createWoodedBadlandsPlateau());
        BuiltinBiomes.register(BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands());
        BuiltinBiomes.register(BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
        BuiltinBiomes.register(BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
        BuiltinBiomes.register(BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
        BuiltinBiomes.register(BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
        BuiltinBiomes.register(BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        BuiltinBiomes.register(BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        BuiltinBiomes.register(BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        BuiltinBiomes.register(BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        BuiltinBiomes.register(BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        BuiltinBiomes.register(BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        THE_VOID = BuiltinBiomes.register(BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());
        BuiltinBiomes.register(BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
        BuiltinBiomes.register(BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
        BuiltinBiomes.register(BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(true));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(true));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.25f, true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_SAVANNA, DefaultBiomeCreator.createSavanna(1.1f, true));
        BuiltinBiomes.register(BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
        BuiltinBiomes.register(BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        BuiltinBiomes.register(BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
        BuiltinBiomes.register(BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
        BuiltinBiomes.register(BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
        BuiltinBiomes.register(BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
        BuiltinBiomes.register(BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
        BuiltinBiomes.register(BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
        BuiltinBiomes.register(BiomeKeys.MEADOW, DefaultBiomeCreator.composeMeadowSettings());
        BuiltinBiomes.register(BiomeKeys.GROVE, DefaultBiomeCreator.composeGroveSettings());
        BuiltinBiomes.register(BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.composeSnowySlopesSettings());
        BuiltinBiomes.register(BiomeKeys.FROZEN_PEAKS, DefaultBiomeCreator.composeSnowcappedPeaksSettings());
        BuiltinBiomes.register(BiomeKeys.JAGGED_PEAKS, DefaultBiomeCreator.composeLoftyPeaksSettings());
        BuiltinBiomes.register(BiomeKeys.STONY_PEAKS, DefaultBiomeCreator.composeStonyPeaksSettings());
    }
}

