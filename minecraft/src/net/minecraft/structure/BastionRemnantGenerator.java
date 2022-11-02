package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.registry.Registerable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryKey;

public class BastionRemnantGenerator {
	public static final RegistryKey<StructurePool> STRUCTURE_POOLS = StructurePools.of("bastion/starts");

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(Registry.STRUCTURE_PROCESSOR_LIST_KEY);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.BASTION_GENERIC_DEGRADATION);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(Registry.STRUCTURE_POOL_KEY);
		RegistryEntry<StructurePool> registryEntry2 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		poolRegisterable.register(
			STRUCTURE_POOLS,
			new StructurePool(
				registryEntry2,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/units/air_base", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/hoglin_stable/air_base", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/treasure/big_air_full", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance_base", registryEntry), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		BastionUnitsData.bootstrap(poolRegisterable);
		BastionHoglinStableData.bootstrap(poolRegisterable);
		BastionTreasureData.bootstrap(poolRegisterable);
		BastionBridgeData.bootstrap(poolRegisterable);
		BastionData.bootstrap(poolRegisterable);
	}
}
