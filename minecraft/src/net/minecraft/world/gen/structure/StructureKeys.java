package net.minecraft.world.gen.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface StructureKeys {
	RegistryKey<Structure> PILLAGER_OUTPOST = of("pillager_outpost");
	RegistryKey<Structure> MINESHAFT = of("mineshaft");
	RegistryKey<Structure> MINESHAFT_MESA = of("mineshaft_mesa");
	RegistryKey<Structure> MANSION = of("mansion");
	RegistryKey<Structure> JUNGLE_PYRAMID = of("jungle_pyramid");
	RegistryKey<Structure> DESERT_PYRAMID = of("desert_pyramid");
	RegistryKey<Structure> IGLOO = of("igloo");
	RegistryKey<Structure> SHIPWRECK = of("shipwreck");
	RegistryKey<Structure> SHIPWRECK_BEACHED = of("shipwreck_beached");
	RegistryKey<Structure> SWAMP_HUT = of("swamp_hut");
	RegistryKey<Structure> STRONGHOLD = of("stronghold");
	RegistryKey<Structure> MONUMENT = of("monument");
	RegistryKey<Structure> OCEAN_RUIN_COLD = of("ocean_ruin_cold");
	RegistryKey<Structure> OCEAN_RUIN_WARM = of("ocean_ruin_warm");
	RegistryKey<Structure> FORTRESS = of("fortress");
	RegistryKey<Structure> NETHER_FOSSIL = of("nether_fossil");
	RegistryKey<Structure> END_CITY = of("end_city");
	RegistryKey<Structure> BURIED_TREASURE = of("buried_treasure");
	RegistryKey<Structure> BASTION_REMNANT = of("bastion_remnant");
	RegistryKey<Structure> VILLAGE_PLAINS = of("village_plains");
	RegistryKey<Structure> VILLAGE_DESERT = of("village_desert");
	RegistryKey<Structure> VILLAGE_SAVANNA = of("village_savanna");
	RegistryKey<Structure> VILLAGE_SNOWY = of("village_snowy");
	RegistryKey<Structure> VILLAGE_TAIGA = of("village_taiga");
	RegistryKey<Structure> RUINED_PORTAL = of("ruined_portal");
	RegistryKey<Structure> RUINED_PORTAL_DESERT = of("ruined_portal_desert");
	RegistryKey<Structure> RUINED_PORTAL_JUNGLE = of("ruined_portal_jungle");
	RegistryKey<Structure> RUINED_PORTAL_SWAMP = of("ruined_portal_swamp");
	RegistryKey<Structure> RUINED_PORTAL_MOUNTAIN = of("ruined_portal_mountain");
	RegistryKey<Structure> RUINED_PORTAL_OCEAN = of("ruined_portal_ocean");
	RegistryKey<Structure> RUINED_PORTAL_NETHER = of("ruined_portal_nether");
	RegistryKey<Structure> ANCIENT_CITY = of("ancient_city");
	RegistryKey<Structure> TRAIL_RUINS = of("trail_ruins");

	private static RegistryKey<Structure> of(String id) {
		return RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(id));
	}
}
