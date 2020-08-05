/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public abstract class Biomes {
    public static final RegistryKey<Biome> OCEAN = Biomes.register("ocean");
    public static final RegistryKey<Biome> PLAINS = Biomes.register("plains");
    public static final RegistryKey<Biome> DESERT = Biomes.register("desert");
    public static final RegistryKey<Biome> MOUNTAINS = Biomes.register("mountains");
    public static final RegistryKey<Biome> FOREST = Biomes.register("forest");
    public static final RegistryKey<Biome> TAIGA = Biomes.register("taiga");
    public static final RegistryKey<Biome> SWAMP = Biomes.register("swamp");
    public static final RegistryKey<Biome> RIVER = Biomes.register("river");
    public static final RegistryKey<Biome> NETHER_WASTES = Biomes.register("nether_wastes");
    public static final RegistryKey<Biome> THE_END = Biomes.register("the_end");
    public static final RegistryKey<Biome> FROZEN_OCEAN = Biomes.register("frozen_ocean");
    public static final RegistryKey<Biome> FROZEN_RIVER = Biomes.register("frozen_river");
    public static final RegistryKey<Biome> SNOWY_TUNDRA = Biomes.register("snowy_tundra");
    public static final RegistryKey<Biome> SNOWY_MOUNTAINS = Biomes.register("snowy_mountains");
    public static final RegistryKey<Biome> MUSHROOM_FIELDS = Biomes.register("mushroom_fields");
    public static final RegistryKey<Biome> MUSHROOM_FIELD_SHORE = Biomes.register("mushroom_field_shore");
    public static final RegistryKey<Biome> BEACH = Biomes.register("beach");
    public static final RegistryKey<Biome> DESERT_HILLS = Biomes.register("desert_hills");
    public static final RegistryKey<Biome> WOODED_HILLS = Biomes.register("wooded_hills");
    public static final RegistryKey<Biome> TAIGA_HILLS = Biomes.register("taiga_hills");
    public static final RegistryKey<Biome> MOUNTAIN_EDGE = Biomes.register("mountain_edge");
    public static final RegistryKey<Biome> JUNGLE = Biomes.register("jungle");
    public static final RegistryKey<Biome> JUNGLE_HILLS = Biomes.register("jungle_hills");
    public static final RegistryKey<Biome> JUNGLE_EDGE = Biomes.register("jungle_edge");
    public static final RegistryKey<Biome> DEEP_OCEAN = Biomes.register("deep_ocean");
    public static final RegistryKey<Biome> STONE_SHORE = Biomes.register("stone_shore");
    public static final RegistryKey<Biome> SNOWY_BEACH = Biomes.register("snowy_beach");
    public static final RegistryKey<Biome> BIRCH_FOREST = Biomes.register("birch_forest");
    public static final RegistryKey<Biome> BIRCH_FOREST_HILLS = Biomes.register("birch_forest_hills");
    public static final RegistryKey<Biome> DARK_FOREST = Biomes.register("dark_forest");
    public static final RegistryKey<Biome> SNOWY_TAIGA = Biomes.register("snowy_taiga");
    public static final RegistryKey<Biome> SNOWY_TAIGA_HILLS = Biomes.register("snowy_taiga_hills");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA = Biomes.register("giant_tree_taiga");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA_HILLS = Biomes.register("giant_tree_taiga_hills");
    public static final RegistryKey<Biome> WOODED_MOUNTAINS = Biomes.register("wooded_mountains");
    public static final RegistryKey<Biome> SAVANNA = Biomes.register("savanna");
    public static final RegistryKey<Biome> SAVANNA_PLATEAU = Biomes.register("savanna_plateau");
    public static final RegistryKey<Biome> BADLANDS = Biomes.register("badlands");
    public static final RegistryKey<Biome> WOODED_BADLANDS_PLATEAU = Biomes.register("wooded_badlands_plateau");
    public static final RegistryKey<Biome> BADLANDS_PLATEAU = Biomes.register("badlands_plateau");
    public static final RegistryKey<Biome> SMALL_END_ISLANDS = Biomes.register("small_end_islands");
    public static final RegistryKey<Biome> END_MIDLANDS = Biomes.register("end_midlands");
    public static final RegistryKey<Biome> END_HIGHLANDS = Biomes.register("end_highlands");
    public static final RegistryKey<Biome> END_BARRENS = Biomes.register("end_barrens");
    public static final RegistryKey<Biome> WARM_OCEAN = Biomes.register("warm_ocean");
    public static final RegistryKey<Biome> LUKEWARM_OCEAN = Biomes.register("lukewarm_ocean");
    public static final RegistryKey<Biome> COLD_OCEAN = Biomes.register("cold_ocean");
    public static final RegistryKey<Biome> DEEP_WARM_OCEAN = Biomes.register("deep_warm_ocean");
    public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = Biomes.register("deep_lukewarm_ocean");
    public static final RegistryKey<Biome> DEEP_COLD_OCEAN = Biomes.register("deep_cold_ocean");
    public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = Biomes.register("deep_frozen_ocean");
    public static final RegistryKey<Biome> THE_VOID = Biomes.register("the_void");
    public static final RegistryKey<Biome> SUNFLOWER_PLAINS = Biomes.register("sunflower_plains");
    public static final RegistryKey<Biome> DESERT_LAKES = Biomes.register("desert_lakes");
    public static final RegistryKey<Biome> GRAVELLY_MOUNTAINS = Biomes.register("gravelly_mountains");
    public static final RegistryKey<Biome> FLOWER_FOREST = Biomes.register("flower_forest");
    public static final RegistryKey<Biome> TAIGA_MOUNTAINS = Biomes.register("taiga_mountains");
    public static final RegistryKey<Biome> SWAMP_HILLS = Biomes.register("swamp_hills");
    public static final RegistryKey<Biome> ICE_SPIKES = Biomes.register("ice_spikes");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE = Biomes.register("modified_jungle");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE_EDGE = Biomes.register("modified_jungle_edge");
    public static final RegistryKey<Biome> TALL_BIRCH_FOREST = Biomes.register("tall_birch_forest");
    public static final RegistryKey<Biome> TALL_BIRCH_HILLS = Biomes.register("tall_birch_hills");
    public static final RegistryKey<Biome> DARK_FOREST_HILLS = Biomes.register("dark_forest_hills");
    public static final RegistryKey<Biome> SNOWY_TAIGA_MOUNTAINS = Biomes.register("snowy_taiga_mountains");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA = Biomes.register("giant_spruce_taiga");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA_HILLS = Biomes.register("giant_spruce_taiga_hills");
    public static final RegistryKey<Biome> MODIFIED_GRAVELLY_MOUNTAINS = Biomes.register("modified_gravelly_mountains");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA = Biomes.register("shattered_savanna");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA_PLATEAU = Biomes.register("shattered_savanna_plateau");
    public static final RegistryKey<Biome> ERODED_BADLANDS = Biomes.register("eroded_badlands");
    public static final RegistryKey<Biome> MODIFIED_WOODED_BADLANDS_PLATEAU = Biomes.register("modified_wooded_badlands_plateau");
    public static final RegistryKey<Biome> MODIFIED_BADLANDS_PLATEAU = Biomes.register("modified_badlands_plateau");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE = Biomes.register("bamboo_jungle");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE_HILLS = Biomes.register("bamboo_jungle_hills");
    public static final RegistryKey<Biome> SOUL_SAND_VALLEY = Biomes.register("soul_sand_valley");
    public static final RegistryKey<Biome> CRIMSON_FOREST = Biomes.register("crimson_forest");
    public static final RegistryKey<Biome> WARPED_FOREST = Biomes.register("warped_forest");
    public static final RegistryKey<Biome> BASALT_DELTAS = Biomes.register("basalt_deltas");

    private static RegistryKey<Biome> register(String string) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(string));
    }
}

