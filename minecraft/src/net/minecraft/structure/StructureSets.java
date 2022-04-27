package net.minecraft.structure;

import java.util.List;
import java.util.Optional;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.StructureTypes;

public interface StructureSets {
	RegistryEntry<StructureSet> VILLAGES = register(
		StructureSetKeys.VILLAGES,
		new StructureSet(
			List.of(
				StructureSet.createEntry(StructureTypes.VILLAGE_PLAINS),
				StructureSet.createEntry(StructureTypes.VILLAGE_DESERT),
				StructureSet.createEntry(StructureTypes.VILLAGE_SAVANNA),
				StructureSet.createEntry(StructureTypes.VILLAGE_SNOWY),
				StructureSet.createEntry(StructureTypes.VILLAGE_TAIGA)
			),
			new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312)
		)
	);
	RegistryEntry<StructureSet> DESERT_PYRAMIDS = register(
		StructureSetKeys.DESERT_PYRAMIDS, StructureTypes.DESERT_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617)
	);
	RegistryEntry<StructureSet> IGLOOS = register(
		StructureSetKeys.IGLOOS, StructureTypes.IGLOO, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618)
	);
	RegistryEntry<StructureSet> JUNGLE_TEMPLES = register(
		StructureSetKeys.JUNGLE_TEMPLES, StructureTypes.JUNGLE_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619)
	);
	RegistryEntry<StructureSet> SWAMP_HUTS = register(
		StructureSetKeys.SWAMP_HUTS, StructureTypes.SWAMP_HUT, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620)
	);
	RegistryEntry<StructureSet> PILLAGER_OUTPOSTS = register(
		StructureSetKeys.PILLAGER_OUTPOSTS,
		StructureTypes.PILLAGER_OUTPOST,
		new RandomSpreadStructurePlacement(
			Vec3i.ZERO,
			StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
			0.2F,
			165745296,
			Optional.of(new StructurePlacement.ExclusionZone(VILLAGES, 10)),
			32,
			8,
			SpreadType.LINEAR
		)
	);
	RegistryEntry<StructureSet> ANCIENT_CITIES = register(
		StructureSetKeys.ANCIENT_CITIES, StructureTypes.ANCIENT_CITY, new RandomSpreadStructurePlacement(24, 8, SpreadType.LINEAR, 20083232)
	);
	RegistryEntry<StructureSet> OCEAN_MONUMENTS = register(
		StructureSetKeys.OCEAN_MONUMENTS, StructureTypes.MONUMENT, new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313)
	);
	RegistryEntry<StructureSet> WOODLAND_MANSIONS = register(
		StructureSetKeys.WOODLAND_MANSIONS, StructureTypes.MANSION, new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319)
	);
	RegistryEntry<StructureSet> BURIED_TREASURES = register(
		StructureSetKeys.BURIED_TREASURES,
		StructureTypes.BURIED_TREASURE,
		new RandomSpreadStructurePlacement(
			new Vec3i(9, 0, 9), StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_2, 0.01F, 0, Optional.empty(), 1, 0, SpreadType.LINEAR
		)
	);
	RegistryEntry<StructureSet> MINESHAFTS = register(
		StructureSetKeys.MINESHAFTS,
		new StructureSet(
			List.of(StructureSet.createEntry(StructureTypes.MINESHAFT), StructureSet.createEntry(StructureTypes.MINESHAFT_MESA)),
			new RandomSpreadStructurePlacement(
				Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004F, 0, Optional.empty(), 1, 0, SpreadType.LINEAR
			)
		)
	);
	RegistryEntry<StructureSet> RUINED_PORTALS = register(
		StructureSetKeys.RUINED_PORTALS,
		new StructureSet(
			List.of(
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_DESERT),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_JUNGLE),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_SWAMP),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_MOUNTAIN),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_OCEAN),
				StructureSet.createEntry(StructureTypes.RUINED_PORTAL_NETHER)
			),
			new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645)
		)
	);
	RegistryEntry<StructureSet> SHIPWRECKS = register(
		StructureSetKeys.SHIPWRECKS,
		new StructureSet(
			List.of(StructureSet.createEntry(StructureTypes.SHIPWRECK), StructureSet.createEntry(StructureTypes.SHIPWRECK_BEACHED)),
			new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295)
		)
	);
	RegistryEntry<StructureSet> OCEAN_RUINS = register(
		StructureSetKeys.OCEAN_RUINS,
		new StructureSet(
			List.of(StructureSet.createEntry(StructureTypes.OCEAN_RUIN_COLD), StructureSet.createEntry(StructureTypes.OCEAN_RUIN_WARM)),
			new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621)
		)
	);
	RegistryEntry<StructureSet> NETHER_COMPLEXES = register(
		StructureSetKeys.NETHER_COMPLEXES,
		new StructureSet(
			List.of(StructureSet.createEntry(StructureTypes.FORTRESS, 2), StructureSet.createEntry(StructureTypes.BASTION_REMNANT, 3)),
			new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232)
		)
	);
	RegistryEntry<StructureSet> NETHER_FOSSILS = register(
		StructureSetKeys.NETHER_FOSSILS, StructureTypes.NETHER_FOSSIL, new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921)
	);
	RegistryEntry<StructureSet> END_CITIES = register(
		StructureSetKeys.END_CITIES, StructureTypes.END_CITY, new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313)
	);
	RegistryEntry<StructureSet> STRONGHOLDS = register(
		StructureSetKeys.STRONGHOLDS,
		StructureTypes.STRONGHOLD,
		new ConcentricRingsStructurePlacement(32, 3, 128, BuiltinRegistries.BIOME.getOrCreateEntryList(BiomeTags.STRONGHOLD_BIASED_TO))
	);

	static RegistryEntry<StructureSet> initAndGetDefault() {
		return (RegistryEntry<StructureSet>)BuiltinRegistries.STRUCTURE_SET.streamEntries().iterator().next();
	}

	static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, StructureSet structureSet) {
		return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_SET, key, structureSet);
	}

	static RegistryEntry<StructureSet> register(
		RegistryKey<StructureSet> key, RegistryEntry<net.minecraft.world.gen.structure.StructureType> configuredStructureFigure, StructurePlacement placement
	) {
		return register(key, new StructureSet(configuredStructureFigure, placement));
	}
}
