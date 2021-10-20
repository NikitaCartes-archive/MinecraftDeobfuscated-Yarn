/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.TheEndBiomeCreator;
import net.minecraft.world.biome.TheNetherBiomeCreator;

public abstract class BuiltinBiomes {
    @Deprecated
    public static final Biome THE_VOID = BuiltinBiomes.register(BiomeKeys.THE_VOID, OverworldBiomeCreator.createTheVoid());
    @Deprecated
    public static final Biome PLAINS = BuiltinBiomes.register(BiomeKeys.PLAINS, OverworldBiomeCreator.createPlains(false, false, false));

    private static Biome register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, key, biome);
    }

    static {
        BuiltinBiomes.register(BiomeKeys.SUNFLOWER_PLAINS, OverworldBiomeCreator.createPlains(true, false, false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_PLAINS, OverworldBiomeCreator.createPlains(false, true, false));
        BuiltinBiomes.register(BiomeKeys.ICE_SPIKES, OverworldBiomeCreator.createPlains(false, true, true));
        BuiltinBiomes.register(BiomeKeys.DESERT, OverworldBiomeCreator.createDesert());
        BuiltinBiomes.register(BiomeKeys.SWAMP, OverworldBiomeCreator.createSwamp());
        BuiltinBiomes.register(BiomeKeys.FOREST, OverworldBiomeCreator.createNormalForest(false, false, false));
        BuiltinBiomes.register(BiomeKeys.FLOWER_FOREST, OverworldBiomeCreator.createNormalForest(false, false, true));
        BuiltinBiomes.register(BiomeKeys.BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, false, false));
        BuiltinBiomes.register(BiomeKeys.DARK_FOREST, OverworldBiomeCreator.createDarkForest());
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, OverworldBiomeCreator.createNormalForest(true, true, false));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, OverworldBiomeCreator.createOldGrowthPineTaiga(false));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomeCreator.createOldGrowthPineTaiga(true));
        BuiltinBiomes.register(BiomeKeys.TAIGA, OverworldBiomeCreator.createTaiga(false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_TAIGA, OverworldBiomeCreator.createTaiga(true));
        BuiltinBiomes.register(BiomeKeys.SAVANNA, OverworldBiomeCreator.createSavanna(false, false));
        BuiltinBiomes.register(BiomeKeys.SAVANNA_PLATEAU, OverworldBiomeCreator.createSavanna(false, true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomeCreator.createWindsweptHills(false));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_FOREST, OverworldBiomeCreator.createWindsweptHills(true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_SAVANNA, OverworldBiomeCreator.createSavanna(true, false));
        BuiltinBiomes.register(BiomeKeys.JUNGLE, OverworldBiomeCreator.createJungle());
        BuiltinBiomes.register(BiomeKeys.SPARSE_JUNGLE, OverworldBiomeCreator.createSparseJungle());
        BuiltinBiomes.register(BiomeKeys.BAMBOO_JUNGLE, OverworldBiomeCreator.createNormalBambooJungle());
        BuiltinBiomes.register(BiomeKeys.BADLANDS, OverworldBiomeCreator.createNormalBadlands(false));
        BuiltinBiomes.register(BiomeKeys.ERODED_BADLANDS, OverworldBiomeCreator.createNormalBadlands(false));
        BuiltinBiomes.register(BiomeKeys.WOODED_BADLANDS, OverworldBiomeCreator.createNormalBadlands(true));
        BuiltinBiomes.register(BiomeKeys.MEADOW, OverworldBiomeCreator.createMeadow());
        BuiltinBiomes.register(BiomeKeys.GROVE, OverworldBiomeCreator.createGrove());
        BuiltinBiomes.register(BiomeKeys.SNOWY_SLOPES, OverworldBiomeCreator.createSnowySlopes());
        BuiltinBiomes.register(BiomeKeys.FROZEN_PEAKS, OverworldBiomeCreator.createFrozenPeaks());
        BuiltinBiomes.register(BiomeKeys.JAGGED_PEAKS, OverworldBiomeCreator.createJaggedPeaks());
        BuiltinBiomes.register(BiomeKeys.STONY_PEAKS, OverworldBiomeCreator.createStonyPeaks());
        BuiltinBiomes.register(BiomeKeys.RIVER, OverworldBiomeCreator.createRiver(false));
        BuiltinBiomes.register(BiomeKeys.FROZEN_RIVER, OverworldBiomeCreator.createRiver(true));
        BuiltinBiomes.register(BiomeKeys.BEACH, OverworldBiomeCreator.createBeach(false, false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_BEACH, OverworldBiomeCreator.createBeach(true, false));
        BuiltinBiomes.register(BiomeKeys.STONY_SHORE, OverworldBiomeCreator.createBeach(false, true));
        BuiltinBiomes.register(BiomeKeys.WARM_OCEAN, OverworldBiomeCreator.createWarmOcean());
        BuiltinBiomes.register(BiomeKeys.DEEP_WARM_OCEAN, OverworldBiomeCreator.createDeepWarmOcean());
        BuiltinBiomes.register(BiomeKeys.LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_LUKEWARM_OCEAN, OverworldBiomeCreator.createLukewarmOcean(true));
        BuiltinBiomes.register(BiomeKeys.OCEAN, OverworldBiomeCreator.createNormalOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_OCEAN, OverworldBiomeCreator.createNormalOcean(true));
        BuiltinBiomes.register(BiomeKeys.COLD_OCEAN, OverworldBiomeCreator.createColdOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_COLD_OCEAN, OverworldBiomeCreator.createColdOcean(true));
        BuiltinBiomes.register(BiomeKeys.FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_FROZEN_OCEAN, OverworldBiomeCreator.createFrozenOcean(true));
        BuiltinBiomes.register(BiomeKeys.MUSHROOM_FIELDS, OverworldBiomeCreator.createMushroomFields());
        BuiltinBiomes.register(BiomeKeys.DRIPSTONE_CAVES, OverworldBiomeCreator.createDripstoneCaves());
        BuiltinBiomes.register(BiomeKeys.LUSH_CAVES, OverworldBiomeCreator.createLushCaves());
        BuiltinBiomes.register(BiomeKeys.NETHER_WASTES, TheNetherBiomeCreator.createNetherWastes());
        BuiltinBiomes.register(BiomeKeys.WARPED_FOREST, TheNetherBiomeCreator.createWarpedForest());
        BuiltinBiomes.register(BiomeKeys.CRIMSON_FOREST, TheNetherBiomeCreator.createCrimsonForest());
        BuiltinBiomes.register(BiomeKeys.SOUL_SAND_VALLEY, TheNetherBiomeCreator.createSoulSandValley());
        BuiltinBiomes.register(BiomeKeys.BASALT_DELTAS, TheNetherBiomeCreator.createBasaltDeltas());
        BuiltinBiomes.register(BiomeKeys.THE_END, TheEndBiomeCreator.createTheEnd());
        BuiltinBiomes.register(BiomeKeys.END_HIGHLANDS, TheEndBiomeCreator.createEndHighlands());
        BuiltinBiomes.register(BiomeKeys.END_MIDLANDS, TheEndBiomeCreator.createEndMidlands());
        BuiltinBiomes.register(BiomeKeys.SMALL_END_ISLANDS, TheEndBiomeCreator.createSmallEndIslands());
        BuiltinBiomes.register(BiomeKeys.END_BARRENS, TheEndBiomeCreator.createEndBarrens());
    }
}

