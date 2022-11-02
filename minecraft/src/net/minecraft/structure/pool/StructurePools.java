package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.AncientCityGenerator;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registerable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryKey;

public class StructurePools {
	public static final RegistryKey<StructurePool> EMPTY = of("empty");

	public static RegistryKey<StructurePool> of(String id) {
		return RegistryKey.of(Registry.STRUCTURE_POOL_KEY, new Identifier(id));
	}

	public static void register(Registerable<StructurePool> structurePoolsRegisterable, String id, StructurePool pool) {
		structurePoolsRegisterable.register(of(id), pool);
	}

	public static void bootstrap(Registerable<StructurePool> structurePoolsRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = structurePoolsRegisterable.getRegistryLookup(Registry.STRUCTURE_POOL_KEY);
		RegistryEntry<StructurePool> registryEntry = registryEntryLookup.getOrThrow(EMPTY);
		structurePoolsRegisterable.register(EMPTY, new StructurePool(registryEntry, ImmutableList.of(), StructurePool.Projection.RIGID));
		BastionRemnantGenerator.bootstrap(structurePoolsRegisterable);
		PillagerOutpostGenerator.bootstrap(structurePoolsRegisterable);
		VillageGenerator.bootstrap(structurePoolsRegisterable);
		AncientCityGenerator.bootstrap(structurePoolsRegisterable);
	}
}
