/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public abstract class BuiltInBiomes {
    public static final RegistryKey<Biome> OCEAN = BuiltInBiomes.register("ocean");
    public static final RegistryKey<Biome> PLAINS = BuiltInBiomes.register("plains");
    public static final RegistryKey<Biome> DESERT = BuiltInBiomes.register("desert");
    public static final RegistryKey<Biome> MOUNTAINS = BuiltInBiomes.register("mountains");
    public static final RegistryKey<Biome> FOREST = BuiltInBiomes.register("forest");
    public static final RegistryKey<Biome> TAIGA = BuiltInBiomes.register("taiga");
    public static final RegistryKey<Biome> SWAMP = BuiltInBiomes.register("swamp");
    public static final RegistryKey<Biome> RIVER = BuiltInBiomes.register("river");
    public static final RegistryKey<Biome> NETHER_WASTES = BuiltInBiomes.register("nether_wastes");
    public static final RegistryKey<Biome> THE_END = BuiltInBiomes.register("the_end");
    public static final RegistryKey<Biome> FROZEN_OCEAN = BuiltInBiomes.register("frozen_ocean");
    public static final RegistryKey<Biome> FROZEN_RIVER = BuiltInBiomes.register("frozen_river");
    public static final RegistryKey<Biome> SNOWY_TUNDRA = BuiltInBiomes.register("snowy_tundra");
    public static final RegistryKey<Biome> SNOWY_MOUNTAINS = BuiltInBiomes.register("snowy_mountains");
    public static final RegistryKey<Biome> MUSHROOM_FIELDS = BuiltInBiomes.register("mushroom_fields");
    public static final RegistryKey<Biome> MUSHROOM_FIELD_SHORE = BuiltInBiomes.register("mushroom_field_shore");
    public static final RegistryKey<Biome> BEACH = BuiltInBiomes.register("beach");
    public static final RegistryKey<Biome> DESERT_HILLS = BuiltInBiomes.register("desert_hills");
    public static final RegistryKey<Biome> WOODED_HILLS = BuiltInBiomes.register("wooded_hills");
    public static final RegistryKey<Biome> TAIGA_HILLS = BuiltInBiomes.register("taiga_hills");
    public static final RegistryKey<Biome> MOUNTAIN_EDGE = BuiltInBiomes.register("mountain_edge");
    public static final RegistryKey<Biome> JUNGLE = BuiltInBiomes.register("jungle");
    public static final RegistryKey<Biome> JUNGLE_HILLS = BuiltInBiomes.register("jungle_hills");
    public static final RegistryKey<Biome> JUNGLE_EDGE = BuiltInBiomes.register("jungle_edge");
    public static final RegistryKey<Biome> DEEP_OCEAN = BuiltInBiomes.register("deep_ocean");
    public static final RegistryKey<Biome> STONE_SHORE = BuiltInBiomes.register("stone_shore");
    public static final RegistryKey<Biome> SNOWY_BEACH = BuiltInBiomes.register("snowy_beach");
    public static final RegistryKey<Biome> BIRCH_FOREST = BuiltInBiomes.register("birch_forest");
    public static final RegistryKey<Biome> BIRCH_FOREST_HILLS = BuiltInBiomes.register("birch_forest_hills");
    public static final RegistryKey<Biome> DARK_FOREST = BuiltInBiomes.register("dark_forest");
    public static final RegistryKey<Biome> SNOWY_TAIGA = BuiltInBiomes.register("snowy_taiga");
    public static final RegistryKey<Biome> SNOWY_TAIGA_HILLS = BuiltInBiomes.register("snowy_taiga_hills");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA = BuiltInBiomes.register("giant_tree_taiga");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA_HILLS = BuiltInBiomes.register("giant_tree_taiga_hills");
    public static final RegistryKey<Biome> WOODED_MOUNTAINS = BuiltInBiomes.register("wooded_mountains");
    public static final RegistryKey<Biome> SAVANNA = BuiltInBiomes.register("savanna");
    public static final RegistryKey<Biome> SAVANNA_PLATEAU = BuiltInBiomes.register("savanna_plateau");
    public static final RegistryKey<Biome> BADLANDS = BuiltInBiomes.register("badlands");
    public static final RegistryKey<Biome> WOODED_BADLANDS_PLATEAU = BuiltInBiomes.register("wooded_badlands_plateau");
    public static final RegistryKey<Biome> BADLANDS_PLATEAU = BuiltInBiomes.register("badlands_plateau");
    public static final RegistryKey<Biome> SMALL_END_ISLANDS = BuiltInBiomes.register("small_end_islands");
    public static final RegistryKey<Biome> END_MIDLANDS = BuiltInBiomes.register("end_midlands");
    public static final RegistryKey<Biome> END_HIGHLANDS = BuiltInBiomes.register("end_highlands");
    public static final RegistryKey<Biome> END_BARRENS = BuiltInBiomes.register("end_barrens");
    public static final RegistryKey<Biome> WARM_OCEAN = BuiltInBiomes.register("warm_ocean");
    public static final RegistryKey<Biome> LUKEWARM_OCEAN = BuiltInBiomes.register("lukewarm_ocean");
    public static final RegistryKey<Biome> COLD_OCEAN = BuiltInBiomes.register("cold_ocean");
    public static final RegistryKey<Biome> DEEP_WARM_OCEAN = BuiltInBiomes.register("deep_warm_ocean");
    public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = BuiltInBiomes.register("deep_lukewarm_ocean");
    public static final RegistryKey<Biome> DEEP_COLD_OCEAN = BuiltInBiomes.register("deep_cold_ocean");
    public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = BuiltInBiomes.register("deep_frozen_ocean");
    public static final RegistryKey<Biome> THE_VOID = BuiltInBiomes.register("the_void");
    public static final RegistryKey<Biome> SUNFLOWER_PLAINS = BuiltInBiomes.register("sunflower_plains");
    public static final RegistryKey<Biome> DESERT_LAKES = BuiltInBiomes.register("desert_lakes");
    public static final RegistryKey<Biome> GRAVELLY_MOUNTAINS = BuiltInBiomes.register("gravelly_mountains");
    public static final RegistryKey<Biome> FLOWER_FOREST = BuiltInBiomes.register("flower_forest");
    public static final RegistryKey<Biome> TAIGA_MOUNTAINS = BuiltInBiomes.register("taiga_mountains");
    public static final RegistryKey<Biome> SWAMP_HILLS = BuiltInBiomes.register("swamp_hills");
    public static final RegistryKey<Biome> ICE_SPIKES = BuiltInBiomes.register("ice_spikes");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE = BuiltInBiomes.register("modified_jungle");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE_EDGE = BuiltInBiomes.register("modified_jungle_edge");
    public static final RegistryKey<Biome> TALL_BIRCH_FOREST = BuiltInBiomes.register("tall_birch_forest");
    public static final RegistryKey<Biome> TALL_BIRCH_HILLS = BuiltInBiomes.register("tall_birch_hills");
    public static final RegistryKey<Biome> DARK_FOREST_HILLS = BuiltInBiomes.register("dark_forest_hills");
    public static final RegistryKey<Biome> SNOWY_TAIGA_MOUNTAINS = BuiltInBiomes.register("snowy_taiga_mountains");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA = BuiltInBiomes.register("giant_spruce_taiga");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA_HILLS = BuiltInBiomes.register("giant_spruce_taiga_hills");
    public static final RegistryKey<Biome> MODIFIED_GRAVELLY_MOUNTAINS = BuiltInBiomes.register("modified_gravelly_mountains");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA = BuiltInBiomes.register("shattered_savanna");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA_PLATEAU = BuiltInBiomes.register("shattered_savanna_plateau");
    public static final RegistryKey<Biome> ERODED_BADLANDS = BuiltInBiomes.register("eroded_badlands");
    public static final RegistryKey<Biome> MODIFIED_WOODED_BADLANDS_PLATEAU = BuiltInBiomes.register("modified_wooded_badlands_plateau");
    public static final RegistryKey<Biome> MODIFIED_BADLANDS_PLATEAU = BuiltInBiomes.register("modified_badlands_plateau");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE = BuiltInBiomes.register("bamboo_jungle");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE_HILLS = BuiltInBiomes.register("bamboo_jungle_hills");
    public static final RegistryKey<Biome> SOUL_SAND_VALLEY = BuiltInBiomes.register("soul_sand_valley");
    public static final RegistryKey<Biome> CRIMSON_FOREST = BuiltInBiomes.register("crimson_forest");
    public static final RegistryKey<Biome> WARPED_FOREST = BuiltInBiomes.register("warped_forest");
    public static final RegistryKey<Biome> BASALT_DELTAS = BuiltInBiomes.register("basalt_deltas");

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(name));
    }
}

