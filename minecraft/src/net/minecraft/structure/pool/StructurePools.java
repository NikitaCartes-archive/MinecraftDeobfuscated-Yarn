package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.AncientCityGenerator;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.TrailRuinsGenerator;
import net.minecraft.structure.TrialChamberData;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.util.Identifier;

public class StructurePools {
	public static final RegistryKey<StructurePool> EMPTY = ofVanilla("empty");

	public static RegistryKey<StructurePool> of(Identifier id) {
		return RegistryKey.of(RegistryKeys.TEMPLATE_POOL, id);
	}

	public static RegistryKey<StructurePool> ofVanilla(String id) {
		return of(Identifier.ofVanilla(id));
	}

	public static RegistryKey<StructurePool> of(String id) {
		return of(Identifier.of(id));
	}

	public static void register(Registerable<StructurePool> structurePoolsRegisterable, String id, StructurePool pool) {
		structurePoolsRegisterable.register(ofVanilla(id), pool);
	}

	public static void bootstrap(Registerable<StructurePool> structurePoolsRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = structurePoolsRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry = registryEntryLookup.getOrThrow(EMPTY);
		structurePoolsRegisterable.register(EMPTY, new StructurePool(registryEntry, ImmutableList.of(), StructurePool.Projection.RIGID));
		BastionRemnantGenerator.bootstrap(structurePoolsRegisterable);
		PillagerOutpostGenerator.bootstrap(structurePoolsRegisterable);
		VillageGenerator.bootstrap(structurePoolsRegisterable);
		AncientCityGenerator.bootstrap(structurePoolsRegisterable);
		TrailRuinsGenerator.bootstrap(structurePoolsRegisterable);
		TrialChamberData.bootstrap(structurePoolsRegisterable);
	}
}
