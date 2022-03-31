package net.minecraft.world.gen.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface ConfiguredStructureFeatureKeys {
	RegistryKey<StructureFeature> PILLAGER_OUTPOST = of("pillager_outpost");
	RegistryKey<StructureFeature> MINESHAFT = of("mineshaft");
	RegistryKey<StructureFeature> MINESHAFT_MESA = of("mineshaft_mesa");
	RegistryKey<StructureFeature> MANSION = of("mansion");
	RegistryKey<StructureFeature> JUNGLE_PYRAMID = of("jungle_pyramid");
	RegistryKey<StructureFeature> DESERT_PYRAMID = of("desert_pyramid");
	RegistryKey<StructureFeature> IGLOO = of("igloo");
	RegistryKey<StructureFeature> SHIPWRECK = of("shipwreck");
	RegistryKey<StructureFeature> SHIPWRECK_BEACHED = of("shipwreck_beached");
	RegistryKey<StructureFeature> SWAMP_HUT = of("swamp_hut");
	RegistryKey<StructureFeature> STRONGHOLD = of("stronghold");
	RegistryKey<StructureFeature> MONUMENT = of("monument");
	RegistryKey<StructureFeature> OCEAN_RUIN_COLD = of("ocean_ruin_cold");
	RegistryKey<StructureFeature> OCEAN_RUIN_WARM = of("ocean_ruin_warm");
	RegistryKey<StructureFeature> FORTRESS = of("fortress");
	RegistryKey<StructureFeature> NETHER_FOSSIL = of("nether_fossil");
	RegistryKey<StructureFeature> END_CITY = of("end_city");
	RegistryKey<StructureFeature> BURIED_TREASURE = of("buried_treasure");
	RegistryKey<StructureFeature> BASTION_REMNANT = of("bastion_remnant");
	RegistryKey<StructureFeature> VILLAGE_PLAINS = of("village_plains");
	RegistryKey<StructureFeature> VILLAGE_DESERT = of("village_desert");
	RegistryKey<StructureFeature> VILLAGE_SAVANNA = of("village_savanna");
	RegistryKey<StructureFeature> VILLAGE_SNOWY = of("village_snowy");
	RegistryKey<StructureFeature> VILLAGE_TAIGA = of("village_taiga");
	RegistryKey<StructureFeature> RUINED_PORTAL = of("ruined_portal");
	RegistryKey<StructureFeature> RUINED_PORTAL_DESERT = of("ruined_portal_desert");
	RegistryKey<StructureFeature> RUINED_PORTAL_JUNGLE = of("ruined_portal_jungle");
	RegistryKey<StructureFeature> RUINED_PORTAL_SWAMP = of("ruined_portal_swamp");
	RegistryKey<StructureFeature> RUINED_PORTAL_MOUNTAIN = of("ruined_portal_mountain");
	RegistryKey<StructureFeature> RUINED_PORTAL_OCEAN = of("ruined_portal_ocean");
	RegistryKey<StructureFeature> RUINED_PORTAL_NETHER = of("ruined_portal_nether");
	RegistryKey<StructureFeature> ANCIENT_CITY = of("ancient_city");

	private static RegistryKey<StructureFeature> of(String id) {
		return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
	}
}
