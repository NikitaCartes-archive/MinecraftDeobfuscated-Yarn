package net.minecraft.world.gen.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface ConfiguredStructureFeatureKeys {
	RegistryKey<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = of("pillager_outpost");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = of("mineshaft");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = of("mineshaft_mesa");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MANSION = of("mansion");
	RegistryKey<ConfiguredStructureFeature<?, ?>> JUNGLE_PYRAMID = of("jungle_pyramid");
	RegistryKey<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = of("desert_pyramid");
	RegistryKey<ConfiguredStructureFeature<?, ?>> IGLOO = of("igloo");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = of("shipwreck");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = of("shipwreck_beached");
	RegistryKey<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = of("swamp_hut");
	RegistryKey<ConfiguredStructureFeature<?, ?>> STRONGHOLD = of("stronghold");
	RegistryKey<ConfiguredStructureFeature<?, ?>> MONUMENT = of("monument");
	RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = of("ocean_ruin_cold");
	RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = of("ocean_ruin_warm");
	RegistryKey<ConfiguredStructureFeature<?, ?>> FORTRESS = of("fortress");
	RegistryKey<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = of("nether_fossil");
	RegistryKey<ConfiguredStructureFeature<?, ?>> END_CITY = of("end_city");
	RegistryKey<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = of("buried_treasure");
	RegistryKey<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = of("bastion_remnant");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = of("village_plains");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = of("village_desert");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = of("village_savanna");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = of("village_snowy");
	RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = of("village_taiga");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = of("ruined_portal");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = of("ruined_portal_desert");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = of("ruined_portal_jungle");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = of("ruined_portal_swamp");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = of("ruined_portal_mountain");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = of("ruined_portal_ocean");
	RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = of("ruined_portal_nether");

	private static RegistryKey<ConfiguredStructureFeature<?, ?>> of(String id) {
		return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
	}
}
