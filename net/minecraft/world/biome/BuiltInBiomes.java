/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.class_6726;
import net.minecraft.class_6727;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.DefaultBiomeCreator;

public abstract class BuiltinBiomes {
    @Deprecated
    public static final Biome THE_VOID = BuiltinBiomes.register(BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());
    @Deprecated
    public static final Biome PLAINS = BuiltinBiomes.register(BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false, false, false));

    private static Biome register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.set(BuiltinRegistries.BIOME, key, biome);
    }

    static {
        BuiltinBiomes.register(BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true, false, false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_PLAINS, DefaultBiomeCreator.createPlains(false, true, false));
        BuiltinBiomes.register(BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createPlains(false, true, true));
        BuiltinBiomes.register(BiomeKeys.DESERT, DefaultBiomeCreator.createDesert());
        BuiltinBiomes.register(BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp());
        BuiltinBiomes.register(BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(false, false, false));
        BuiltinBiomes.register(BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createNormalForest(false, false, true));
        BuiltinBiomes.register(BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createNormalForest(true, false, false));
        BuiltinBiomes.register(BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest());
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, DefaultBiomeCreator.createNormalForest(true, true, false));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, DefaultBiomeCreator.createOldGrowthPineTaiga(false));
        BuiltinBiomes.register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, DefaultBiomeCreator.createOldGrowthPineTaiga(true));
        BuiltinBiomes.register(BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true));
        BuiltinBiomes.register(BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(false, false));
        BuiltinBiomes.register(BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(false, true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_HILLS, DefaultBiomeCreator.createWindsweptHills(false));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, DefaultBiomeCreator.createWindsweptHills(false));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_FOREST, DefaultBiomeCreator.createWindsweptHills(true));
        BuiltinBiomes.register(BiomeKeys.WINDSWEPT_SAVANNA, DefaultBiomeCreator.createSavanna(true, false));
        BuiltinBiomes.register(BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
        BuiltinBiomes.register(BiomeKeys.SPARSE_JUNGLE, DefaultBiomeCreator.createSparseJungle());
        BuiltinBiomes.register(BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
        BuiltinBiomes.register(BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(false));
        BuiltinBiomes.register(BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createNormalBadlands(false));
        BuiltinBiomes.register(BiomeKeys.WOODED_BADLANDS, DefaultBiomeCreator.createNormalBadlands(true));
        BuiltinBiomes.register(BiomeKeys.MEADOW, DefaultBiomeCreator.createMeadow());
        BuiltinBiomes.register(BiomeKeys.GROVE, DefaultBiomeCreator.createGrove());
        BuiltinBiomes.register(BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.createSnowySlopes());
        BuiltinBiomes.register(BiomeKeys.FROZEN_PEAKS, DefaultBiomeCreator.createFrozenPeaks());
        BuiltinBiomes.register(BiomeKeys.JAGGED_PEAKS, DefaultBiomeCreator.createJaggedPeaks());
        BuiltinBiomes.register(BiomeKeys.STONY_PEAKS, DefaultBiomeCreator.createStonyPeaks());
        BuiltinBiomes.register(BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(false));
        BuiltinBiomes.register(BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(true));
        BuiltinBiomes.register(BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(false, false));
        BuiltinBiomes.register(BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(true, false));
        BuiltinBiomes.register(BiomeKeys.STONY_SHORE, DefaultBiomeCreator.createBeach(false, true));
        BuiltinBiomes.register(BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
        BuiltinBiomes.register(BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
        BuiltinBiomes.register(BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
        BuiltinBiomes.register(BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
        BuiltinBiomes.register(BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
        BuiltinBiomes.register(BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
        BuiltinBiomes.register(BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
        BuiltinBiomes.register(BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
        BuiltinBiomes.register(BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
        BuiltinBiomes.register(BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
        BuiltinBiomes.register(BiomeKeys.NETHER_WASTES, class_6727.method_39146());
        BuiltinBiomes.register(BiomeKeys.WARPED_FOREST, class_6727.method_39150());
        BuiltinBiomes.register(BiomeKeys.CRIMSON_FOREST, class_6727.method_39149());
        BuiltinBiomes.register(BiomeKeys.SOUL_SAND_VALLEY, class_6727.method_39147());
        BuiltinBiomes.register(BiomeKeys.BASALT_DELTAS, class_6727.method_39148());
        BuiltinBiomes.register(BiomeKeys.THE_END, class_6726.method_39142());
        BuiltinBiomes.register(BiomeKeys.END_HIGHLANDS, class_6726.method_39144());
        BuiltinBiomes.register(BiomeKeys.END_MIDLANDS, class_6726.method_39143());
        BuiltinBiomes.register(BiomeKeys.SMALL_END_ISLANDS, class_6726.method_39145());
        BuiltinBiomes.register(BiomeKeys.END_BARRENS, class_6726.method_39140());
    }
}

