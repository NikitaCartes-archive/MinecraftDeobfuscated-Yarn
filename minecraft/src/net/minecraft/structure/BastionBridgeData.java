package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;

public class BastionBridgeData {
	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry = registryEntryLookup.getOrThrow(StructureProcessorLists.ENTRANCE_REPLACEMENT);
		RegistryEntry<StructureProcessorList> registryEntry2 = registryEntryLookup.getOrThrow(StructureProcessorLists.BASTION_GENERIC_DEGRADATION);
		RegistryEntry<StructureProcessorList> registryEntry3 = registryEntryLookup.getOrThrow(StructureProcessorLists.BRIDGE);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup.getOrThrow(StructureProcessorLists.RAMPART_DEGRADATION);
		RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry5 = registryEntryLookup2.getOrThrow(StructurePools.EMPTY);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/starting_pieces",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance", registryEntry), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/starting_pieces/entrance_face", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/bridge_pieces",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/bridge_pieces/bridge", registryEntry3), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/legs",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/legs/leg_0", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/legs/leg_1", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/walls",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/walls/wall_base_0", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/walls/wall_base_1", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/ramparts",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/ramparts/rampart_0", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/ramparts/rampart_1", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/rampart_plates",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/rampart_plates/plate_0", registryEntry4), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"bastion/bridge/connectors",
			new StructurePool(
				registryEntry5,
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/connectors/back_bridge_top", registryEntry2), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("bastion/bridge/connectors/back_bridge_bottom", registryEntry2), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
