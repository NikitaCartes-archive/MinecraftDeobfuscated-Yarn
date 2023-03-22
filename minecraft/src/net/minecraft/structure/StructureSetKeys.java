package net.minecraft.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface StructureSetKeys {
	RegistryKey<StructureSet> VILLAGES = of("villages");
	RegistryKey<StructureSet> DESERT_PYRAMIDS = of("desert_pyramids");
	RegistryKey<StructureSet> IGLOOS = of("igloos");
	RegistryKey<StructureSet> JUNGLE_TEMPLES = of("jungle_temples");
	RegistryKey<StructureSet> SWAMP_HUTS = of("swamp_huts");
	RegistryKey<StructureSet> PILLAGER_OUTPOSTS = of("pillager_outposts");
	RegistryKey<StructureSet> OCEAN_MONUMENTS = of("ocean_monuments");
	RegistryKey<StructureSet> WOODLAND_MANSIONS = of("woodland_mansions");
	RegistryKey<StructureSet> BURIED_TREASURES = of("buried_treasures");
	RegistryKey<StructureSet> MINESHAFTS = of("mineshafts");
	RegistryKey<StructureSet> RUINED_PORTALS = of("ruined_portals");
	RegistryKey<StructureSet> SHIPWRECKS = of("shipwrecks");
	RegistryKey<StructureSet> OCEAN_RUINS = of("ocean_ruins");
	RegistryKey<StructureSet> NETHER_COMPLEXES = of("nether_complexes");
	RegistryKey<StructureSet> NETHER_FOSSILS = of("nether_fossils");
	RegistryKey<StructureSet> END_CITIES = of("end_cities");
	RegistryKey<StructureSet> ANCIENT_CITIES = of("ancient_cities");
	RegistryKey<StructureSet> STRONGHOLDS = of("strongholds");
	RegistryKey<StructureSet> TRAIL_RUINS = of("trail_ruins");

	private static RegistryKey<StructureSet> of(String id) {
		return RegistryKey.of(RegistryKeys.STRUCTURE_SET, new Identifier(id));
	}
}
