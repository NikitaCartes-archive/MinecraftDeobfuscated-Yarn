/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public abstract class BiomeKeys {
    public static final RegistryKey<Biome> OCEAN = BiomeKeys.register("ocean");
    public static final RegistryKey<Biome> PLAINS = BiomeKeys.register("plains");
    public static final RegistryKey<Biome> DESERT = BiomeKeys.register("desert");
    public static final RegistryKey<Biome> MOUNTAINS = BiomeKeys.register("mountains");
    public static final RegistryKey<Biome> FOREST = BiomeKeys.register("forest");
    public static final RegistryKey<Biome> TAIGA = BiomeKeys.register("taiga");
    public static final RegistryKey<Biome> SWAMP = BiomeKeys.register("swamp");
    public static final RegistryKey<Biome> RIVER = BiomeKeys.register("river");
    public static final RegistryKey<Biome> NETHER_WASTES = BiomeKeys.register("nether_wastes");
    public static final RegistryKey<Biome> THE_END = BiomeKeys.register("the_end");
    public static final RegistryKey<Biome> FROZEN_OCEAN = BiomeKeys.register("frozen_ocean");
    public static final RegistryKey<Biome> FROZEN_RIVER = BiomeKeys.register("frozen_river");
    public static final RegistryKey<Biome> SNOWY_TUNDRA = BiomeKeys.register("snowy_tundra");
    public static final RegistryKey<Biome> SNOWY_MOUNTAINS = BiomeKeys.register("snowy_mountains");
    public static final RegistryKey<Biome> MUSHROOM_FIELDS = BiomeKeys.register("mushroom_fields");
    public static final RegistryKey<Biome> MUSHROOM_FIELD_SHORE = BiomeKeys.register("mushroom_field_shore");
    public static final RegistryKey<Biome> BEACH = BiomeKeys.register("beach");
    public static final RegistryKey<Biome> DESERT_HILLS = BiomeKeys.register("desert_hills");
    public static final RegistryKey<Biome> WOODED_HILLS = BiomeKeys.register("wooded_hills");
    public static final RegistryKey<Biome> TAIGA_HILLS = BiomeKeys.register("taiga_hills");
    public static final RegistryKey<Biome> MOUNTAIN_EDGE = BiomeKeys.register("mountain_edge");
    public static final RegistryKey<Biome> JUNGLE = BiomeKeys.register("jungle");
    public static final RegistryKey<Biome> JUNGLE_HILLS = BiomeKeys.register("jungle_hills");
    public static final RegistryKey<Biome> JUNGLE_EDGE = BiomeKeys.register("jungle_edge");
    public static final RegistryKey<Biome> DEEP_OCEAN = BiomeKeys.register("deep_ocean");
    public static final RegistryKey<Biome> STONE_SHORE = BiomeKeys.register("stone_shore");
    public static final RegistryKey<Biome> SNOWY_BEACH = BiomeKeys.register("snowy_beach");
    public static final RegistryKey<Biome> BIRCH_FOREST = BiomeKeys.register("birch_forest");
    public static final RegistryKey<Biome> BIRCH_FOREST_HILLS = BiomeKeys.register("birch_forest_hills");
    public static final RegistryKey<Biome> DARK_FOREST = BiomeKeys.register("dark_forest");
    public static final RegistryKey<Biome> SNOWY_TAIGA = BiomeKeys.register("snowy_taiga");
    public static final RegistryKey<Biome> SNOWY_TAIGA_HILLS = BiomeKeys.register("snowy_taiga_hills");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA = BiomeKeys.register("giant_tree_taiga");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA_HILLS = BiomeKeys.register("giant_tree_taiga_hills");
    public static final RegistryKey<Biome> WOODED_MOUNTAINS = BiomeKeys.register("wooded_mountains");
    public static final RegistryKey<Biome> SAVANNA = BiomeKeys.register("savanna");
    public static final RegistryKey<Biome> SAVANNA_PLATEAU = BiomeKeys.register("savanna_plateau");
    public static final RegistryKey<Biome> BADLANDS = BiomeKeys.register("badlands");
    public static final RegistryKey<Biome> WOODED_BADLANDS_PLATEAU = BiomeKeys.register("wooded_badlands_plateau");
    public static final RegistryKey<Biome> BADLANDS_PLATEAU = BiomeKeys.register("badlands_plateau");
    public static final RegistryKey<Biome> SMALL_END_ISLANDS = BiomeKeys.register("small_end_islands");
    public static final RegistryKey<Biome> END_MIDLANDS = BiomeKeys.register("end_midlands");
    public static final RegistryKey<Biome> END_HIGHLANDS = BiomeKeys.register("end_highlands");
    public static final RegistryKey<Biome> END_BARRENS = BiomeKeys.register("end_barrens");
    public static final RegistryKey<Biome> WARM_OCEAN = BiomeKeys.register("warm_ocean");
    public static final RegistryKey<Biome> LUKEWARM_OCEAN = BiomeKeys.register("lukewarm_ocean");
    public static final RegistryKey<Biome> COLD_OCEAN = BiomeKeys.register("cold_ocean");
    public static final RegistryKey<Biome> DEEP_WARM_OCEAN = BiomeKeys.register("deep_warm_ocean");
    public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = BiomeKeys.register("deep_lukewarm_ocean");
    public static final RegistryKey<Biome> DEEP_COLD_OCEAN = BiomeKeys.register("deep_cold_ocean");
    public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = BiomeKeys.register("deep_frozen_ocean");
    public static final RegistryKey<Biome> THE_VOID = BiomeKeys.register("the_void");
    public static final RegistryKey<Biome> SUNFLOWER_PLAINS = BiomeKeys.register("sunflower_plains");
    public static final RegistryKey<Biome> DESERT_LAKES = BiomeKeys.register("desert_lakes");
    public static final RegistryKey<Biome> GRAVELLY_MOUNTAINS = BiomeKeys.register("gravelly_mountains");
    public static final RegistryKey<Biome> FLOWER_FOREST = BiomeKeys.register("flower_forest");
    public static final RegistryKey<Biome> TAIGA_MOUNTAINS = BiomeKeys.register("taiga_mountains");
    public static final RegistryKey<Biome> SWAMP_HILLS = BiomeKeys.register("swamp_hills");
    public static final RegistryKey<Biome> ICE_SPIKES = BiomeKeys.register("ice_spikes");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE = BiomeKeys.register("modified_jungle");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE_EDGE = BiomeKeys.register("modified_jungle_edge");
    public static final RegistryKey<Biome> TALL_BIRCH_FOREST = BiomeKeys.register("tall_birch_forest");
    public static final RegistryKey<Biome> TALL_BIRCH_HILLS = BiomeKeys.register("tall_birch_hills");
    public static final RegistryKey<Biome> DARK_FOREST_HILLS = BiomeKeys.register("dark_forest_hills");
    public static final RegistryKey<Biome> SNOWY_TAIGA_MOUNTAINS = BiomeKeys.register("snowy_taiga_mountains");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA = BiomeKeys.register("giant_spruce_taiga");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA_HILLS = BiomeKeys.register("giant_spruce_taiga_hills");
    public static final RegistryKey<Biome> MODIFIED_GRAVELLY_MOUNTAINS = BiomeKeys.register("modified_gravelly_mountains");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA = BiomeKeys.register("shattered_savanna");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA_PLATEAU = BiomeKeys.register("shattered_savanna_plateau");
    public static final RegistryKey<Biome> ERODED_BADLANDS = BiomeKeys.register("eroded_badlands");
    public static final RegistryKey<Biome> MODIFIED_WOODED_BADLANDS_PLATEAU = BiomeKeys.register("modified_wooded_badlands_plateau");
    public static final RegistryKey<Biome> MODIFIED_BADLANDS_PLATEAU = BiomeKeys.register("modified_badlands_plateau");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE = BiomeKeys.register("bamboo_jungle");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE_HILLS = BiomeKeys.register("bamboo_jungle_hills");
    public static final RegistryKey<Biome> SOUL_SAND_VALLEY = BiomeKeys.register("soul_sand_valley");
    public static final RegistryKey<Biome> CRIMSON_FOREST = BiomeKeys.register("crimson_forest");
    public static final RegistryKey<Biome> WARPED_FOREST = BiomeKeys.register("warped_forest");
    public static final RegistryKey<Biome> BASALT_DELTAS = BiomeKeys.register("basalt_deltas");

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(name));
    }
}

