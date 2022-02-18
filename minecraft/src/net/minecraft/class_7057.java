package net.minecraft;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface class_7057 {
	RegistryKey<class_7059> VILLAGES = method_41141("villages");
	RegistryKey<class_7059> DESERT_PYRAMIDS = method_41141("desert_pyramids");
	RegistryKey<class_7059> IGLOOS = method_41141("igloos");
	RegistryKey<class_7059> JUNGLE_TEMPLES = method_41141("jungle_temples");
	RegistryKey<class_7059> SWAMP_HUTS = method_41141("swamp_huts");
	RegistryKey<class_7059> PILLAGER_OUTPOSTS = method_41141("pillager_outposts");
	RegistryKey<class_7059> OCEAN_MONUMENTS = method_41141("ocean_monuments");
	RegistryKey<class_7059> WOODLAND_MANSIONS = method_41141("woodland_mansions");
	RegistryKey<class_7059> BURIED_TREASURES = method_41141("buried_treasures");
	RegistryKey<class_7059> MINESHAFTS = method_41141("mineshafts");
	RegistryKey<class_7059> RUINED_PORTALS = method_41141("ruined_portals");
	RegistryKey<class_7059> SHIPWRECKS = method_41141("shipwrecks");
	RegistryKey<class_7059> OCEAN_RUINS = method_41141("ocean_ruins");
	RegistryKey<class_7059> NETHER_COMPLEXES = method_41141("nether_complexes");
	RegistryKey<class_7059> NETHER_FOSSILS = method_41141("nether_fossils");
	RegistryKey<class_7059> END_CITIES = method_41141("end_cities");
	RegistryKey<class_7059> STRONGHOLDS = method_41141("strongholds");

	private static RegistryKey<class_7059> method_41141(String string) {
		return RegistryKey.of(Registry.STRUCTURE_SET_WORLDGEN, new Identifier(string));
	}
}
