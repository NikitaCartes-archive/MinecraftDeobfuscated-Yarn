/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public abstract class BiomeKeys {
    public static final RegistryKey<Biome> THE_VOID = BiomeKeys.register("the_void");
    public static final RegistryKey<Biome> PLAINS = BiomeKeys.register("plains");
    public static final RegistryKey<Biome> SUNFLOWER_PLAINS = BiomeKeys.register("sunflower_plains");
    public static final RegistryKey<Biome> SNOWY_PLAINS = BiomeKeys.register("snowy_plains");
    public static final RegistryKey<Biome> ICE_SPIKES = BiomeKeys.register("ice_spikes");
    public static final RegistryKey<Biome> DESERT = BiomeKeys.register("desert");
    public static final RegistryKey<Biome> SWAMP = BiomeKeys.register("swamp");
    public static final RegistryKey<Biome> FOREST = BiomeKeys.register("forest");
    public static final RegistryKey<Biome> FLOWER_FOREST = BiomeKeys.register("flower_forest");
    public static final RegistryKey<Biome> BIRCH_FOREST = BiomeKeys.register("birch_forest");
    public static final RegistryKey<Biome> DARK_FOREST = BiomeKeys.register("dark_forest");
    public static final RegistryKey<Biome> OLD_GROWTH_BIRCH_FOREST = BiomeKeys.register("old_growth_birch_forest");
    public static final RegistryKey<Biome> OLD_GROWTH_PINE_TAIGA = BiomeKeys.register("old_growth_pine_taiga");
    public static final RegistryKey<Biome> OLD_GROWTH_SPRUCE_TAIGA = BiomeKeys.register("old_growth_spruce_taiga");
    public static final RegistryKey<Biome> TAIGA = BiomeKeys.register("taiga");
    public static final RegistryKey<Biome> SNOWY_TAIGA = BiomeKeys.register("snowy_taiga");
    public static final RegistryKey<Biome> SAVANNA = BiomeKeys.register("savanna");
    public static final RegistryKey<Biome> SAVANNA_PLATEAU = BiomeKeys.register("savanna_plateau");
    public static final RegistryKey<Biome> WINDSWEPT_HILLS = BiomeKeys.register("windswept_hills");
    public static final RegistryKey<Biome> WINDSWEPT_GRAVELLY_HILLS = BiomeKeys.register("windswept_gravelly_hills");
    public static final RegistryKey<Biome> WINDSWEPT_FOREST = BiomeKeys.register("windswept_forest");
    public static final RegistryKey<Biome> WINDSWEPT_SAVANNA = BiomeKeys.register("windswept_savanna");
    public static final RegistryKey<Biome> JUNGLE = BiomeKeys.register("jungle");
    public static final RegistryKey<Biome> SPARSE_JUNGLE = BiomeKeys.register("sparse_jungle");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE = BiomeKeys.register("bamboo_jungle");
    public static final RegistryKey<Biome> BADLANDS = BiomeKeys.register("badlands");
    public static final RegistryKey<Biome> ERODED_BADLANDS = BiomeKeys.register("eroded_badlands");
    public static final RegistryKey<Biome> WOODED_BADLANDS = BiomeKeys.register("wooded_badlands");
    public static final RegistryKey<Biome> MEADOW = BiomeKeys.register("meadow");
    public static final RegistryKey<Biome> GROVE = BiomeKeys.register("grove");
    public static final RegistryKey<Biome> SNOWY_SLOPES = BiomeKeys.register("snowy_slopes");
    public static final RegistryKey<Biome> FROZEN_PEAKS = BiomeKeys.register("frozen_peaks");
    public static final RegistryKey<Biome> JAGGED_PEAKS = BiomeKeys.register("jagged_peaks");
    public static final RegistryKey<Biome> STONY_PEAKS = BiomeKeys.register("stony_peaks");
    public static final RegistryKey<Biome> RIVER = BiomeKeys.register("river");
    public static final RegistryKey<Biome> FROZEN_RIVER = BiomeKeys.register("frozen_river");
    public static final RegistryKey<Biome> BEACH = BiomeKeys.register("beach");
    public static final RegistryKey<Biome> SNOWY_BEACH = BiomeKeys.register("snowy_beach");
    public static final RegistryKey<Biome> STONY_SHORE = BiomeKeys.register("stony_shore");
    public static final RegistryKey<Biome> WARM_OCEAN = BiomeKeys.register("warm_ocean");
    public static final RegistryKey<Biome> DEEP_WARM_OCEAN = BiomeKeys.register("deep_warm_ocean");
    public static final RegistryKey<Biome> LUKEWARM_OCEAN = BiomeKeys.register("lukewarm_ocean");
    public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = BiomeKeys.register("deep_lukewarm_ocean");
    public static final RegistryKey<Biome> OCEAN = BiomeKeys.register("ocean");
    public static final RegistryKey<Biome> DEEP_OCEAN = BiomeKeys.register("deep_ocean");
    public static final RegistryKey<Biome> COLD_OCEAN = BiomeKeys.register("cold_ocean");
    public static final RegistryKey<Biome> DEEP_COLD_OCEAN = BiomeKeys.register("deep_cold_ocean");
    public static final RegistryKey<Biome> FROZEN_OCEAN = BiomeKeys.register("frozen_ocean");
    public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = BiomeKeys.register("deep_frozen_ocean");
    public static final RegistryKey<Biome> MUSHROOM_FIELDS = BiomeKeys.register("mushroom_fields");
    public static final RegistryKey<Biome> DRIPSTONE_CAVES = BiomeKeys.register("dripstone_caves");
    public static final RegistryKey<Biome> LUSH_CAVES = BiomeKeys.register("lush_caves");
    public static final RegistryKey<Biome> NETHER_WASTES = BiomeKeys.register("nether_wastes");
    public static final RegistryKey<Biome> WARPED_FOREST = BiomeKeys.register("warped_forest");
    public static final RegistryKey<Biome> CRIMSON_FOREST = BiomeKeys.register("crimson_forest");
    public static final RegistryKey<Biome> SOUL_SAND_VALLEY = BiomeKeys.register("soul_sand_valley");
    public static final RegistryKey<Biome> BASALT_DELTAS = BiomeKeys.register("basalt_deltas");
    public static final RegistryKey<Biome> THE_END = BiomeKeys.register("the_end");
    public static final RegistryKey<Biome> END_HIGHLANDS = BiomeKeys.register("end_highlands");
    public static final RegistryKey<Biome> END_MIDLANDS = BiomeKeys.register("end_midlands");
    public static final RegistryKey<Biome> SMALL_END_ISLANDS = BiomeKeys.register("small_end_islands");
    public static final RegistryKey<Biome> END_BARRENS = BiomeKeys.register("end_barrens");

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(name));
    }
}

