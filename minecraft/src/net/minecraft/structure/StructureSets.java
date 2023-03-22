package net.minecraft.structure;

import java.util.List;
import java.util.Optional;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public interface StructureSets {
	static void bootstrap(Registerable<StructureSet> structureSetRegisterable) {
		RegistryEntryLookup<Structure> registryEntryLookup = structureSetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE);
		RegistryEntryLookup<Biome> registryEntryLookup2 = structureSetRegisterable.getRegistryLookup(RegistryKeys.BIOME);
		RegistryEntry.Reference<StructureSet> reference = structureSetRegisterable.register(
			StructureSetKeys.VILLAGES,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.VILLAGE_PLAINS)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.VILLAGE_DESERT)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.VILLAGE_SAVANNA)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.VILLAGE_SNOWY)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.VILLAGE_TAIGA))
				),
				new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.DESERT_PYRAMIDS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.DESERT_PYRAMID), new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617))
		);
		structureSetRegisterable.register(
			StructureSetKeys.IGLOOS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.IGLOO), new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618))
		);
		structureSetRegisterable.register(
			StructureSetKeys.JUNGLE_TEMPLES,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.JUNGLE_PYRAMID), new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619))
		);
		structureSetRegisterable.register(
			StructureSetKeys.SWAMP_HUTS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.SWAMP_HUT), new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620))
		);
		structureSetRegisterable.register(
			StructureSetKeys.PILLAGER_OUTPOSTS,
			new StructureSet(
				registryEntryLookup.getOrThrow(StructureKeys.PILLAGER_OUTPOST),
				new RandomSpreadStructurePlacement(
					Vec3i.ZERO,
					StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
					0.2F,
					165745296,
					Optional.of(new StructurePlacement.ExclusionZone(reference, 10)),
					32,
					8,
					SpreadType.LINEAR
				)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.ANCIENT_CITIES,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.ANCIENT_CITY), new RandomSpreadStructurePlacement(24, 8, SpreadType.LINEAR, 20083232))
		);
		structureSetRegisterable.register(
			StructureSetKeys.OCEAN_MONUMENTS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.MONUMENT), new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313))
		);
		structureSetRegisterable.register(
			StructureSetKeys.WOODLAND_MANSIONS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.MANSION), new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319))
		);
		structureSetRegisterable.register(
			StructureSetKeys.BURIED_TREASURES,
			new StructureSet(
				registryEntryLookup.getOrThrow(StructureKeys.BURIED_TREASURE),
				new RandomSpreadStructurePlacement(
					new Vec3i(9, 0, 9), StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_2, 0.01F, 0, Optional.empty(), 1, 0, SpreadType.LINEAR
				)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.MINESHAFTS,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.MINESHAFT)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.MINESHAFT_MESA))
				),
				new RandomSpreadStructurePlacement(
					Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004F, 0, Optional.empty(), 1, 0, SpreadType.LINEAR
				)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.RUINED_PORTALS,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_DESERT)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_JUNGLE)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_SWAMP)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_MOUNTAIN)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_OCEAN)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.RUINED_PORTAL_NETHER))
				),
				new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.SHIPWRECKS,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.SHIPWRECK)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.SHIPWRECK_BEACHED))
				),
				new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.OCEAN_RUINS,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.OCEAN_RUIN_COLD)),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.OCEAN_RUIN_WARM))
				),
				new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.NETHER_COMPLEXES,
			new StructureSet(
				List.of(
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.FORTRESS), 2),
					StructureSet.createEntry(registryEntryLookup.getOrThrow(StructureKeys.BASTION_REMNANT), 3)
				),
				new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232)
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.NETHER_FOSSILS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.NETHER_FOSSIL), new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921))
		);
		structureSetRegisterable.register(
			StructureSetKeys.END_CITIES,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.END_CITY), new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313))
		);
		structureSetRegisterable.register(
			StructureSetKeys.STRONGHOLDS,
			new StructureSet(
				registryEntryLookup.getOrThrow(StructureKeys.STRONGHOLD),
				new ConcentricRingsStructurePlacement(32, 3, 128, registryEntryLookup2.getOrThrow(BiomeTags.STRONGHOLD_BIASED_TO))
			)
		);
		structureSetRegisterable.register(
			StructureSetKeys.TRAIL_RUINS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.TRAIL_RUINS), new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 83469867))
		);
	}
}
