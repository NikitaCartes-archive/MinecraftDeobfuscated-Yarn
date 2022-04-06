package net.minecraft.world.gen.structure;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface StructureTypeKeys {
	RegistryKey<StructureType> PILLAGER_OUTPOST = of("pillager_outpost");
	RegistryKey<StructureType> MINESHAFT = of("mineshaft");
	RegistryKey<StructureType> MINESHAFT_MESA = of("mineshaft_mesa");
	RegistryKey<StructureType> MANSION = of("mansion");
	RegistryKey<StructureType> JUNGLE_PYRAMID = of("jungle_pyramid");
	RegistryKey<StructureType> DESERT_PYRAMID = of("desert_pyramid");
	RegistryKey<StructureType> IGLOO = of("igloo");
	RegistryKey<StructureType> SHIPWRECK = of("shipwreck");
	RegistryKey<StructureType> SHIPWRECK_BEACHED = of("shipwreck_beached");
	RegistryKey<StructureType> SWAMP_HUT = of("swamp_hut");
	RegistryKey<StructureType> STRONGHOLD = of("stronghold");
	RegistryKey<StructureType> MONUMENT = of("monument");
	RegistryKey<StructureType> OCEAN_RUIN_COLD = of("ocean_ruin_cold");
	RegistryKey<StructureType> OCEAN_RUIN_WARM = of("ocean_ruin_warm");
	RegistryKey<StructureType> FORTRESS = of("fortress");
	RegistryKey<StructureType> NETHER_FOSSIL = of("nether_fossil");
	RegistryKey<StructureType> END_CITY = of("end_city");
	RegistryKey<StructureType> BURIED_TREASURE = of("buried_treasure");
	RegistryKey<StructureType> BASTION_REMNANT = of("bastion_remnant");
	RegistryKey<StructureType> VILLAGE_PLAINS = of("village_plains");
	RegistryKey<StructureType> VILLAGE_DESERT = of("village_desert");
	RegistryKey<StructureType> VILLAGE_SAVANNA = of("village_savanna");
	RegistryKey<StructureType> VILLAGE_SNOWY = of("village_snowy");
	RegistryKey<StructureType> VILLAGE_TAIGA = of("village_taiga");
	RegistryKey<StructureType> RUINED_PORTAL = of("ruined_portal");
	RegistryKey<StructureType> RUINED_PORTAL_DESERT = of("ruined_portal_desert");
	RegistryKey<StructureType> RUINED_PORTAL_JUNGLE = of("ruined_portal_jungle");
	RegistryKey<StructureType> RUINED_PORTAL_SWAMP = of("ruined_portal_swamp");
	RegistryKey<StructureType> RUINED_PORTAL_MOUNTAIN = of("ruined_portal_mountain");
	RegistryKey<StructureType> RUINED_PORTAL_OCEAN = of("ruined_portal_ocean");
	RegistryKey<StructureType> RUINED_PORTAL_NETHER = of("ruined_portal_nether");
	RegistryKey<StructureType> ANCIENT_CITY = of("ancient_city");

	private static RegistryKey<StructureType> of(String id) {
		return RegistryKey.of(Registry.STRUCTURE_KEY, new Identifier(id));
	}
}
