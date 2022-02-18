package net.minecraft;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface class_7058 {
	RegistryKey<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = method_41142("pillager_outpost");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = method_41142("mineshaft");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = method_41142("mineshaft_mesa");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MANSION = method_41142("mansion");
	RegistryKey<ConfiguredStructureFeature<?, ?>> JUNGLE_PYRAMID = method_41142("jungle_pyramid");
	RegistryKey<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = method_41142("desert_pyramid");
	RegistryKey<ConfiguredStructureFeature<?, ?>> IGLOO = method_41142("igloo");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = method_41142("shipwreck");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = method_41142("shipwreck_beached");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = method_41142("swamp_hut");
	RegistryKey<ConfiguredStructureFeature<?, ?>> STRONGHOLD = method_41142("stronghold");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MONUMENT = method_41142("monument");
	RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = method_41142("ocean_ruin_cold");
	RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = method_41142("ocean_ruin_warm");
	RegistryKey<ConfiguredStructureFeature<?, ?>> FORTRESS = method_41142("fortress");
	RegistryKey<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = method_41142("nether_fossil");
	RegistryKey<ConfiguredStructureFeature<?, ?>> END_CITY = method_41142("end_city");
	RegistryKey<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = method_41142("buried_treasure");
	RegistryKey<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = method_41142("bastion_remnant");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = method_41142("village_plains");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = method_41142("village_desert");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = method_41142("village_savanna");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = method_41142("village_snowy");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = method_41142("village_taiga");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = method_41142("ruined_portal");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = method_41142("ruined_portal_desert");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = method_41142("ruined_portal_jungle");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = method_41142("ruined_portal_swamp");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = method_41142("ruined_portal_mountain");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = method_41142("ruined_portal_ocean");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = method_41142("ruined_portal_nether");

	private static RegistryKey<ConfiguredStructureFeature<?, ?>> method_41142(String string) {
		return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(string));
	}
}
