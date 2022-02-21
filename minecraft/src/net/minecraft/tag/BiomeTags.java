package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeTags {
	public static final TagKey<Biome> IS_DEEP_OCEAN = of("is_deep_ocean");
	public static final TagKey<Biome> IS_OCEAN = of("is_ocean");
	public static final TagKey<Biome> IS_BEACH = of("is_beach");
	public static final TagKey<Biome> IS_RIVER = of("is_river");
	public static final TagKey<Biome> IS_MOUNTAIN = of("is_mountain");
	public static final TagKey<Biome> IS_BADLANDS = of("is_badlands");
	public static final TagKey<Biome> IS_HILL = of("is_hill");
	public static final TagKey<Biome> IS_TAIGA = of("is_taiga");
	public static final TagKey<Biome> IS_JUNGLE = of("is_jungle");
	public static final TagKey<Biome> IS_FOREST = of("is_forest");
	public static final TagKey<Biome> IS_NETHER = of("is_nether");
	public static final TagKey<Biome> BURIED_TREASURE_HAS_STRUCTURE = of("has_structure/buried_treasure");
	public static final TagKey<Biome> DESERT_PYRAMID_HAS_STRUCTURE = of("has_structure/desert_pyramid");
	public static final TagKey<Biome> IGLOO_HAS_STRUCTURE = of("has_structure/igloo");
	public static final TagKey<Biome> JUNGLE_TEMPLE_HAS_STRUCTURE = of("has_structure/jungle_temple");
	public static final TagKey<Biome> MINESHAFT_HAS_STRUCTURE = of("has_structure/mineshaft");
	public static final TagKey<Biome> MINESHAFT_MESA_HAS_STRUCTURE = of("has_structure/mineshaft_mesa");
	public static final TagKey<Biome> OCEAN_MONUMENT_HAS_STRUCTURE = of("has_structure/ocean_monument");
	public static final TagKey<Biome> OCEAN_RUIN_COLD_HAS_STRUCTURE = of("has_structure/ocean_ruin_cold");
	public static final TagKey<Biome> OCEAN_RUIN_WARM_HAS_STRUCTURE = of("has_structure/ocean_ruin_warm");
	public static final TagKey<Biome> PILLAGER_OUTPOST_HAS_STRUCTURE = of("has_structure/pillager_outpost");
	public static final TagKey<Biome> RUINED_PORTAL_DESERT_HAS_STRUCTURE = of("has_structure/ruined_portal_desert");
	public static final TagKey<Biome> RUINED_PORTAL_JUNGLE_HAS_STRUCTURE = of("has_structure/ruined_portal_jungle");
	public static final TagKey<Biome> RUINED_PORTAL_OCEAN_HAS_STRUCTURE = of("has_structure/ruined_portal_ocean");
	public static final TagKey<Biome> RUINED_PORTAL_SWAMP_HAS_STRUCTURE = of("has_structure/ruined_portal_swamp");
	public static final TagKey<Biome> RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE = of("has_structure/ruined_portal_mountain");
	public static final TagKey<Biome> RUINED_PORTAL_STANDARD_HAS_STRUCTURE = of("has_structure/ruined_portal_standard");
	public static final TagKey<Biome> SHIPWRECK_BEACHED_HAS_STRUCTURE = of("has_structure/shipwreck_beached");
	public static final TagKey<Biome> SHIPWRECK_HAS_STRUCTURE = of("has_structure/shipwreck");
	public static final TagKey<Biome> SWAMP_HUT_HAS_STRUCTURE = of("has_structure/swamp_hut");
	public static final TagKey<Biome> VILLAGE_DESERT_HAS_STRUCTURE = of("has_structure/village_desert");
	public static final TagKey<Biome> VILLAGE_PLAINS_HAS_STRUCTURE = of("has_structure/village_plains");
	public static final TagKey<Biome> VILLAGE_SAVANNA_HAS_STRUCTURE = of("has_structure/village_savanna");
	public static final TagKey<Biome> VILLAGE_SNOWY_HAS_STRUCTURE = of("has_structure/village_snowy");
	public static final TagKey<Biome> VILLAGE_TAIGA_HAS_STRUCTURE = of("has_structure/village_taiga");
	public static final TagKey<Biome> WOODLAND_MANSION_HAS_STRUCTURE = of("has_structure/woodland_mansion");
	public static final TagKey<Biome> STRONGHOLD_HAS_STRUCTURE = of("has_structure/stronghold");
	public static final TagKey<Biome> NETHER_FORTRESS_HAS_STRUCTURE = of("has_structure/nether_fortress");
	public static final TagKey<Biome> NETHER_FOSSIL_HAS_STRUCTURE = of("has_structure/nether_fossil");
	public static final TagKey<Biome> BASTION_REMNANT_HAS_STRUCTURE = of("has_structure/bastion_remnant");
	public static final TagKey<Biome> RUINED_PORTAL_NETHER_HAS_STRUCTURE = of("has_structure/ruined_portal_nether");
	public static final TagKey<Biome> END_CITY_HAS_STRUCTURE = of("has_structure/end_city");

	private BiomeTags() {
	}

	private static TagKey<Biome> of(String id) {
		return TagKey.of(Registry.BIOME_KEY, new Identifier(id));
	}
}
