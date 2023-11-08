package net.minecraft.structure;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

public interface OneTwentyOneStructureSets {
	static void bootstrap(Registerable<StructureSet> structureSetRegisterable) {
		RegistryEntryLookup<Structure> registryEntryLookup = structureSetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE);
		structureSetRegisterable.register(
			StructureSetKeys.TRIAL_CHAMBERS,
			new StructureSet(registryEntryLookup.getOrThrow(StructureKeys.TRIAL_CHAMBERS), new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 94251327))
		);
	}
}
